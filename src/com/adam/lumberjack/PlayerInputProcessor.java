package com.adam.lumberjack;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class PlayerInputProcessor implements InputProcessor {
    Mob p1;
    OrthographicCamera cam;
    LumberJack game;

    public PlayerInputProcessor(Mob p1, OrthographicCamera cam, LumberJack game) {

        this.p1 = p1;
        this.cam = cam;
        this.game = game;
    }
    @Override
    public boolean keyDown(int keycode) {
        //if(keycode == Input.Keys.BACK){
          //  System.out.println("Back pressed.");


            //game.pause();

            // Do your optional back button handling (show pause menu?)
        //}
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        /*p1.setMoving(true);
        Vector3 destination = new Vector3(screenX - p1.hitbox.width / 2 ,screenY ,0);
        cam.unproject(destination);
        //p1.setDestination(destination);
        return true;*/
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //p1.setMoving(false);
        //return true;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        /*Vector3 destination = new Vector3(screenX - p1.hitbox.width / 2  ,screenY ,0);
        cam.unproject(destination);
        p1.setMoving(true);*/
        //p1.setDestination(destination);
        return false;
        //return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
