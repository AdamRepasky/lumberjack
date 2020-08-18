package com.adam.lumberjack;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class Mushroom extends GameObject{
    int type;
    public Mushroom(TextureRegion texture, float widthRatio, float heightRatio, World world, Hitbox hitbox, int maxHealth, float x, float y, Chunk ownerChunk, int type) {
        super(texture, widthRatio, heightRatio, world, hitbox, maxHealth, x, y, ownerChunk);
        this.type = type;
    }
    public int getType() {
        return type;
    }
    public void applyEffect(Mob player) {
        switch (type) {
            case 0:
                player.takeDamage(- player.getMaxHealth() / 4);
                if (player.getMaxHealth() < player.getCurrentHealth()) {
                    player.setCurrentHealth(player.getMaxHealth());

                }
                break;
            case 1:
                player.takeDamage(player.getCurrentHealth() / 4);
                break;
            case 2:

                break;
        }
    }

    @Override
    protected void createBody(World world, float x, float y) {
        BodyDef def1 = new BodyDef();
        def1.type = BodyDef.BodyType.StaticBody;
        def1.position.set(x, y);


        this.body = world.createBody(def1);
        body.setUserData(this);
        this.fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = hitbox.shape;
        fixtureDef.filter.categoryBits = ObjectCategory.LOOT.getValue();
        fixtureDef.filter.maskBits = (short) (ObjectCategory.PLAYER_SENSOR.getValue() | ObjectCategory.PLAYER.getValue());

        this.fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }

    public void update() {
        this.pos.set(body.getPosition().x - width / 2, body.getPosition().y - hitbox.height / 2, 0); //for texture to be in correct place SKUS IF vlozit
        //body.setLinearVelocity(body.getLinearVelocity().x / 3, body.getLinearVelocity().x / 3);
        //body.applyForceToCenter(-body.getLinearVelocity().x, -body.getLinearVelocity().y, true);
    }
}

