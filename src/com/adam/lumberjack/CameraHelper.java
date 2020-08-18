package com.adam.lumberjack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;


public class CameraHelper{
    private OrthographicCamera camera;
    private Player p1;
    private GameObject cameraObject;

    private float beforeZoomWidth;
    private float beforeZoomHeight;
    private float zoomWidth;
    private float zoomHeight;
    public CameraHelper(OrthographicCamera camera, Player p1) {
        this.camera = camera;
        this.p1 = p1;

        camera.setToOrtho(false, Gdx.graphics.getWidth() / 100, Gdx.graphics.getHeight() / 100);
        camera.position.x = p1.getPos().x;
        camera.position.y = p1.getPos().y;
        this.beforeZoomWidth = camera.viewportWidth;
        this.beforeZoomHeight = camera.viewportHeight;
        this.zoomWidth = camera.viewportWidth/4;
        this.zoomHeight = camera.viewportHeight/4;
    }
    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setCameraMovingRight() {
        float speed = 0.25f;
        float lerp = 0.03f;
        Vector3 position = camera.position;
        if (position.x - Gdx.graphics.getWidth() / 100 / 2.2f  > cameraObject.getBody().getPosition().x) {
            //position.x += (go.body.getPosition().x - position.x) * lerp + speed /** Gdx.graphics.getDeltaTime()*/;
        } else {
            position.x += (cameraObject.body.getPosition().x - position.x) * lerp + speed /** Gdx.graphics.getDeltaTime()*/;

        }
        position.y += (cameraObject.body.getPosition().y - position.y) * lerp /** Gdx.graphics.getDeltaTime()*/;
    }

    public void setCameraPosToGameObject(GameObject go) {
        float lerp = 0.1f;
        Vector3 position = camera.position;
        position.x += (go.getPos().x + go.getWidth() / 2 /*body.getPosition().x*/ - position.x) * lerp /** Gdx.graphics.getDeltaTime()*/;
        position.y += (go.getPos().y + go.getHeight() / 2/*body.getPosition().y*/ - position.y) * lerp /** Gdx.graphics.getDeltaTime()*/;
    }
    public void zoomCameraToObject(GameObject go) {
        float lerp = 0.1f;
        float w = zoomWidth;
        float h = zoomHeight;
        //System.out.println(w + " " + h);
        setCameraPosToGameObject(go);
        //System.out.println(w + " "+ h);
        camera.viewportWidth += (w  - camera.viewportWidth) * lerp /** Gdx.graphics.getDeltaTime()*/;
        camera.viewportHeight += (h  - camera.viewportHeight) * lerp /** Gdx.graphics.getDeltaTime()*/;
        /*
        cam.viewportWidth = go.width * 2;
        cam.viewportHeight = go.height * 2;
        */
    }
    public void zoomCameraFromObject(GameObject go) {
        float lerp = 0.1f;
        float w = beforeZoomWidth;//cam.viewportWidth/10;
        float h = beforeZoomHeight;//cam.viewportHeight/10;
        setCameraPosToGameObject(go);
        //System.out.println(w + " "+ h);
        camera.viewportWidth += (w  - camera.viewportWidth) * lerp /** Gdx.graphics.getDeltaTime()*/;
        camera.viewportHeight += (h  - camera.viewportHeight) * lerp /** Gdx.graphics.getDeltaTime()*/;
        /*
        cam.viewportWidth = go.width * 2;
        cam.viewportHeight = go.height * 2;
        */
    }
    public void tick() {
        this.setCameraPosToGameObject(p1);
        camera.update();
        if (!p1.isAlive()) {
            setCameraObject(p1);
        }
        if (p1.getBuildMode() && camera.viewportWidth != zoomWidth) {
            this.zoomCameraToObject(p1);
        } else if (!p1.getBuildMode() && camera.viewportWidth != beforeZoomWidth) {
            this.zoomCameraFromObject(p1);
        }
    }

    public void setCameraObject(GameObject gameObject) {
        this.cameraObject = gameObject;
    }
}