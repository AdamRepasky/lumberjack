package com.adam.lumberjack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Map {
    private Texture background;
    private Texture mountain;
    private Texture cliff;
    private Player player;
    /*private List<GameObject> activeObjects;*/
    /*
    private List<GameObject> newObjects = new ArrayList<GameObject>();
    private List<GameObject> oldObjects = new ArrayList<GameObject>();
    */
    private List<Chunk> chunks;
    private List<Chunk> activeChunks = new ArrayList<Chunk>();
    private List<Mob> activeMobs = new ArrayList<Mob>();
    //private int centerChunkIndex = 0;
    private List<GameObject> activeObjects = new ArrayList<GameObject>();
    private OrthographicCamera camera;
    private World world;

    public Map(World world, Texture background, Texture mountain, Texture cliff, Player player, OrthographicCamera camera) {
        this.world = world;
        this.player = player;
        this.background = background;
        this.background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        this.mountain = mountain;
        this.mountain.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        this.cliff = cliff;
        this.cliff.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        //this.activeObjects = objects;
        this.camera = camera;
        this.chunks = GameUtils.generateTestChunks(this);
        //this.chunks = GameUtils.generateChunks(this);
        activateChunksNearPlayer();
        chunks.get(playerAtChunk()).addObject(player);
        player.setOwnerChunk(chunks.get(playerAtChunk()));
    }
    public int playerAtChunk() {
        return (int) player.getPos().x / Constants.CHUNK_SIZE;
    }
    public int objectAtChunk(GameObject o) {
        return (int) o.getPos().x / Constants.CHUNK_SIZE;
    }
    //if right == true dectivates left chunk and activates one more to the right and vice-versa
    public void activateNextChunks(boolean right){
        if (right)  {
            activeChunks.remove(chunks.get(playerAtChunk() - 2));
            activeChunks.add(chunks.get(playerAtChunk() + 1));
        } else {
            activeChunks.remove(chunks.get(playerAtChunk() + 2));
            activeChunks.add(chunks.get(playerAtChunk() - 1));
        }
    }
    public void activateChunksNearPlayer(){
        int playerAtChunk = playerAtChunk();
        activeChunks.clear();
        activeChunks.add(chunks.get(playerAtChunk - 1));
        activeChunks.add(chunks.get(playerAtChunk));
        activeChunks.add(chunks.get(playerAtChunk + 1));
    }
    public List<Chunk> getChunks() {return chunks;}
    public World getWorld() {
        return world;
    }
    public Mob getPlayer() {
        return this.player;
    }
    public Texture getBackground() {
        return background;
    }

    public void setBackground(Texture background) {
        this.background =background;
    }

    /*public List<GameObject> getObjects() {
        return Collections.unmodifiableList(activeObjects);
    }*/
    /*
    public void setObjects(List<GameObject> objects) {
        this.activeObjects = objects;
    }
    public void addObjects(List<GameObject> objects) {
        this.newObjects.addAll(objects);
    }
    public void removeObject(GameObject o){
        o.getBody().getWorld().destroyBody(o.getBody());
        this.oldObjects.remove(o);
    }
    public void removeObjects(List<GameObject> objects){
        this.oldObjects.addAll(objects);
    }

    public void updateAll(){
        Collections.sort(activeObjects);
        for (GameObject obj : activeObjects) {
            obj.update();
        }
    }
    public void addNewObjects() {
        activeObjects.addAll(newObjects);
        newObjects.clear();
    }
    public void removeOldObjects(){
        for (GameObject o : oldObjects) {
            world.destroyBody(o.getBody());
        }
        activeObjects.removeAll(oldObjects);
        oldObjects.clear();
    }*/
    public void tick(){
        //System.out.println(playerAtChunk());
        int playerAtChunk = playerAtChunk();
        /*
        activeObjects.clear();
        activeMobs.clear();
        for (int i = 0; i < activeChunks.size(); i++) {
            for (GameObject g : activeChunks.get(i).getObjects()) {
                activeObjects.add(g);
                if (g instanceof Mob) activeMobs.add((Mob) g);
            }
            //activeObjects.addAll(activeChunks.get(i).getObjects());
        }*/

        for (Chunk c : activeChunks) {
            c.updateAll();
            c.removeOldObjects();
            c.addNewObjects();
        }
        /*for (GameObject go : allwaysActiveObjects) {
            go.update();
        }*/
        //player.update();
        if (playerAtChunk != playerAtChunk() && playerAtChunk() != 0 && playerAtChunk != 0) {
            boolean right = playerAtChunk < playerAtChunk();
            //System.out.println(playerAtChunk());
            chunks.get(playerAtChunk).removeNow(player);
            chunks.get(playerAtChunk()).addNow(player);
            player.setOwnerChunk(chunks.get(playerAtChunk()));
            activateNextChunks(right);
        }
        world.step(1/60f, 6, 2);
    }
    public void drawAll(SpriteBatch batch, SpriteBatch shadeBatch, SpriteBatch buildBatch) {
        activeObjects.clear();
        activeMobs.clear();
        for (int i = 0; i < activeChunks.size(); i++) {
            for (GameObject g : activeChunks.get(i).getObjects()) {
                activeObjects.add(g);
                if (g instanceof Mob) activeMobs.add((Mob) g);
            }
            //activeObjects.addAll(activeChunks.get(i).getObjects());
        }

        batch.draw(this.mountain, - Gdx.graphics.getWidth() / 2 / 100, Constants.PLAYGROUND_HEIGHT - 0.2f, Constants.PLAYGROUND_WIDTH *2, 8, Constants.PLAYGROUND_WIDTH / 20,1, 0,0);
        batch.draw(this.background, - Gdx.graphics.getWidth() / 2 / 100, 0, Constants.PLAYGROUND_WIDTH *2, Constants.PLAYGROUND_HEIGHT, Constants.PLAYGROUND_WIDTH / 25, Constants.PLAYGROUND_HEIGHT / 6, 0, 0);
        batch.draw(this.cliff, - Gdx.graphics.getWidth() / 2 / 100, - Gdx.graphics.getHeight() / 2 / 100, Constants.PLAYGROUND_WIDTH *2,  Gdx.graphics.getHeight() / 100 /2 + 0.5f, Constants.PLAYGROUND_WIDTH / 10, 1, 0, 0);
        //player.draw(batch);
        /*for (Chunk c : activeChunks) {
            c.drawAll(batch);
        }*/
        for (GameObject go : activeObjects) {
            go.draw(batch);
        }
        //batch.setColor(1,1,1,0.3f);
        /*for (Mob mob : activeMobs) {
            mob.draw(shadeBatch);
        }*/
        //player.draw(shadeBatch);
        //batch.setColor(1,1,1,1.0f);
        if (player.getBuildMode()) {
            player.drawBuildSensors(buildBatch);
        }
    }
    /*public void drawAll(SpriteBatch batch){
        batch.draw(this.mountain, - Gdx.graphics.getWidth() / 2 / 100, Constants.PLAYGROUND_HEIGHT - 0.2f, Constants.PLAYGROUND_WIDTH *2, 8, Constants.PLAYGROUND_WIDTH / 20,1, 0,0);
        batch.draw(this.background, - Gdx.graphics.getWidth() / 2 / 100, 0, Constants.PLAYGROUND_WIDTH *2, Constants.PLAYGROUND_HEIGHT, Constants.PLAYGROUND_WIDTH / 25, Constants.PLAYGROUND_HEIGHT / 6, 0, 0);
        batch.draw(this.cliff, - Gdx.graphics.getWidth() / 2 / 100, - Gdx.graphics.getHeight() / 2 / 100, Constants.PLAYGROUND_WIDTH *2,  Gdx.graphics.getHeight() / 100 /2 + 0.5f, Constants.PLAYGROUND_WIDTH / 10, 1, 0, 0);

        Vector3 camPos = camera.position;
        int i = 0;
        int j = 0;
        for (GameObject obj : activeObjects) {
            j++;
            if (obj.getPos().x > camPos.x - camera.viewportWidth / 2 - obj.getWidth() && obj.getPos().x < camPos.x + camera.viewportWidth / 2 + obj.getWidth()) {
                obj.draw(batch);
                i++;
            }
        }
        batch.setColor(1,1,1,0.3f);
        for (GameObject obj : activeObjects) {
            if (obj instanceof Mob) {
                obj.draw(batch);
                i++;
                if (!((Mob) obj).isAlive()) {
                    GameUtils.zoomCameraToObject(obj, camera);
                }
            }
            j++;
        }
        batch.setColor(1,1,1,1.0f);
        System.out.println(i +" "+ j);
    }*/
}
