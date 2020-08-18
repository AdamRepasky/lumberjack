package com.adam.lumberjack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public final class GameUtils {
    private static int chunkCounter = 0;
    private static List<BankObject> lootBank = new ArrayList<>();
    private static List<BankObject> fenceBank = new ArrayList<>();
    private static List<BankObject> bridgeBank = new ArrayList<>();
    private GameUtils(){}
    public static void reverseY(OrthographicCamera camera) {
        camera.setToOrtho(true, Gdx.graphics.getWidth() / 100, Gdx.graphics.getHeight() / 100);
    }
    public static Loot getLootFromBank(float x, float y, Chunk ownerChunk) {
        for (BankObject bo : lootBank) {
            if (bo.getAvailable()) {
                bo.getGameObject().getBody().setTransform(x, y, 0);
                bo.getGameObject().setPos(new Vector3(x - (bo.getGameObject().getHitbox().width / 2), y - (bo.getGameObject().getHitbox().height / 2), 0 ));
                bo.getGameObject().setOwnerChunk(ownerChunk);
                bo.setAvailable(false);
                return (Loot) bo.getGameObject();
            }
        }
        return null;
    }
    public static void returnLootToBank(Loot loot) {
        for (BankObject bo : lootBank) {
            if (bo.equals(loot)) {
                loot.getBody().setTransform(bo.getCoordinates(), 0);
                loot.getBody().setLinearVelocity(0.0f, 0.0f);
                loot.setPos(new Vector3(bo.getCoordinates().x, bo.getCoordinates().y, 0));
                bo.getGameObject().setOwnerChunk(null);
                bo.setAvailable(true);
            }
        }
    }
    public static Fence getFenceFromBank(float x, float y, Chunk ownerChunk) {
        for (BankObject bo : fenceBank) {
            if (bo.getAvailable()) {
                bo.getGameObject().getBody().setTransform(x, y, 0);
                bo.getGameObject().setPos(new Vector3(x - (bo.getGameObject().getHitbox().width / 2), y - (bo.getGameObject().getHitbox().height / 2), 0 ));
                bo.getGameObject().setOwnerChunk(ownerChunk);
                bo.setAvailable(false);
                return (Fence) bo.getGameObject();
            }
        }
        return null;
    }
    public static void returnFenceToBank(Fence fence) {
        for (BankObject bo : fenceBank) {
            if (bo.equals(fence)) {
                //System.out.println("loot in bank found!");
                //System.out.println(loot.getPos());
                fence.getBody().setTransform(bo.getCoordinates(), 0);
                fence.getBody().setLinearVelocity(0.0f, 0.0f);
                //System.out.println(loot.getPos());
                //System.out.println(bo.getCoordinates());
                fence.setPos(new Vector3(bo.getCoordinates().x, bo.getCoordinates().y, 0));
                bo.getGameObject().setOwnerChunk(null);
                bo.setAvailable(true);
            }
        }
    }
    public static Bridge getBridgeFromBank(float x, float y, Chunk ownerChunk) {
        for (BankObject bo : bridgeBank) {
            if (bo.getAvailable()) {
                bo.getGameObject().getBody().setTransform(x, y, 0);
                bo.getGameObject().setPos(new Vector3(x - (bo.getGameObject().getHitbox().width / 2), y - (bo.getGameObject().getHitbox().height / 2), 0 ));
                bo.getGameObject().setOwnerChunk(ownerChunk);
                bo.setAvailable(false);
                return (Bridge) bo.getGameObject();
            }
        }
        return null;
    }
    public static void returnBridgeToBank(Bridge bridge) {
        for (BankObject bo : bridgeBank) {
            if (bo.equals(bridge)) {
                //System.out.println("loot in bank found!");
                //System.out.println(loot.getPos());
                bridge.getBody().setTransform(bo.getCoordinates(), 0);
                bridge.getBody().setLinearVelocity(0.0f, 0.0f);
                //System.out.println(loot.getPos());
                //System.out.println(bo.getCoordinates());
                bridge.setPos(new Vector3(bo.getCoordinates().x, bo.getCoordinates().y, 0));
                bo.getGameObject().setOwnerChunk(null);
                bo.setAvailable(true);
            }
        }
    }
    public static final void initWorldBorders(float width, float height, World world) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = ObjectCategory.BORDER.getValue();
        fixtureDef.shape = new EdgeShape();
        ((EdgeShape) fixtureDef.shape).set(new Vector2(0,0), new Vector2(width, 0));
        BodyDef def1 = new BodyDef();
        def1.type = BodyDef.BodyType.StaticBody;
        def1.position.set(0, 0);
        world.createBody(def1).createFixture(fixtureDef);
        def1.position.set(0, height);
        world.createBody(def1).createFixture(fixtureDef);

        ((EdgeShape) fixtureDef.shape).set(new Vector2(0,0), new Vector2(0, height));
        def1.position.set(Constants.CHUNK_SIZE * 2, 0);
        world.createBody(def1).createFixture(fixtureDef);
        def1.position.set(width - Constants.CHUNK_SIZE, 0);
        world.createBody(def1).createFixture(fixtureDef);
    }
    public static final void creteDebugGridLayout(World world) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = new EdgeShape();
        ((EdgeShape) fixtureDef.shape).set(new Vector2(0,0), new Vector2(0, Constants.PLAYGROUND_HEIGHT));
        fixtureDef.isSensor = true;
        BodyDef def1 = new BodyDef();
        def1.type = BodyDef.BodyType.StaticBody;
        for (float i = 0.0f; i < Constants.PLAYGROUND_WIDTH; i += 0.25f) {
            def1.position.set(i, 0.0f);
            world.createBody(def1).createFixture(fixtureDef);
        }
        ((EdgeShape) fixtureDef.shape).set(new Vector2(0, 0), new Vector2(Constants.PLAYGROUND_WIDTH, 0));
        for (float j = 0.0f; j < Constants.PLAYGROUND_HEIGHT; j += 0.25f) {
            def1.position.set(0.0f, j);
            world.createBody(def1).createFixture(fixtureDef);
        }
    }
    public static final Vector2 roundDownToGrid(float x, float y) {
        x -= x % Constants.TILE_SIZE; //round down to tile coordinates (bottom-left) for example 18.25 or so
        y -= y % Constants.TILE_SIZE;

        return new Vector2(x, y);
    }
    private static final Vector2 findUnoccupiedCells(Chunk ownerChunk, float fromX, float fromY, float toX, float toY, Hitbox hitbox) {
        //System.out.println(fromX + " " + fromY + " " + toX + " " + toY);
        if (fromX > ownerChunk.getChunkID() * Constants.CHUNK_SIZE + Constants.CHUNK_SIZE || fromY > Constants.CHUNK_SIZE) return null; //out of chunk for sure
        if (fromX < ownerChunk.getChunkID() * Constants.CHUNK_SIZE) fromX = ownerChunk.getChunkID() * Constants.CHUNK_SIZE;
        if (fromY < 0) fromY = 0;
        if (toX > ownerChunk.getChunkID() * Constants.CHUNK_SIZE + Constants.CHUNK_SIZE) toX = ownerChunk.getChunkID() * Constants.CHUNK_SIZE + Constants.CHUNK_SIZE;
        if (toY > Constants.CHUNK_SIZE) toY = Constants.CHUNK_SIZE;
        //System.out.println(fromX + " " + fromY + " " + toX + " " + toY);
        Random r = new Random();
        float randomX;
        float randomY;
        //brute force search for any big enough free space, will stop if it can't find any after limitMax tries
        int limit = 0;
        int limitMax = 10;
        do {
            if (limit == limitMax) return null;
            randomX = fromX + r.nextFloat() * (toX - fromX);
            randomY = fromY + r.nextFloat() * (toY - fromY);
            randomX = randomX - (randomX % Constants.TILE_SIZE); //round down to tile coordinates (bottom-left) for example 18.25 or so
            randomY = randomY - (randomY % Constants.TILE_SIZE);
            limit++;
        } while (!ownerChunk.areAllFreeGridCells((int) ((randomX % Constants.CHUNK_SIZE) / Constants.TILE_SIZE), (int) ((randomY % Constants.CHUNK_SIZE) / Constants.TILE_SIZE), (int) (hitbox.width / Constants.TILE_SIZE), (int) (hitbox.height / Constants.TILE_SIZE)));
        return new Vector2(randomX, randomY);
    }
    private static final Vector2 findAndOccupyCells(Chunk ownerChunk, float fromX, float fromY, float toX, float toY, Hitbox hitbox, char data) {
        Vector2 freeSpace = findUnoccupiedCells(ownerChunk, fromX, fromY, toX, toY, hitbox);
        //System.out.println(freeSpace);
        if (freeSpace == null) return null;
        ownerChunk.occupyAllGridCells((int) ((freeSpace.x % Constants.CHUNK_SIZE) / Constants.TILE_SIZE), (int) ((freeSpace.y % Constants.CHUNK_SIZE) / Constants.TILE_SIZE), (int) (hitbox.width /Constants.TILE_SIZE), (int) (hitbox.height / Constants.TILE_SIZE), data);
        return freeSpace;
    }
    public static final List<GameObject> generateTrees(int count, float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        Texture trees = new Texture("trees2.png");

        TextureRegion[][] regions = TextureRegion.split(trees, trees.getWidth() / 8, trees.getHeight() / 2);
        List<GameObject> objects = new ArrayList<GameObject>();
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 2, Constants.TILE_SIZE);



        for (int i = 0; i < count; i++) {
            Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'T');
            if (freeSpace == null) break;
            int randomTRIndex = ThreadLocalRandom.current().nextInt(0, 7);
            System.out.println("generated: "+freeSpace);
            System.out.println("modified bottom left x:"+(freeSpace.x + hitbox.width / 2));
            System.out.println("modified bottom left y:"+(freeSpace.y + hitbox.height / 2));
            Tree tree = new Tree(world, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, regions[0][randomTRIndex], regions[0][7], 4.0f, 17.0f, hitbox ,100, 3, ownerChunk);
            objects.add(tree);
            System.out.println("new tree pos: " +tree.getPos()); //pos may differ, may not be left bottom of body err
        }
        System.out.println("Generated " + objects.size() + " objects out of total " + count + ".");
        return objects;
    }
    public static final List<GameObject> generateForests(int count, float fromX, float fromY, float toX, float toY, float w, float h, World world, Chunk ownerChunk) {
        List<GameObject> objects = new ArrayList<GameObject>();
        for (int i = 0; i < count; i++) {
            Random r = new Random();

            float randomX1 = fromX + r.nextFloat() * (toX - fromX);
            float randomY1 = fromY + r.nextFloat() * (toY - fromY);
            float randomX2 = randomX1 + r.nextFloat() * w;
            float randomY2 = randomY1 + r.nextFloat() * h;
            if (randomY2 > toY) {
                randomY2 = toY;
            }
            objects.addAll(generateTrees((int) (w * (randomY2 - randomY1) / 3), randomX1, randomY1, randomX2, randomY2, world, ownerChunk));
        }
        return objects;
    }
    public static final List<GameObject> generateStones(int count, float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        Texture stones = new Texture("stones4.png");

        TextureRegion[][] regions = TextureRegion.split(stones, stones.getWidth() / 5, stones.getHeight());
        List<GameObject> objects = new ArrayList<GameObject>();
        for (int i = 0; i < count; i++) {
            int randomSize = ThreadLocalRandom.current().nextInt(1, 4);
            Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 2 * randomSize, Constants.TILE_SIZE * randomSize);
            Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'S');
            if (freeSpace == null) break;
            int randomStone = ThreadLocalRandom.current().nextInt(0, 4);
            objects.add(new Stone(world, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, regions[0][randomStone], 1.8f, 2.0f, hitbox,100, 100, ownerChunk));
            //objects.add(new Tree(world, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, regions[0][randomTRIndex], regions[0][7], 4.0f, 17.0f, hitbox ,100, 3, ownerChunk));
            //objects.addAll(generateMinorInactiveStones(10, randomX - randomSize1,  randomY - randomSize2, randomX +  randomSize1, randomY + randomSize2 / 2, world, ownerChunk));
        }
        System.out.println("Generated " + objects.size() + " objects out of total " + count + ".");
        return objects;
    }
    public static final List<GameObject> generateMinorInactiveStones(int count, float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        Texture stones = new Texture("stones4.png");

        TextureRegion[][] regions = TextureRegion.split(stones, stones.getWidth() / 5, stones.getHeight());
        List<GameObject> objects = new ArrayList<GameObject>();
        for (int i = 0; i < count; i++) {
            Random r = new Random();
            float randomX = fromX + r.nextFloat() * (toX - fromX);
            float randomY = fromY + r.nextFloat() * (toY - fromY);

            int randomStone = ThreadLocalRandom.current().nextInt(0, 4);
            float randomSize1 = 0.05f + r.nextFloat() * (0.20f - 0.05f);
            float randomSize2 = 0.05f + r.nextFloat() * (0.20f - 0.05f);

            Stone s = new Stone(world, randomX, randomY, regions[0][randomStone], 1.8f, 2.0f, new Hitbox(3 * randomSize1, randomSize2),100, 100, ownerChunk);
            s.getBody().getFixtureList().first().setSensor(true);
            s.setDrawPriority(0);
            objects.add(s);
        }
        return objects;
    }

    public static final List<GameObject> generateBearTraps(int count, float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        Texture trap = new Texture("bearTrap.png");

        TextureRegion[][] regions = TextureRegion.split(trap, trap.getWidth() / 2, trap.getHeight());
        List<GameObject> objects = new ArrayList<GameObject>();
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 4, Constants.TILE_SIZE * 2);
        for (int i = 0; i < count; i++) {
            Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'B');
            if (freeSpace == null) break;
            objects.add(new Trap(world, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2,regions[0][0], regions[0][1], 1.3f, 1.3f, hitbox,100, 30, ownerChunk));
            }
        System.out.println("Generated " + objects.size() + " objects out of total " + count + ".");
        return objects;
    }
    public static final List<GameObject> generateMushroom(int count, float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        TextureRegion[] mushroomTextureRegions = new TextureRegion[]{
                new TextureRegion(new Texture("healthMushroom.png")),
                new TextureRegion(new Texture("poisonMushroom.png")),
                new TextureRegion(new Texture("magicMushroom.png")),
        };
        //TextureRegion[][] regions = TextureRegion.split(trap, trap.getWidth() / 2, trap.getHeight());
        List<GameObject> objects = new ArrayList<GameObject>();
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
        for (int i = 0; i < count; i++) {
            Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'F');
            if (freeSpace == null) break;
            int randomShroom = ThreadLocalRandom.current().nextInt(0, 3);
            objects.add(new Mushroom(mushroomTextureRegions[randomShroom], 1.3f, 1.3f,  world, hitbox, 1, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, ownerChunk, randomShroom));
        }
        System.out.println("Generated " + objects.size() + " objects out of total " + count + ".");
        return objects;
    }
    public static final void generateLoot(int count, World world) {
        TextureRegion wood = new TextureRegion(new Texture("wood.png"));
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
        for (int i = 0; i < count; i++) {
            lootBank.add(new BankObject(new Loot(wood, 1.0f, 1.0f,  world, hitbox, 1, 0, hitbox.height * i, null), true));
            //objects.add(new Loot(wood, 1.0f, 1.0f,  world, hitbox, 1, 0, hitbox.height * i, null));
        }
    }
    public static final void generateFences(int count, World world) {
        TextureRegion fenceTexture = new TextureRegion(new Texture("fence.png"));
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 3, Constants.TILE_SIZE * 1);
        for (int i = 0; i < count; i++) {
            fenceBank.add(new BankObject(new Fence(world, 0 + Constants.TILE_SIZE * 2, hitbox.height * i, fenceTexture, fenceTexture, 1.2f, 3.0f, hitbox, 1, 0, null), true));
        }
    }
    public static final void generateBridges(int count, World world) {
        TextureRegion fenceTexture = new TextureRegion(new Texture("bridge.png"));
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 16, Constants.TILE_SIZE * 8);
        for (int i = 0; i < count; i++) {
            fenceBank.add(new BankObject(new Fence(world, 0 + Constants.TILE_SIZE * 2, hitbox.height * i, fenceTexture, fenceTexture, 1.2f, 3.0f, hitbox, 1, 0, null), true));
        }
    }
    public static final GameObject generateFinishHut(float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        TextureRegion hutTexture = new TextureRegion(new Texture("hut.png"));
        TextureRegion hutOpen = new TextureRegion(new Texture("hut_open.png"));
        Texture smoke = new Texture("smoke.png");

        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 25, Constants.TILE_SIZE * 7);
        Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'H');
        if (freeSpace == null) return null;
        GameObject hut = new Hut(hutTexture, hutOpen, smoke, 1.2f, 3.5f,  world, hitbox, 1, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, ownerChunk);
        System.out.println("Generated hut.");
        return hut;
    }
    public static final List<GameObject> dropLoot(int count, float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        //TextureRegion wood = new TextureRegion(new Texture("wood.png"));
        List<GameObject> objects = new ArrayList<GameObject>();
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
        for (int i = 0; i < count; i++) {
            //Vector2 freeSpace = findUnoccupiedCells(ownerChunk, fromX, fromY, toX, toY, hitbox);
            //System.out.println(freeSpace);
            //if (freeSpace == null) break;
            //ownerChunk.occupyNodesFromHitbox(fromX, fromY, hitbox);
            Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'L');
            if (freeSpace == null) break;
            //objects.add(new Loot(wood, 1.0f, 1.0f,  world, hitbox, 1,freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, ownerChunk));

            Loot l = getLootFromBank(freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, ownerChunk);
            //motion on spawning
            /*Random r = new Random();
            float fromXvelocity = 20.0f;
            float toXvelocity = 30.0f;
            float fromYvelocity = 20.0f;
            float toYvelocity = 30.0f;
            float randomXvelocity = fromXvelocity + r.nextFloat() * (toXvelocity - fromXvelocity);
            float randomYvelocity = fromYvelocity + r.nextFloat() * (toYvelocity - fromYvelocity);
            boolean neg = r.nextBoolean();
            if (neg) {
                randomXvelocity *= -1;
            }
            neg = r.nextBoolean();
            if (neg) {
                randomYvelocity *= -1;
            }
            l.getBody().setLinearVelocity(randomXvelocity, randomYvelocity);
            */
            objects.add(l);
        }
        return objects;
    }
    public static final List<GameObject> putDownFence(float x, float y, World world, Chunk ownerChunk) {
        List<GameObject> objects = new ArrayList<GameObject>();
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 3, Constants.TILE_SIZE * 1);
        //Vector2 freeSpace = OccupyCells(ownerChunk, x, y, x + hitbox.width, y + hitbox.height, hitbox, 'F');
        //if (freeSpace == null) return null;
        x -= x % Constants.TILE_SIZE; //round down to tile coordinates (bottom-left) for example 18.25 or so
        y -= y % Constants.TILE_SIZE;
        Fence f = getFenceFromBank(x + hitbox.width / 2, y + hitbox.height / 2, ownerChunk);
        objects.add(f);
        return objects;
    }
    public static final List<GameObject> putDownBridge(float x, float y, World world, Chunk ownerChunk) {
        List<GameObject> objects = new ArrayList<GameObject>();
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 16, Constants.TILE_SIZE * 8);
        //Vector2 freeSpace = OccupyCells(ownerChunk, x, y, x + hitbox.width, y + hitbox.height, hitbox, 'F');
        //if (freeSpace == null) return null;
        x -= x % Constants.TILE_SIZE; //round down to tile coordinates (bottom-left) for example 18.25 or so
        y -= y % Constants.TILE_SIZE;
        Bridge f = getBridgeFromBank(x + hitbox.width / 2, y + hitbox.height / 2, ownerChunk);
        objects.add(f);
        return objects;
    }
    public static final List<GameObject> generateRandomFences(int count, float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {

        TextureRegion fenceTexture = new TextureRegion(new Texture("fence.png"));
        List<GameObject> objects = new ArrayList<GameObject>();
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 3, Constants.TILE_SIZE * 1);
        for (int i = 0; i < count; i++) {
            Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'F');
            if (freeSpace == null) break;
            objects.add(new Fence(world, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, fenceTexture, fenceTexture, 1.2f, 3.0f, hitbox, 1, 0, ownerChunk));
        }
        System.out.println("Generated " + objects.size() + " objects out of total " + count + ".");
        return objects;
    }
    public static final int setChunkID(){
        return chunkCounter++;
    }
    public static final Chunk generateChunk(Map map, int leftBorder) {

        Chunk c = new Chunk(map);
        c.addObjects(GameUtils.generateStones( 5, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
        c.addObjects(GameUtils.generateMinorInactiveStones(20,leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
        c.addObjects(GameUtils.generateBearTraps(3,leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
        c.addObjects(GameUtils.generateMobs(3,leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
        c.addObjects(GameUtils.generateMushroom( 5, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));

        //c.addObjects(GameUtils.generateForests(0,100,leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, 50, 20, map.getWorld()));

        c.addObjects(GameUtils.generateTrees(15, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));

        return c;
    }
    public static final Chunk generateFinishChunk(Map map, int leftBorder) {

        Chunk c = new Chunk(map);

        if (leftBorder != 0) {
            c.addObject(GameUtils.generateFinishHut(leftBorder, Constants.TILE_SIZE * 2, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
            c.addObjects(GameUtils.generateTrees(10, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
            c.addObjects(GameUtils.generateMushroom( 10, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
            c.addObjects(GameUtils.generateRandomFences( 4, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
        }
        //c.printGrid();
        return c;
    }
    public static final Chunk generateTestChunk(Map map, int leftBorder) {

        Chunk c = new Chunk(map);

        if (leftBorder != 0) {
            List<GameObject> rivers = GameUtils.generateVerticalRiver( leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c);
            if (rivers != null) c.addObjects(rivers);
            //c.addObjects(GameUtils.generateStones(15, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
            //c.addObjects(GameUtils.generateBearTraps(3,leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
            c.addObjects(GameUtils.generateTrees(30, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
            //c.addObjects(GameUtils.generateMushroom( 10, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
            //c.addObjects(GameUtils.generateSheep(4, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
            //c.addObjects(GameUtils.generateKu(2, leftBorder, 0, leftBorder + Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, map.getWorld(), c));
        }

        //c.printGrid();
        return c;
    }
    public static final List<Chunk> generateChunks(Map map) {
        List<Chunk> c = new ArrayList<Chunk>();
        for (int i = 0; i < Constants.PLAYGROUND_WIDTH; i += Constants.CHUNK_SIZE) {
            c.add(generateChunk(map, i));
        }
        return c;
    }
    public static final List<Chunk> generateTestChunks(Map map) {
        List<Chunk> c = new ArrayList<Chunk>();
        int i;
        for (i = 0; i < Constants.PLAYGROUND_WIDTH ; i += Constants.CHUNK_SIZE) {
            if (i != Constants.PLAYGROUND_WIDTH - Constants.CHUNK_SIZE) {
                c.add(generateTestChunk(map, i));
            } else {
                c.add(generateFinishChunk(map, i));
            }
        }
        //c.add(generateFinishChunk(map, i + Constants.CHUNK_SIZE));
        return c;
    }
    public static final List<GameObject> generateKu(int count, float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        List<GameObject> objects = new ArrayList<GameObject>();
        Texture moving = new Texture( Gdx.files.internal("movingKu.png"));
        Texture idle = new Texture( Gdx.files.internal("idleKu.png"));
        Texture attack = new Texture( Gdx.files.internal("attackKu.png"));
        Texture dead = new Texture( Gdx.files.internal("deadKu.png"));
        for (int i = 0; i < count; i++) {
            Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 2, Constants.TILE_SIZE);
            Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'M');
            if (freeSpace == null) break;
            Ku ku = new Ku(world, MobType.Ku, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, 40, 1, 3.5f, 4,
                    moving, 3, 2,
                    1.5f,8.0f, hitbox,
                    idle, 3,
                    attack, 3,
                    new TextureRegion(dead),
                    ownerChunk);
            ownerChunk.occupyNodesFromHitbox(freeSpace, hitbox);
            objects.add(ku);
            }
        /*moving.dispose();
        idle.dispose();
        attack.dispose();
        dead.dispose();*/
        System.out.println("Generated " + objects.size() + " objects out of total " + count + ".");
        return objects;
    }
    public static final List<GameObject> generateVerticalRiver(float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        Texture rivTex = new Texture("river3.png");
        TextureRegion[] riverTexture = TextureRegion.split(rivTex, rivTex.getWidth() / 5, rivTex.getHeight())[0];
        Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 16, Constants.TILE_SIZE * 8);

        //Place initial river part at top (source)
        Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, toY - hitbox.height, toX, toY, hitbox, 'R');
        if (freeSpace == null) return null;

        List<GameObject> rivers = new ArrayList<>();
        //rivers.add(new River(riverTexture[1], 1.0f, 1.0f,  world, hitbox, 1, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, ownerChunk));
        Random r = new Random();
        boolean canTurn = false;
        int curve;
        for (float i = 0; i < Constants.CHUNK_SIZE; i += hitbox.height) {
            //Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'R');
            //if (freeSpace == null) return null;
            System.out.println(i);
            //move left right..

            //1 for left turn, 2 for right turn
            if (canTurn) {
                curve = r.nextInt(3);
            } else {
                curve = 0;
            }
            if (curve == 1) {
                rivers.add(new River(riverTexture[4], 1.0f, 1.0f,  world, hitbox, 1, freeSpace.x + hitbox.width / 2, freeSpace.y - i + hitbox.height / 2, ownerChunk));
                freeSpace.x -= hitbox.width;
                rivers.add(new River(riverTexture[2], 1.0f, 1.0f,  world, hitbox, 1, freeSpace.x + hitbox.width / 2, freeSpace.y - i + hitbox.height / 2, ownerChunk));
                canTurn = false;
            } else if (curve == 2) {
                rivers.add(new River(riverTexture[3], 1.0f, 1.0f,  world, hitbox, 1, freeSpace.x + hitbox.width / 2, freeSpace.y - i + hitbox.height / 2, ownerChunk));
                freeSpace.x += hitbox.width;
                rivers.add(new River(riverTexture[1], 1.0f, 1.0f,  world, hitbox, 1, freeSpace.x + hitbox.width / 2, freeSpace.y - i + hitbox.height / 2, ownerChunk));
                canTurn = false;
            } else {
                rivers.add(new River(riverTexture[0], 1.0f, 1.0f,  world, hitbox, 1, freeSpace.x + hitbox.width / 2, freeSpace.y - i + hitbox.height / 2, ownerChunk));
                canTurn = true;
            }
        }
        return rivers;
    }
    public static final List<GameObject> generateMobs(int count, float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        List<GameObject> objects = new ArrayList<GameObject>();
        Texture moving = new Texture( Gdx.files.internal("movingKu.png"));
        Texture idle = new Texture( Gdx.files.internal("idleKu.png"));
        Texture attack = new Texture( Gdx.files.internal("attackKu.png"));
        Texture dead = new Texture( Gdx.files.internal("deadKu.png"));
        for (int i = 0; i < count; i++) {
            Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 2, Constants.TILE_SIZE);
            Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'M');
            System.out.println("generated: "+freeSpace);
            System.out.println("modified bottom left x:"+(freeSpace.x + hitbox.width / 2));
            System.out.println("modified bottom left y:"+(freeSpace.y + hitbox.height / 2));

            if (freeSpace == null) break;
            Mob ku = new Mob(world, MobType.Ku, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, 40, 5, 3.5f, 4,
                    moving, 3, 2,
                    1.5f,8.0f, hitbox,
                    idle, 3,
                    attack, 3,
                    new TextureRegion(dead),
                    ownerChunk);
            ownerChunk.occupyNodesFromHitbox(freeSpace, hitbox);
            System.out.println("new ku pos:ku" +ku.getPos()); //pos may differ, may not be left bottom of body err
            objects.add(ku);
        }
        /*moving.dispose();
        idle.dispose();
        attack.dispose();
        dead.dispose();*/
        System.out.println("Generated " + objects.size() + " objects out of total " + count + ".");
        return objects;
    }
    public static final List<GameObject> generateSheep(int count, float fromX, float fromY, float toX, float toY, World world, Chunk ownerChunk) {
        List<GameObject> objects = new ArrayList<GameObject>();
        Texture moving = new Texture( Gdx.files.internal("sheep_moving.png"));
        Texture idle = new Texture( Gdx.files.internal("sheep_idle.png"));
        Texture attack = new Texture( Gdx.files.internal("sheep_idle.png"));
        Texture dead = new Texture( Gdx.files.internal("sheep_dead.png"));
        for (int i = 0; i < count; i++) {
            Hitbox hitbox = new Hitbox(Constants.TILE_SIZE * 2, Constants.TILE_SIZE);
            Vector2 freeSpace = findAndOccupyCells(ownerChunk, fromX, fromY, toX, toY, hitbox, 'M');
            if (freeSpace == null) break;
            Sheep sheep = new Sheep(world, MobType.Sheep, freeSpace.x + hitbox.width / 2, freeSpace.y + hitbox.height / 2, 20, 0, 3.5f, 4,
                    moving, 3, 1,
                    3.0f,4.0f, hitbox,
                    idle, 6,
                    attack, 6,
                    new TextureRegion(dead),
                    ownerChunk);
            ownerChunk.occupyNodesFromHitbox(freeSpace, hitbox);
            objects.add(sheep);
        }
        System.out.println("Generated " + objects.size() + " objects out of total " + count + ".");
        return objects;
    }
}
