package com.adam.lumberjack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class Gui implements Disposable {
    private Player p1;
    private Texture healthBar = new Texture("healthBar.png");
    private Texture healthCurrent = new Texture("health.png");
    private Texture wood = new Texture("wood.png");
    private TextureRegion rip = new TextureRegion(new Texture("RIP.png"));
    private Image image = new Image(rip);
    private ColorAction fade = new ColorAction();
    private Stage stage;
    private boolean buildMode = false;
    private List<Button> buildButtons = new ArrayList<>();
    CameraHelper cam;

    BitmapFont font = new BitmapFont();
    //BitmapFont font2 = new BitmapFont();

    public Gui(final Player p1, Texture healthBar, Texture healthCurrent, Texture wood, CameraHelper cam) {
        this.p1 = p1;
        this.cam = cam;
        this.healthBar = healthBar;
        this.healthCurrent = healthCurrent;
        this.wood = wood;
        //rip.setAlpha(0.6f);
        //rip.setBounds(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(2.5f);
        font.setColor(Color.GOLDENROD);


        stage = new Stage(new ScreenViewport());
        createButtons();

        //img
        image.setPosition(0,0);
        image.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //image.addAction(Actions.sequence(Actions.fadeIn(1)));
        image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
        image.setColor(1f, 1f, 1f, 0f); // or myLabel.setColor(1f, 1f, 1f, 0f); or even myLabel.setColor(myLabel.getColor().r, myLabel.getColor().g, myLabel.getColor().b, 0f);
        image.addAction(Actions.sequence(Actions.delay(2f), Actions.forever(Actions.sequence(Actions.fadeIn(2f),Actions.fadeOut(2f)))));
        //image.addAction(Actions.forever(Actions.sequence(Actions.fadeIn(2f),Actions.fadeOut(2f))));
        image.addAction(Actions.sequence(Actions.delay(2f),Actions.rotateBy(-45), Actions.forever(Actions.sequence(Actions.rotateBy(90, 3f), Actions.rotateBy(-90, 3f)))));

        //image.addAction(Actions.sequence(Actions.delay(2f), Actions.fadeOut(2f)));

        //fade.setDuration(10f);
        //fade.setColor(new Color(1,0,0,0.2f));
        //fade.setActor(image);
        //image.addAction(fade);
    }
    public void draw(Batch batch) {
        batch.draw(healthBar, Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 18,
                Gdx.graphics.getWidth() / 5, 20);
        batch.draw(healthCurrent, Gdx.graphics.getWidth() / 17, Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 18,
                Gdx.graphics.getWidth() / 5 * (p1.getCurrentHealth() / (p1.getMaxHealth() / 100.0f)) / 100.0f, 20);
        batch.draw(wood, 10, Gdx.graphics.getHeight() - 100,
                100, 100);
        font.draw(batch, "" + p1.getWood(), 10 + 25 , Gdx.graphics.getHeight() - 80);
        if (!p1.isAlive()) {
            stage.addActor(image);
            //image.draw(batch, 1);
            //batch.draw(rip, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }
    public void toggleBuildButtons() {
        buildMode = !buildMode;
        if (buildMode) {
            for (Button b : buildButtons) {
                stage.addActor(b);
            }
        } else {
            for (Button b : buildButtons) {
                b.remove();
            }
        }
    }
    public Stage getStage() {
        return stage;
    }
    public void tick(){
        stage.draw();
        stage.act(Gdx.graphics.getDeltaTime());
    }
    public void createBlockButton(Skin mySkin) {
        Button button1 = new ImageButton(mySkin);

        button1.getStyle().down = new TextureRegionDrawable(new TextureRegion(new Texture("blockButtonDownLightBW.png")));
        button1.getStyle().up = new TextureRegionDrawable(new TextureRegion(new Texture("blockButtonUpLightBW.png")));
        button1.setSize(Gdx.graphics.getHeight() / 4, Gdx.graphics.getHeight() / 4);
        button1.setPosition(Gdx.graphics.getWidth() - button1.getWidth() - 100,50 + button1.getHeight()/*Gdx.graphics.getHeight() / 30*/);
        button1.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                p1.stopBlocking();
                p1.getOwnerChunk().printGrid();
                /*Vector2 v = new Vector2(321.145f, 231.231f);
                p1.getOwnerChunk().getNodeFromVector(v);
                p1.getOwnerChunk().getNodeFromVector(v);
                System.out.println(v);
                System.out.println("---");
                p1.getOwnerChunk().getNodeFromVector(new Vector2(0.145f, 2.231f));System.out.println("---");
                p1.getOwnerChunk().getNodeFromVector(new Vector2(0.0f, 0.0f));System.out.println("---");
                p1.getOwnerChunk().getNodeFromVector(new Vector2(12.2f, 63.25f));System.out.println("---");
                p1.getOwnerChunk().getNodeFromVector(new Vector2(0.25f, 0.26f));System.out.println("---");
                p1.getOwnerChunk().getNodeFromVector(new Vector2(10.115f, 2021.231f));System.out.println("---");*/
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (p1.isAttacking() || p1.blockOnCooldown()) return true;

                p1.startBlocking();
                p1.resetStateTime();
                return true;
            }
        });
        stage.addActor(button1);
    }
    public void createAttackButton(Skin mySkin) {
        Button button2 = new ImageButton(mySkin);

        button2.getStyle().down = new TextureRegionDrawable(new TextureRegion(new Texture("axeButtonDownLightBW.png")));
        button2.getStyle().up = new TextureRegionDrawable(new TextureRegion(new Texture("axeButtonUpLightBW.png")));
        button2.setSize(Gdx.graphics.getHeight() / 4, Gdx.graphics.getHeight() / 4);
        button2.setPosition(Gdx.graphics.getWidth() - button2.getWidth() - 100,25/*Gdx.graphics.getHeight() / 30*/);
        button2.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (p1.isAttacking() || p1.attackOnCooldown() || p1.getBlocking() == true) return true;

                p1.attack();
                p1.resetStateTime();
                //p1.setAttackCooldown(20);
                p1.dealDmg();
                //System.out.println("dmg");
                return true;
            }
        });
        stage.addActor(button2);
    }
    public void createBridgeButton(Skin mySkin) {
        Button button2 = new ImageButton(mySkin);

        button2.getStyle().down = new TextureRegionDrawable(new TextureRegion(new Texture("missing.png")));
        button2.getStyle().up = new TextureRegionDrawable(new TextureRegion(new Texture("missing.png")));
        button2.setSize(Gdx.graphics.getHeight() / 8, Gdx.graphics.getHeight() / 8);
        button2.setPosition(Gdx.graphics.getWidth() - button2.getWidth() - 100,Gdx.graphics.getHeight() - button2.getHeight() - 20/*Gdx.graphics.getHeight() / 30*/);
        button2.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (p1.getBuildObject() != River.class) p1.setBuildObject(River.class);
                else p1.setBuildObject(null);
                return true;
            }
        });
        this.buildButtons.add(button2);
    }
    public void createBuildModeButton(Skin mySkin) {
        Button button2 = new ImageButton(mySkin);

        button2.getStyle().down = new TextureRegionDrawable(new TextureRegion(new Texture("missing.png")));
        button2.getStyle().up = new TextureRegionDrawable(new TextureRegion(new Texture("missing.png")));
        button2.setSize(Gdx.graphics.getHeight() / 8, Gdx.graphics.getHeight() / 8);
        button2.setPosition(Gdx.graphics.getWidth() - button2.getWidth() - 100,Gdx.graphics.getHeight() - button2.getHeight() - 200/*Gdx.graphics.getHeight() / 30*/);
        button2.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                toggleBuildButtons();
                if (p1.getBuildMode()) {
                    p1.setBuildMode(false);
                    return true;
                }
                p1.setBuildMode(true);
                cam.zoomCameraToObject(p1);
                return true;
            }
        });
        stage.addActor(button2);
    }
    public void createButtons() {
        Skin mySkin1 = new Skin(Gdx.files.internal("mySkin.json"));
        Skin mySkin2 = new Skin(Gdx.files.internal("mySkin.json"));
        Skin mySkin3 = new Skin(Gdx.files.internal("mySkin.json"));
        Skin mySkin4 = new Skin(Gdx.files.internal("mySkin.json"));
        createAttackButton(mySkin1);
        createBlockButton(mySkin2);
        createBridgeButton(mySkin3);
        createBuildModeButton(mySkin4);
        //Touchpad
        // Create a touchpad skin
        Skin touchpadSkin = new Skin();
        // Set background image
        touchpadSkin.add("buttonBack", new Texture("buttonBackXS.png"));
        // Set knob image
        touchpadSkin.add("buttonHead", new Texture("buttonHeadLight.png"));
        // Create TouchPad Style
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        // Create Drawable's from TouchPad skin
        Drawable touchBackground;
        Drawable touchKnob;
        touchBackground = touchpadSkin.getDrawable("buttonBack");
        touchKnob = touchpadSkin.getDrawable("buttonHead");
        float touchpadRadius = Gdx.graphics.getHeight() / 2.0f;
        touchKnob.setMinWidth(touchpadRadius / 2.0f);
        touchKnob.setMinHeight(touchpadRadius / 2.0f);
        // Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        Touchpad touchpad = new Touchpad(0, touchpadStyle);
        touchpad.setBounds(50, -100, touchpadRadius, touchpadRadius);
        touchpad.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float deltaX = ((Touchpad) actor).getKnobPercentX();
                float deltaY = ((Touchpad) actor).getKnobPercentY();
                p1.setMoving(true);
                //System.out.println("move");
                p1.setMoveVector(deltaX, deltaY);
                if (!((Touchpad) actor).isTouched()) {
                    //System.out.println("no move");
                    p1.setMoving(false);
                }
            }
        });
        stage.addActor(touchpad);
    }
    @Override
    public void dispose(){
        stage.dispose();
    }
}
