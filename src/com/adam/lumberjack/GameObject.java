package com.adam.lumberjack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.List;

public abstract class GameObject implements Comparable<GameObject> {

    private Chunk ownerChunk;
    protected Integer[] activeTextureIndex = new Integer[2]; //like pair
    protected TextureRegion[][] regions;
    protected int drawPriority = 2;
    protected Vector3 pos;
    protected float width; //texture width
    protected float height; //texture height
    protected int maxHealth;
    protected int currentHealth;

    protected boolean alive = true;
    protected Body body;
    protected Fixture fixture;
    protected FixtureDef fixtureDef;
    protected Hitbox hitbox;
/*
hWidth is in percentage of width, hHeight too
 */
    public GameObject(Texture texture, int cols, int rows, float widthRatio, float heightRatio, World world, Hitbox hitbox, int maxHealth, float x, float y, Chunk ownerChunk) {
        if (rows == 1) {
            this.regions = TextureRegion.split(texture, texture.getWidth() / cols, texture.getHeight());
        } else {
            this.regions = TextureRegion.split(texture, texture.getWidth() / cols, texture.getHeight() / rows);
        }
        this.activeTextureIndex[0] = 0;
        this.activeTextureIndex[1] = 0;
        //texture.dispose();
        this.width = hitbox.width * widthRatio;
        this.height = hitbox.height * heightRatio;
        this.hitbox = hitbox;
        createBody(world, x, y);
        this.pos = new Vector3(body.getPosition().x - width / 2 /*- hitbox.width / 2*/, body.getPosition().y - hitbox.height / 2, 0);

        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.ownerChunk = ownerChunk;
    }
    //for trees this is good
    public GameObject(TextureRegion alive, TextureRegion dead, float widthRatio, float heightRatio, World world, Hitbox hitbox, int maxHealth, float x, float y, Chunk ownerChunk) {
        this.regions = new TextureRegion[1][2];
        regions[0][0] = alive;
        regions[0][1] = dead;
        this.activeTextureIndex[0] = 0;
        this.activeTextureIndex[1] = 0;
        //texture.dispose();
        this.width = hitbox.width * widthRatio;
        this.height = hitbox.height * heightRatio;
        this.hitbox = hitbox;
        createBody(world, x, y);
        this.pos = new Vector3(body.getPosition().x - width / 2, body.getPosition().y - hitbox.height / 2, 0);

        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.ownerChunk = ownerChunk;
    }

    public GameObject(TextureRegion texture, float widthRatio, float heightRatio, World world, Hitbox hitbox, int maxHealth, float x, float y, Chunk ownerChunk) {
        this.regions = new TextureRegion[1][1];
        regions[0][0] = texture;
        this.activeTextureIndex[0] = 0;
        this.activeTextureIndex[1] = 0;
        //texture.dispose();
        this.width = hitbox.width * widthRatio;
        this.height = hitbox.height * heightRatio;
        this.hitbox = hitbox;
        createBody(world, x, y);// <---loot problem
        this.pos = new Vector3(body.getPosition().x - width / 2, body.getPosition().y - hitbox.height / 2, 0);

        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.ownerChunk = ownerChunk;
    }
    public Chunk getOwnerChunk() {
        return ownerChunk;
    }

    public void setOwnerChunk(Chunk ownerChunk) {
        this.ownerChunk = ownerChunk;
    }
    protected void createBody(World world, float x, float y) {
    }

    public int getDrawPriority() {
        return drawPriority;
    }

    public void setDrawPriority(int drawPriority) {
        this.drawPriority = drawPriority;
    }
    public Hitbox getHitbox() {
        return hitbox;
    }
    public Body getBody() {
        return body;
    }
    public Fixture getFixture() {
        return fixture;
    }
    public TextureRegion getActiveTexture() {
        return regions[activeTextureIndex[0]][activeTextureIndex[1]];
    }
    public void setActiveTextureIndex(int fst, int snd) {
        this.activeTextureIndex[0] = fst;
        this.activeTextureIndex[1] = snd;
    }

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(int health) { currentHealth = health; }
    public float getWidth() {return width; }
    public void setWidth(int width) {this.width = width; }
    public float getHeight() {return height; }
    public void setHeight(int height) {this.height = height; }
    public boolean isAlive() {return alive; }
    public void update() {}
    public void draw(SpriteBatch batch) {
        batch.draw(this.getActiveTexture(), this.getPos().x, this.getPos().y
                , this.getWidth(), this.getHeight());
    }
    public void destroy() {
        this.alive = false;
        currentHealth = 0;
    }
    public void takeDamage(int amount) {
        if (!alive) return;
        this.currentHealth -= amount;
        if (currentHealth <= 0) {
            this.destroy();
        }
    }

    @Override
    public int compareTo(GameObject gameObject) {
        if (this.drawPriority > gameObject.getDrawPriority()) {
            return 1;
        } else if (this.drawPriority < gameObject.getDrawPriority()) {
            return -1;
        } else if (this.pos.y > gameObject.getPos().y) {
            return -1;
        } else if (this.pos.y < gameObject.getPos().y) {
            return 1;
        } else if (this.pos.x == gameObject.getPos().x) {
            return 0;
        } else {
            return this.pos.x < gameObject.getPos().x ? 1 : -1; //later decide which is better
        }
    }
}

