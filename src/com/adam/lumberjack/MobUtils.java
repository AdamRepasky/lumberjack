package com.adam.lumberjack;

import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public final class MobUtils {
    private MobUtils(){}
    public static final void crazedMove(Mob mob) {
        Random r = new Random();
        float fromX = -1.0f;
        float toX = 1.0f;
        float fromY = -1.0f;
        float toY = 1.0f;
        float randomX = fromX + r.nextFloat() * (toX - fromX);
        float randomY = fromY + r.nextFloat() * (toY - fromY);
        mob.setMoveVector(randomX, randomY);
        mob.setMoving(true);
    }
    public static final void moveTo(Mob mob, Vector2 to) {
        if (to.x - Constants.TILE_SIZE * 5 < mob.getBody().getPosition().x && to.x + Constants.TILE_SIZE * 5> mob.getBody().getPosition().x  &&
                to.y - Constants.TILE_SIZE * 5 < mob.getBody().getPosition().y && to.y + Constants.TILE_SIZE * 5> mob.getBody().getPosition().y) {
            mob.setMoving(false);
            return;
        }
        double x = to.x - mob.body.getPosition().x;
        double y = to.y - mob.body.getPosition().y;
        double angle;
        if (y == 0.0) angle = 0;
        else angle = Math.atan(x/y);

        float xVelocity = (float) Math.sin(angle);
        float yVelocity = (float) Math.cos(angle);

        /*float part = (Math.abs(x) + Math.abs(y)) / 100;
        if (part > 1.0f) {
            mob.setMoveVector(x / part, y / part);
        } else {*/
        if (x < 0 && y < 0) {
            xVelocity *= -1;
            yVelocity *= -1;
        } else if (x > 0 && y < 0) {
            xVelocity *= -1;
            yVelocity *= -1;
        }
        //if (y < 0) yVelocity *= -1;
        mob.setMoveVector(xVelocity, yVelocity);
        //}
        mob.setMoving(true);
    }
    public static final void moveTo(Mob mob, GameObject obj) {
        Vector2 to =  obj.body.getPosition();
        for (GameObject o : mob.gameObjectsInRange) {
            if (o == obj) {
                mob.setMoving(false);
                System.out.println("Player reached");
                return;
            }
        }
        System.out.println("Player not reached");
        double x = to.x - mob.body.getPosition().x;
        double y = to.y - mob.body.getPosition().y;
        double angle;
        if (y == 0.0) angle = 0;
        else angle = Math.atan(x/y);

        float xVelocity = (float) Math.sin(angle);
        float yVelocity = (float) Math.cos(angle);

        /*float part = (Math.abs(x) + Math.abs(y)) / 100;
        if (part > 1.0f) {
            mob.setMoveVector(x / part, y / part);
        } else {*/
        if (x < 0 && y < 0) {
            xVelocity *= -1;
            yVelocity *= -1;
        } else if (x > 0 && y < 0) {
            xVelocity *= -1;
            yVelocity *= -1;
        }
        //if (y < 0) yVelocity *= -1;
        mob.setMoveVector(xVelocity, yVelocity);
        //}
        mob.setMoving(true);
    }
}
