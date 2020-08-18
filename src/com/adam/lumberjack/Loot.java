package com.adam.lumberjack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class Loot extends GameObject{
    private int waitTime = 30;
    private final int maxWaitTime = 30;
    private final int maxDespawnTime = 500;
    private int despawnTime = 500;

    public Loot(TextureRegion texture, float widthRatio, float heightRatio, World world, Hitbox hitbox, int maxHealth, float x, float y, Chunk ownerChunk) {
        super(texture, widthRatio, heightRatio, world, hitbox, maxHealth, x, y, ownerChunk);
        //tu padlo raz
        /*Random r = new Random();
        float fromX = 20.0f;
        float toX = 30.0f;
        float fromY = 20.0f;
        float toY = 30.0f;
        float randomX = fromX + r.nextFloat() * (toX - fromX);
        float randomY = fromY + r.nextFloat() * (toY - fromY);
        boolean neg = r.nextBoolean();
        if (neg) {
            randomX *= -1;
        }
        neg = r.nextBoolean();
        if (neg) {
            randomY *= -1;
        }
        body.setLinearVelocity(randomX, randomY);*/
    }

    @Override //<--- 8 err
    protected void createBody(World world, float x, float y) {
        BodyDef def1 = new BodyDef();
        def1.type = BodyDef.BodyType.DynamicBody;
        def1.position.set(x, y);
        System.out.println("b");

        this.body = world.createBody(def1); System.out.println("d"); //here SIGSEGV happens
        body.setUserData(this);
        this.fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = hitbox.shape;
        fixtureDef.filter.categoryBits = ObjectCategory.LOOT.getValue();
        fixtureDef.filter.maskBits = (short) (ObjectCategory.PLAYER_SENSOR.getValue() | ObjectCategory.PLAYER.getValue());// | ObjectCategory.BORDER.getValue());

        this.fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }

    public int getWaitTime() {
        return waitTime;
    }
    public void resetWaitTime() {
        waitTime = maxWaitTime;
    }
    public int getDespawnTime() {
        return despawnTime;
    }
    public void resetDespawnTime() {
        despawnTime = maxDespawnTime;
    }
    public void update() {
        if (waitTime > 0) {
            waitTime--;
        }
        if (despawnTime > 0) {
            despawnTime--;
        } else {
            this.getOwnerChunk().removeObject(this);
            despawnTime = maxDespawnTime;
        }
        this.pos.set(body.getPosition().x - width / 2, body.getPosition().y - hitbox.height / 2, 0); //for texture to be in correct place SKUS IF vlozit
        //body.setLinearVelocity(body.getLinearVelocity().x / 3, body.getLinearVelocity().x / 3);
        //body.applyForceToCenter(-body.getLinearVelocity().x, -body.getLinearVelocity().y, true);
    }
}
