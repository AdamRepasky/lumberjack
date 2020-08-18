package com.adam.lumberjack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jdk.nashorn.internal.objects.Global.Infinity;

public class Mob extends GameObject implements Movable {
    private final MobType mobType;
    private int dmg;
    private float speed;
    private float maxSpeed;
    private float minSpeed;
    protected boolean moving;
    protected int wood;
    private Vector3 destination;
    protected Vector2 moveVector = new Vector2(0, 0);
    protected TextureRegion dead;
    protected Animation<TextureRegion> idle;
    protected Animation<TextureRegion> attack;
    protected Animation<TextureRegion> block;
    protected List<GameObject> gameObjectsInRange = new ArrayList<GameObject>();
    protected int facing = 0;
    //fight attributes
    protected boolean attacking = false;
    protected int attackCooldown = 4;
    protected int currentAttackCooldown = 0;
    private boolean blocking = false;
    private int blockCooldown = 200;
    private int currentBlockCooldown = 0;
    protected Body sensorBody;

    protected List<Animation<TextureRegion>> walkAnimations = new ArrayList<Animation<TextureRegion>>();
    protected float stateTime = 0f;

    Mob(World world, MobType mobType, float x, float y, int maxHealth, int dmg, float acceleration, float maxSpeed, Texture textures, int cols, int rows,
        float widthRatio, float heightRatio, Hitbox hitbox, Texture idle, int idleCols, Texture attack, int attackCols, Texture block, int blockCols, TextureRegion texDead, Chunk ownerChunk) {
        super(textures, cols, rows, widthRatio, heightRatio, world, hitbox, maxHealth, x, y, ownerChunk);
        this.mobType = mobType;
        this.dead = texDead;
        this.dmg = dmg;
        this.speed = maxSpeed;
        this.moving = false;
        this.destination = new Vector3();
        this.maxSpeed = maxSpeed;
        this.minSpeed = maxSpeed / 2;
        //addSensorFixture();
        this.idle = new Animation<>(0.4f, TextureRegion.split(idle, idle.getWidth() / idleCols, idle.getHeight())[0]);
        this.attack = new Animation<>(0.025f, TextureRegion.split(attack, attack.getWidth() / attackCols, attack.getHeight())[0]);
        this.block = new Animation<>(0.4f, TextureRegion.split(block, block.getWidth() / blockCols, block.getHeight())[0]);

        walkAnimations.add(new Animation<>(0.1f, this.regions[0]));
        if (this.regions.length == 2) {
            walkAnimations.add(new Animation<>(0.1f, this.regions[1]));
        } else {
            walkAnimations.add(new Animation<>(0.1f, this.regions[0]));
        }
    }
    Mob(World world, MobType mobType, float x, float y, int maxHealth, int dmg, float acceleration, float maxSpeed, Texture textures, int cols, int rows,
        float widthRatio, float heightRatio, Hitbox hitbox, Texture idle, int idleCols, Texture attack, int attackCols, TextureRegion texDead, Chunk ownerChunk) {
        super(textures, cols, rows, widthRatio, heightRatio, world, hitbox, maxHealth, x, y, ownerChunk);
        this.mobType = mobType;
        this.dead = texDead;
        this.dmg = dmg;
        this.speed = maxSpeed;
        this.moving = false;
        this.destination = new Vector3();
        this.maxSpeed = maxSpeed;
        this.minSpeed = maxSpeed / 2;
        //addSensorFixture();
        this.idle = new Animation<>(0.4f, TextureRegion.split(idle, idle.getWidth() / idleCols, idle.getHeight())[0]);
        this.attack = new Animation<>(0.025f, TextureRegion.split(attack, attack.getWidth() / attackCols, attack.getHeight())[0]);
        this.block  = null;

        walkAnimations.add(new Animation<>(0.1f, this.regions[0]));
        if (this.regions.length == 2) {
            walkAnimations.add(new Animation<>(0.1f, this.regions[1]));
        } else {
            walkAnimations.add(new Animation<>(0.1f, this.regions[0]));
        }
    }
    public MobType getMobType(){
        return mobType;
    }
    public void setBlocking(boolean blocking) {
        blocking = blocking;
    }

    public void startBlocking() {
        blocking = true;
        speed = minSpeed;
    }

    public void stopBlocking() {
        blocking = false;
        speed = maxSpeed;
    }

    public boolean getBlocking() {
        return blocking;
    }
    public boolean blockOnCooldown() {
        return currentBlockCooldown > 0;
    }
    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }
    public void attack() {
        this.attacking = true;
        this.currentAttackCooldown = attackCooldown;
    }
    public void resetStateTime() {
        stateTime = 0;
    }

    public void setAttackCooldown(int cd) {
        this.attackCooldown = cd;
    }

    public void addGameObjectInRange(GameObject go){
        this.gameObjectsInRange.add(go);
        //System.out.println(go.getClass());
    }
    public void removeGameObjectInRange(GameObject go){
        this.gameObjectsInRange.remove(go);
    }
    public boolean attackOnCooldown() {
        return currentAttackCooldown > 0;
    }
    // unused circle sensor
    protected Fixture addSensorFixture() {
        FixtureDef sensorFixtureDef = new FixtureDef();
        CircleShape circle = new CircleShape();
        circle.setRadius(hitbox.width);
        sensorFixtureDef.shape = circle;
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.filter.categoryBits = ObjectCategory.MOB_SENSOR.getValue();
        sensorFixtureDef.filter.maskBits = ObjectCategory.PLAYER.getValue();

        Fixture fx = body.createFixture(sensorFixtureDef);
        fx.setUserData(this);
        circle.dispose();
        return fx;
    }
/*
        float radius = hitbox.width;
        Vector2 vertices[] = new Vector2[8];
        vertices[0] = new Vector2(0,0);
        for (int i = 0; i < 7; i++) {
            double angle = Math.toRadians(i / 6.0 * 180 ) - Math.PI / 2;
            vertices[i+1] = new Vector2 ((float) (2*radius * Math.cos(angle)), (float) (radius * Math.sin(angle)));
        }
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);
        sensorFixtureDef.shape = polygonShape;
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.filter.categoryBits = ObjectCategory.PLAYER_SENSOR.getValue();
        sensorFixtureDef.filter.maskBits = ObjectCategory.TREE.getValue();
        //sensorFixtureDef.filter.maskBits = ObjectCategory.PLAYER.getValue();
        Fixture fx = body.createFixture(sensorFixtureDef);
        fx.setUserData(this);
        polygonShape.dispose();

        return fx;
        //tower->m_body->CreateFixture(&myFixtureDef);

        //make the tower rotate at 45 degrees per second
        //tower->m_body->SetAngularVelocity(45 * DEGTORAD);
    }*/
    protected Fixture createSensorBody(){
        BodyDef def1 = new BodyDef();
        def1.type = BodyDef.BodyType.DynamicBody;
        def1.position.set(body.getPosition());
        this.sensorBody = body.getWorld().createBody(def1);
        sensorBody.setUserData(this);
        sensorBody.setSleepingAllowed(false);
        FixtureDef sensorFixtureDef = new FixtureDef();

        float radius = hitbox.width;
        Vector2 vertices[] = new Vector2[8];
        vertices[0] = new Vector2(0,0);
        for (int i = 0; i < 7; i++) {
            double angle = Math.toRadians(i / 6.0 * 180 ) - Math.PI / 2;
            vertices[i+1] = new Vector2 ((float) (2*radius * Math.cos(angle)), (float) (radius * Math.sin(angle)));
        }
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);
        sensorFixtureDef.shape = polygonShape;
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.filter.categoryBits = ObjectCategory.MOB_SENSOR.getValue();
        //sensorFixtureDef.filter.maskBits = (short) (ObjectCategory.TREE.getValue() | ObjectCategory.LOOT.getValue());
        sensorFixtureDef.filter.maskBits = (short) (ObjectCategory.PLAYER.getValue() | ObjectCategory.PLAYER_SENSOR.getValue());
        Fixture fx = sensorBody.createFixture(sensorFixtureDef);
        fx.setUserData(this);
        polygonShape.dispose();

        return fx;
    }
    @Override
    protected void createBody(World world, float x, float y) {
        BodyDef def1 = new BodyDef();
        def1.type = BodyDef.BodyType.DynamicBody;
        def1.position.set(x, y);
        this.body = world.createBody(def1);
        body.setUserData(this);
        body.setSleepingAllowed(false);
        body.setActive(true);
        FixtureDef fixtureDef = new FixtureDef();
        //fixtureDef.density = 5;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.shape = hitbox.shape;
        hitbox.shape.dispose();
        fixtureDef.filter.categoryBits = ObjectCategory.MOB.getValue();
        fixtureDef.filter.maskBits = (short) (ObjectCategory.HUT.getValue() | ObjectCategory.BUILD_SENSOR.getValue() | ObjectCategory.MOB.getValue() | ObjectCategory.PLAYER.getValue() | ObjectCategory.PLAYER_SENSOR.getValue() | ObjectCategory.TREE.getValue() | ObjectCategory.STONE.getValue() | ObjectCategory.BORDER.getValue());

        this.fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }

    public int getWood() {
        return wood;
    }

    public void setWood(int wood) {
        this.wood = wood;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getDmg() {
        return dmg;
    }

    public void setDmg(int dmg) {
        this.dmg = dmg;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public Vector2 getMoveVector() {
        return moveVector;
    }

    public void setMoveVector(Vector2 moveVector) {
        this.moveVector = moveVector;
    }
    public void setMoveVector(float x, float y) {
        this.moveVector.x = x;
        this.moveVector.y = y;
    }

    public Vector3 getDestination() {
            return destination;
        }

        public void setDestination(Vector3 destination) {
            this.destination = destination;
        }

        public void setDestination(float x, float y) {
            this.destination.x = x;
            this.destination.y = y;
        }
    public void setDirection(float x, float y){
        if (x > 0 && y > 0 ) {
            facing = 3;
            body.setTransform(body.getPosition(), 0.0f);
        } else if (x > 0 && y < 0 ) {
            facing = 0;
            body.setTransform(body.getPosition(), 0.0f);
        } else if (x < 0 && y > 0 ) {
            facing = 1;
            body.setTransform(body.getPosition(), (float)Math.PI);
        } else if (x < 0 && y < 0 ) {
            facing = 2;
            body.setTransform(body.getPosition(), (float)Math.PI);

        }
    }
    @Override
    public void takeDamage(int amount) {
        if (blocking) return;
        super.takeDamage(amount);
    }
    public void dealDmg() {}

    public void follow(Mob other) {
        this.setDestination(other.getBody().getPosition().x, other.getBody().getPosition().y);
        this.setMoving(true);
    }
    @Override
    public void draw(SpriteBatch batch) {
        boolean flip = (facing == 2 || facing == 1);
        float x = flip ? this.getPos().x + this.getWidth() : this.getPos().x;
        float width = flip ? - this.getWidth() : this.getWidth();
        if (!alive) {
            batch.draw(this.dead, this.getPos().x - this.getHeight() / 2, this.getPos().y + this.getWidth() / 2,getWidth() / 2, 0, this.getWidth(), this.getHeight(), 1, 1, 270);
            return;
        }
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;

        if (attacking) {
            currentFrame = attack.getKeyFrame(stateTime);
            batch.draw(currentFrame, x, this.getPos().y
                    , width * 2, this.getHeight() * 1.5f);
            return;
        } else if (blocking) {
            currentFrame = block.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, x, this.getPos().y
                    , width * 1.5f, this.getHeight());
            return;
        } else if (moving) {
            currentFrame = walkAnimations.get(facing % 2).getKeyFrame(stateTime, true);
        } else {
            currentFrame = idle.getKeyFrame(stateTime, true);
        }
        batch.draw(currentFrame, x, this.getPos().y, width, this.getHeight());
    }

    public void move() {
        this.setDirection(moveVector.x, moveVector.y);
        body.setLinearVelocity(speed * moveVector.x, speed * moveVector.y);
    }
    public float getAngle() {
        return (float) Math.atan2(moveVector.y, moveVector.x);
    }
}
