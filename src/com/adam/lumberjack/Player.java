package com.adam.lumberjack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

//TODO create extension of mob, move functionality specific for player here...
public class Player extends Mob {
    private boolean buildMode = false;
    LinkedList<BuildSensor> buildSensors;
    TextureRegion grid = new TextureRegion(new Texture("grid.png"));
    private Class buildObject;

    Player(World world, MobType mobType, float x, float y, int maxHealth, int dmg, float acceleration, float maxSpeed, Texture textures, int cols, int rows, float widthRatio, float heightRatio, Hitbox hitbox, Texture idle, int idleCols, Texture attack, int attackCols, Texture block, int blockCols, TextureRegion texDead, Chunk ownerChunk) {
        super(world, mobType, x, y, maxHealth, dmg, acceleration, maxSpeed, textures, cols, rows, widthRatio, heightRatio, hitbox, idle, idleCols, attack, attackCols, block, blockCols, texDead, ownerChunk);
        createSensorBody();
        changeFilterData();
        attackCooldown = 20;

        createBuildSensors();
    }
    public void setBuildMode(boolean val) {
        buildMode = val;
    }
    public boolean getBuildMode() {
        return buildMode;
    }
    private void changeFilterData() {
        Filter f1 = this.body.getFixtureList().first().getFilterData();
        f1.categoryBits = ObjectCategory.PLAYER.getValue();
        f1.maskBits = (short) (f1.maskBits | ObjectCategory.LOOT.getValue() | ObjectCategory.TRAP.getValue() | ObjectCategory.MOB_SENSOR.getValue() | ObjectCategory.RIVER.getValue());
        this.body.getFixtureList().first().setFilterData(f1);

        Filter f2 = this.sensorBody.getFixtureList().first().getFilterData();
        f2.categoryBits = ObjectCategory.PLAYER_SENSOR.getValue();
        f2.maskBits = (short) (f2.maskBits | ObjectCategory.LOOT.getValue() | ObjectCategory.MOB_SENSOR.getValue() | ObjectCategory.TREE.getValue() | ObjectCategory.MOB.getValue() | ObjectCategory.TRAP.getValue());
        this.sensorBody.getFixtureList().first().setFilterData(f2);

    }
    private void createBuildSensors() {
        LinkedList<BuildSensor> sensors = new LinkedList<>();
        Hitbox h = new Hitbox(0.2f,0.2f);
        Texture t = new Texture("free_occupied.png");

        for (int i = -4; i < 9; i++) {
            for (int j = 0; j < 7; j++) {

                BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.DynamicBody;
                Vector2 pos = GameUtils.roundDownToGrid(this.getPos().x, this.getPos().y);
                def.position.set(pos.x + i * Constants.TILE_SIZE, pos.y + j * Constants.TILE_SIZE);
                Body buildSensor = body.getWorld().createBody(def);
                buildSensor.setUserData(new Vector2(i, j));
                //buildSensor.setUserData(this);
                buildSensor.setSleepingAllowed(false);
                FixtureDef sensorFixtureDef = new FixtureDef();

                sensorFixtureDef.shape = h.shape;
                sensorFixtureDef.isSensor = true;
                sensorFixtureDef.filter.categoryBits = ObjectCategory.BUILD_SENSOR.getValue();
                //sensorFixtureDef.filter.maskBits = (short) (-ObjectCategory.BUILD_SENSOR.getValue());
                Fixture fx = buildSensor.createFixture(sensorFixtureDef);

                //polygonShape.dispose();

                BuildSensor b = new BuildSensor(buildSensor, t);
                fx.setUserData(b);
                sensors.add(b);
            }
        }
        this.buildSensors = sensors;
    }
    @Override
    public void dealDmg() {
        for (GameObject go : gameObjectsInRange){
            if (go instanceof Sheep) continue;
            go.takeDamage(this.getDmg() * 50);
        }
    }
    public void buildFence() {
        try {
            getOwnerChunk().addObjects(GameUtils.putDownFence(this.body.getPosition().x , this.body.getPosition().y, this.body.getWorld(), this.getOwnerChunk()));
        } catch (NullPointerException e){
            System.out.println("cant find ownerChunk");
        }
    }
    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        //batch.draw(this.grid, this.getPos().x - this.getHeight() / 2, this.getPos().y + this.getWidth() / 2,getWidth() / 2, 0, this.getHitbox().width * 2.5f, this.hitbox.height * 5, 1, 1, 270);
    }
    public void drawBuildSensors(SpriteBatch batch) {
        for (BuildSensor b : buildSensors) {
            b.draw(batch);
        }
    }
    public Class getBuildObject() {
        return this.buildObject;
    }
    public void setBuildObject(Class obj) {
        this.buildObject = obj;
        if (obj == River.class) {
            for (BuildSensor b: buildSensors) {
                b.setBridgeMode();
            }
        } else {
            for (BuildSensor b: buildSensors) {
                b.setDefaultMode();
            }
        }
    }
    @Override
    public void update() {
        //System.out.println(buildObject);
        if (currentAttackCooldown > 0) currentAttackCooldown--;
        if (attacking && attack.isAnimationFinished(stateTime)) {
            attacking = false;
            stateTime = 0;
            currentAttackCooldown = attackCooldown;
        }
        /*if (attackCooldown > 0) attackCooldown--;
        if (attackCooldown == 0) attacking = false;*/

        Set<GameObject> tmp = new HashSet<GameObject>();
        for (GameObject o : gameObjectsInRange) {
            if (o instanceof Loot && ((Loot) o).getWaitTime() == 0) {
                wood += 1;
                //o.getOwnerChunk().removeObject(o);
                tmp.add(o);
            } else if (o instanceof Mushroom) {
                if (((Mushroom) o).getType() == 0 && this.currentHealth == this.maxHealth) break;
                ((Mushroom) o).applyEffect(this);
                tmp.add(o);
            }

        }
        for (GameObject l : tmp) {
            l.getOwnerChunk().removeObject(l);
            //GameUtils.returnLootToBank((Loot) l);
            removeGameObjectInRange(l);
        }
        //map.removeObjects(tmp);

        this.pos.set(body.getPosition().x -  hitbox.width / 2, body.getPosition().y - hitbox.height / 2, 0); //for texture to be in correct place
        sensorBody.setTransform(body.getPosition(), moveVector.x != 0 ? this.getAngle() : sensorBody.getAngle());
        for (BuildSensor b : buildSensors) {
            Vector2 coords = GameUtils.roundDownToGrid(this.getPos().x, this.getPos().y);
            coords.x += ((Vector2) b.getBody().getUserData()).x * Constants.TILE_SIZE - Constants.TILE_SIZE / 2;
            coords.y += ((Vector2) b.getBody().getUserData()).y * Constants.TILE_SIZE - Constants.TILE_SIZE / 2;
            b.getBody().setTransform(coords, 0);
            //System.out.println(b.getBody().getPosition().x + "POZ" + b.getBody().getPosition().y);
        }
        if (alive && moving)
        {
            move();
        } else {
            body.setLinearVelocity(body.getLinearVelocity().x / 1.1f, body.getLinearVelocity().y / 1.1f);
            body.applyForceToCenter(-body.getLinearVelocity().x, -body.getLinearVelocity().y, true);
            //body.setLinearVelocity(0, 0);
            //body.setAngularVelocity(0);
        }
    }
}
