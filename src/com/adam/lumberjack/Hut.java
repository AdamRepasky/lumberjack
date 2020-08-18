package com.adam.lumberjack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import javax.swing.text.Position;

public class Hut extends GameObject{

    Animation<TextureRegion> smoke;
    Body sensorBody;
    protected float stateTime = 0f;

    public Hut(TextureRegion texture, TextureRegion open, Texture smokeTexture, float widthRatio, float heightRatio, World world, Hitbox hitbox, int maxHealth, float x, float y, Chunk ownerChunk) {
        super(texture, open, widthRatio, heightRatio, world, hitbox, maxHealth, x, y, ownerChunk);
        createSensorBody();
        this.smoke = new Animation<>(0.25f, TextureRegion.split(smokeTexture, smokeTexture.getWidth() / 4, smokeTexture.getHeight())[0]);
    }

    @Override
    protected void createBody(World world, float x, float y) {
        BodyDef def1 = new BodyDef();
        def1.type = BodyDef.BodyType.StaticBody;
        def1.position.set(x, y);

        this.body = world.createBody(def1);
        body.setUserData(this);
        this.fixtureDef = new FixtureDef();
        fixtureDef.shape = hitbox.shape;
        fixtureDef.filter.categoryBits = ObjectCategory.HUT.getValue();
        fixtureDef.filter.maskBits = (short) (ObjectCategory.MOB.getValue() | ObjectCategory.BUILD_SENSOR.getValue() | ObjectCategory.PLAYER.getValue());

        this.fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }
    protected Fixture createSensorBody(){
        BodyDef def1 = new BodyDef();
        def1.type = BodyDef.BodyType.StaticBody;
        Vector2 p = body.getPosition();
        Hitbox sensorHitbox = new Hitbox(hitbox.width / 6, Constants.TILE_SIZE);
        def1.position.set(p.x - hitbox.width / 2 + sensorHitbox.width / 2 + hitbox.width / 10, p.y - hitbox.height / 2 - sensorHitbox.height / 2);
        this.sensorBody = body.getWorld().createBody(def1);
        sensorBody.setUserData(this);
        sensorBody.setSleepingAllowed(false);
        FixtureDef sensorFixtureDef = new FixtureDef();


        sensorFixtureDef.shape = sensorHitbox.shape;
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.filter.categoryBits = ObjectCategory.HUT.getValue();
        sensorFixtureDef.filter.maskBits = ObjectCategory.PLAYER.getValue();
        Fixture fx = sensorBody.createFixture(sensorFixtureDef);
        fx.setUserData(this);
        sensorHitbox.shape.dispose();

        return fx;
    }
    public void open() {
        this.setActiveTextureIndex(0, 1);
    }
    public void close() {
        this.setActiveTextureIndex(0, 0);
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = smoke.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, this.getPos().x + this.width / 10 * 7 , this.getPos().y + this.height / 10 * 9.5f, width / 6, height / 2);
    }

    public void update() {
        //this.pos.set(body.getPosition().x - width / 2, body.getPosition().y - hitbox.height / 2, 0);
    }
}