package com.adam.lumberjack;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PlayerGestureListener implements GestureDetector.GestureListener {
    Player p1;
    CameraHelper cam;
    PlayerGestureListener(Player p1, CameraHelper cam) {
        this.p1 = p1;
        this.cam = cam;
    }
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
        /*pan(x, y, x,y);
        System.out.println("touchDown");*/
        //p1.setMoving(true);
        // Vector3 destination = new Vector3(x - p1.hitbox.width / 2 ,y ,0);
        //cam.unproject(destination);
        //p1.setDestination(destination);
        //return true;
        //return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (!p1.getBuildMode()) return false;
        p1.buildFence();

        //pan(x, y, 0,0);
        //System.out.println("tap");
        /*p1.setMoving(true);
        Vector3 destination = new Vector3(x - p1.hitbox.width / 2 ,y ,0);
        cam.unproject(destination);
        p1.setDestination(destination);*/
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        /*if (p1.getBuildMode()) {
            p1.setBuildMode(false);
            return true;
        }
        System.out.println("longPress");
        p1.setBuildMode(true);
        cam.zoomCameraToObject(p1);*/
        //p1.buildFence();
        //if (x > p1.getPos().x + p1.width + p1.width * 3 || x < p1.getPos().x - p1.width * 3 || y > p1.getPos().y + p1.height + p1.height * 3 || y < p1.getPos().y - p1.height * 3) return false;
        //System.out.println("long press on player");
        /*p1.setMoving(true);
        Vector3 destination = new Vector3(x - p1.hitbox.width / 2 ,y ,0);
        cam.unproject(destination);
        p1.setDestination(destination);
        return true;*/
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        /*
        System.out.println("pan");
        p1.setMoving(true);
        Vector3 destination = new Vector3(x - p1.hitbox.width / 2 ,y ,0);
        cam.unproject(destination);
        *///p1.setDestination(destination);

        //return true;
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        //p1.setMoving(false);
        //return true;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
