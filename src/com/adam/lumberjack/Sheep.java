package com.adam.lumberjack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;

import java.util.HashSet;
import java.util.Set;

public class Sheep extends Mob {
    private boolean buildMode = false;
    Sheep(World world, MobType mobType, float x, float y, int maxHealth, int dmg, float acceleration, float maxSpeed, Texture textures, int cols, int rows, float widthRatio, float heightRatio, Hitbox hitbox, Texture idle, int idleCols, Texture attack, int attackCols, TextureRegion texDead, Chunk ownerChunk) {
        super(world, mobType, x, y, maxHealth, dmg, acceleration, maxSpeed, textures, cols, rows, widthRatio, heightRatio, hitbox, idle, idleCols, attack, attackCols, texDead, ownerChunk);
        this.changeFilterData();
        attackCooldown = 20;
    }
    private void changeFilterData() {
        Filter f1 = this.body.getFixtureList().first().getFilterData();
        f1.categoryBits = ObjectCategory.MOB.getValue();
        f1.maskBits = (short) (f1.maskBits | ObjectCategory.LOOT.getValue() | ObjectCategory.TRAP.getValue() | ObjectCategory.MOB_SENSOR.getValue() | ObjectCategory.RIVER.getValue());
        this.body.getFixtureList().first().setFilterData(f1);
    }
    @Override
    public void draw(SpriteBatch batch) {
        boolean flip = (facing == 2 || facing == 1);
        float x = flip ? this.getPos().x + this.getWidth() : this.getPos().x;
        float width = flip ? - this.getWidth() : this.getWidth();
        if (!alive) {
            batch.draw(this.dead, x, this.getPos().y, width, this.getHeight());
            return;
        }
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;

        if (attacking) {
            currentFrame = attack.getKeyFrame(stateTime);
            batch.draw(currentFrame, x, this.getPos().y
                    , width * 2, this.getHeight() * 1.5f);
            return;
        } else if (moving) {
            currentFrame = walkAnimations.get(facing % 2).getKeyFrame(stateTime, true);
        } else {
            currentFrame = idle.getKeyFrame(stateTime, true);
        }
        batch.draw(currentFrame, x, this.getPos().y, width, this.getHeight());
    }
    @Override
    public void update() {
        this.pos.set(body.getPosition().x -  width / 2, body.getPosition().y - hitbox.height / 2, 0); //for texture to be in correct place
        //sensorBody.setTransform(body.getPosition(), moveVector.x != 0 ? this.getAngle() : sensorBody.getAngle());
        if (alive && moving)
        {
            move();
        } else {
            body.setLinearVelocity(body.getLinearVelocity().x / 1.1f, body.getLinearVelocity().y / 1.1f);
            body.applyForceToCenter(-body.getLinearVelocity().x, -body.getLinearVelocity().y, true);
        }
    }
}
