package com.adam.lumberjack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class LumberJack extends ApplicationAdapter {
	private BatchHelper batchHelper;
	//private SpriteBatch batch;
	private SpriteBatch shadeBatch;
	private SpriteBatch guiBatch;
	private SpriteBatch buildBatch;
	private CameraHelper cam;
	private OrthographicCamera guiCam;
	private Gui gui;
	private Stage stage;
	private Player p1;
	private SensorContactListener sensorContactListener;
	private Map map;
	private World world;
	private final int playgroundWidth = Constants.PLAYGROUND_WIDTH;
	private final int playgroundHeight = Constants.PLAYGROUND_HEIGHT;
	Box2DDebugRenderer debugRenderer;

	InputMultiplexer im;
	PlayerGestureListener playerGestureListener;
    PlayerInputProcessor playerInputProcessor;
    ShapeRenderer sr;
    State state = State.Running;
	Music music;
	@Override
	public void create () {
		System.out.println("creating new game instance");
		debugRenderer = new Box2DDebugRenderer();
		Box2D.init();
		world = new World(new Vector2(0,0), true);
		//Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
		sr = new ShapeRenderer();
		batchHelper = new BatchHelper(new SpriteBatch() , new Vector3(0.1f,0.1f,0.35f));
		shadeBatch = new SpriteBatch();
		buildBatch = new SpriteBatch();
		guiBatch = new SpriteBatch();

		p1 = new Player(world, MobType.Player,40,8, 100, 15, 3.5f, 5,
				new Texture( Gdx.files.internal("movingPlayer6.png")), 5, 2,
				1.5f,8.0f, new Hitbox(0.45f,0.2f ),
				new Texture( Gdx.files.internal("idleJack.png")), 10,
				new Texture( Gdx.files.internal("playerAttack10.png")), 10,
				new Texture( Gdx.files.internal("blockJack3.png")), 3,
				new TextureRegion(new Texture("playerDead.png")),
				null);
		sensorContactListener = new SensorContactListener(/*p1*/);
		world.setContactListener(sensorContactListener);
		Music music = Gdx.audio.newMusic(Gdx.files.internal("menu.mp3"));
		cam = new CameraHelper(new OrthographicCamera(), p1);
		guiCam = new OrthographicCamera();
		guiCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		gui = new Gui(p1, new Texture("healthBar.png"), new Texture("health.png"), new Texture("wood.png"), cam);
        map = new Map(world, new Texture("bg2.png"), new Texture("mountains.png"), new Texture("cliff.png"), p1, cam.getCamera());

		im = new InputMultiplexer();
		playerGestureListener = new PlayerGestureListener(p1, cam);
		playerInputProcessor = new PlayerInputProcessor(p1, cam.getCamera(), this);

	    Gdx.input.setInputProcessor(im);

        im.addProcessor(gui.getStage());
		im.addProcessor(new GestureDetector(playerGestureListener));
		im.addProcessor(playerInputProcessor);
	    GameUtils.initWorldBorders(playgroundWidth, playgroundHeight, world);
	    //GameUtils.creteDebugGridLayout(world);
		GameUtils.generateLoot(75, world);
		GameUtils.generateFences(75, world);
		GameUtils.generateBridges(75, world);
        //batch.setColor(0.1f,0.1f,0.35f,1f);
		buildBatch.setColor(1,1,1,0.3f );
        shadeBatch.setColor(0,0,0,0.3f );
        music.setLooping(true);
        music.play();
	}

	@Override
	public void render () {
		if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            if (state == State.Running) {
                state = State.Paused;
            } else {
                state = State.Running;
            }
			//state = State.Paused;
			Gdx.input.setCatchBackKey(true);
		}
		batchHelper.batch.setProjectionMatrix(cam.getCamera().combined);
		shadeBatch.setProjectionMatrix(cam.getCamera().combined);
		buildBatch.setProjectionMatrix(cam.getCamera().combined);
		guiBatch.setProjectionMatrix(guiCam.combined);
		//GameUtils.setCameraMovingRight(p1, cam);
		cam.tick();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batchHelper.batch.begin();
		shadeBatch.begin();
		buildBatch.begin();

		batchHelper.tick();
		map.drawAll(batchHelper.batch, shadeBatch, buildBatch);
		//debugRenderer.render(world, cam.getCamera().combined);
		buildBatch.end();
		shadeBatch.end();
		batchHelper.batch.end();

		switch(state){
			case Running:
				guiBatch.begin();
				gui.draw(guiBatch);
				guiBatch.end();
				gui.tick();
				map.tick();
				break;
			case Paused:
				//don't update
				break;
		}
	}
	
	@Override
	public void dispose () {
		batchHelper.batch.dispose();
		shadeBatch.dispose();
		guiBatch.dispose();
		gui.dispose();
		//p1.getTexture().dispose();
	}
	public void setState(State state) {
		this.state = state;
	}
	public State getState() {
		return this.state;
	}
}
