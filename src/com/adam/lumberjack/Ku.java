package com.adam.lumberjack;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;

import java.util.HashSet;
import java.util.Set;

//TODO create extension of mob, move functionality specific for player here...
public class Ku extends Mob {
    private boolean buildMode = false;
    TextureRegion grid = new TextureRegion(new Texture("grid.png"));
    Ku(World world, MobType mobType, float x, float y, int maxHealth, int dmg, float acceleration, float maxSpeed, Texture textures, int cols, int rows, float widthRatio, float heightRatio, Hitbox hitbox, Texture idle, int idleCols, Texture attack, int attackCols, TextureRegion texDead, Chunk ownerChunk) {
        super(world, mobType, x, y, maxHealth, dmg, acceleration, maxSpeed, textures, cols, rows, widthRatio, heightRatio, hitbox, idle, idleCols, attack, attackCols, texDead, ownerChunk);
        createSensorBody();
        changeFilterData();
        attackCooldown = 20;
    }
    private void changeFilterData() {
        Filter f1 = this.body.getFixtureList().first().getFilterData();
        f1.categoryBits = ObjectCategory.MOB.getValue();
        f1.maskBits = (short) (f1.maskBits | ObjectCategory.RIVER.getValue()); //| ObjectCategory.LOOT.getValue() | ObjectCategory.TRAP.getValue() | ObjectCategory.MOB_SENSOR.getValue());
        this.body.getFixtureList().first().setFilterData(f1);

        Filter f2 = this.sensorBody.getFixtureList().first().getFilterData();
        f2.categoryBits = ObjectCategory.MOB_SENSOR.getValue();
        //f2.maskBits = (short) (f2.maskBits | ObjectCategory.LOOT.getValue() | ObjectCategory.MOB_SENSOR.getValue() | ObjectCategory.TREE.getValue() | ObjectCategory.MOB.getValue() | ObjectCategory.TRAP.getValue());
        this.sensorBody.getFixtureList().first().setFilterData(f2);
    }
    @Override
    public void dealDmg() {
        for (GameObject go : gameObjectsInRange){
            if (go instanceof Player && go.isAlive()) {
                go.takeDamage(this.getDmg());
            } else if (go instanceof Sheep && go.isAlive()) {
                go.takeDamage(this.getDmg());
            }
        }
    }
    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }
    @Override
    public void update() {
        //System.out.println("atChunk : " + getOwnerChunk());
        if (currentAttackCooldown > 0) currentAttackCooldown--;
        if (attacking && attack.isAnimationFinished(stateTime)) {
            attacking = false;
            stateTime = 0;
            currentAttackCooldown = attackCooldown;
        }
        Set<GameObject> tmp = new HashSet<GameObject>();
        if (alive) {
            if (currentAttackCooldown == 0) {
                for (GameObject o : gameObjectsInRange) {
                    if (o instanceof Player && o.isAlive()) {
                        this.attack();
                        this.resetStateTime();
                        this.dealDmg();
                        tmp.add(o);
                    }
                }
            }
        }
        this.pos.set(body.getPosition().x -  hitbox.width / 2, body.getPosition().y - hitbox.height / 2, 0); //for texture to be in correct place
        //sensorBody.setTransform(body.getPosition(), moveVector.x != 0 ? body.getAngle() : sensorBody.getAngle());
        sensorBody.setTransform(body.getPosition(), moveVector.x != 0 ? this.getAngle() : sensorBody.getAngle());
        if (alive && moving)
        {
            move();
        } else {
            body.setLinearVelocity(body.getLinearVelocity().x / 1.1f, body.getLinearVelocity().y / 1.1f);
            body.applyForceToCenter(-body.getLinearVelocity().x, -body.getLinearVelocity().y, true);
            //body.setLinearVelocity(0, 0);
            //body.setAngularVelocity(0);
        }
        if (!alive) {
            body.getFixtureList().first().setSensor(true);
        }
    }
}