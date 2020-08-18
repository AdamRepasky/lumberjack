package com.adam.lumberjack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

//TODO node methods kinda chaotic, think about removing
public class Chunk {
    private int chunkID;
    private List<GameObject> activeObjects = new ArrayList<GameObject>();
    private List<GameObject> newObjects = new ArrayList<GameObject>();
    private List<GameObject> oldObjects = new ArrayList<GameObject>();
    private Node[][] grid = new Node[Constants.CHUNK_SIZE * 4][Constants.CHUNK_SIZE * 4];
    //private boolean[][] grid = new boolean[Constants.CHUNK_SIZE * 4][Constants.CHUNK_SIZE * 4];
    private Map map;
    private World world;

    public Chunk(Map map) {
        this.world = map.getWorld();
        this.chunkID = GameUtils.setChunkID();
        for (int i = 0; i < Constants.CHUNK_SIZE * 4; i++) {
            for (int j = 0; j < Constants.CHUNK_SIZE * 4; j++) {
                grid[i][j] = new Node(' ');
            }
        }
        this.map = map;
    }
    public Vector2 getNodeCoordinates(Node node) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == node) {
                    return new Vector2(i, j);
                }
            }
        }
        return null;
    }
    public Vector2 getGridIndex(Vector2 source) { //actually just a vec2 floor()
        int x = (int) source.x;
        int y = (int) source.y;
        //System.out.println("indexes: " + x + " | " + y);
        return new Vector2(x,y);
    }
    public Node getNodeFromVector(Vector2 source) {
        source = getGridIndex(source);
        //System.out.println("indexes (node): " + source.x + " | " + source.y);
        return grid[(int) source.x][(int) source.y];
    }
    public Vector2 transformToGridIndexes(Vector2 source) {
        float x = source.x  / Constants.TILE_SIZE;
        float y = source.y / Constants.TILE_SIZE;
        //System.out.println("transformed: " + x + " | " + y);
        return new Vector2(x,y);
    }
    public Vector2 transformToGridCoordinates(Vector2 source) {
        float x = (source.x % Constants.CHUNK_SIZE);
        float y = (source.y % Constants.CHUNK_SIZE);
        //System.out.println("transformed: " + x + " | " + y);
        return new Vector2(x,y);
    }
    public void occupyNodesFromHitbox(Vector2 source, Hitbox hitbox) { //nodes of object //source ALWAYS should be left bottom vertex //but here it gets already rounded from util generator! -err
        //System.out.println("source: " + source + " " + hitbox.width + " " + hitbox.height);
        Vector2 destination = new Vector2(source.x + hitbox.width, source.y + hitbox.height); //ERROR not in grid coords! //now both in grid coords
        source = transformToGridCoordinates(source);
        source = transformToGridIndexes(source); //in grid coords
        //System.out.println("source transformed: " + source + " " + hitbox.width + " " + hitbox.height);
        //System.out.println("destination : " +  destination);

        //System.out.println(destination);
        destination = transformToGridCoordinates(destination);
        if (destination.x % Constants.TILE_SIZE != 0.0f) destination.x += Constants.TILE_SIZE;
        if (destination.y % Constants.TILE_SIZE != 0.0f) destination.y += Constants.TILE_SIZE;
        destination = transformToGridIndexes(destination);
        //System.out.println("destination transformed: " +  destination);
        source = getGridIndex(source);
        //System.out.println(destination);
        destination = getGridIndex(destination);
        //System.out.println("indexes: " + source + " " + destination);
        //System.out.println("############" + (int) Math.ceil(destination.x - source.x) + "#######################" + (int) Math.ceil(destination.y - source.y));
        occupyAllGridCells((int) source.x, (int) source.y, (int) Math.ceil(destination.x - source.x), (int) Math.ceil(destination.y - source.y), 'M'); //sizing is bad for sure
    }
    public void deoccupyNodesFromHitbox(Vector2 source, Hitbox hitbox) { //nodes of object //source ALWAYS should be left bottom vertex //but here it gets already rounded from util generator! -err
        //System.out.println("source: " + source + " " + hitbox.width + " " + hitbox.height);
        Vector2 destination = new Vector2(source.x + hitbox.width, source.y + hitbox.height); //ERROR not in grid coords! //now both in grid coords
        source = transformToGridCoordinates(source);
        source = transformToGridIndexes(source); //in grid coords
        destination = transformToGridCoordinates(destination);
        if (destination.x % Constants.TILE_SIZE != 0.0f) destination.x += Constants.TILE_SIZE;
        if (destination.y % Constants.TILE_SIZE != 0.0f) destination.y += Constants.TILE_SIZE;
        destination = transformToGridIndexes(destination);
        //System.out.println("destination transformed: " +  destination);
        source = getGridIndex(source);
        destination = getGridIndex(destination);
        //System.out.println("indexes: " + source + " " + destination);
        deoccupyAllGridCells((int) source.x, (int) source.y, (int) Math.ceil(destination.x - source.x), (int) Math.ceil(destination.y - source.y)); //sizing is bad for sure
    }
    public Node getNextStepBFS(Node from, Node to) { //will walk zig-zag using this
        Queue<Node> queue = new LinkedList<>();
        List<Node> visited = new ArrayList<>();
        queue.add(from);
        from.founder = from;
        while (!queue.isEmpty()) {
            Node current = queue.remove();
            if (current == to) {
                Node step = to;
                while (step.founder != from) {
                    step = step.founder;
                }
                //reset founders
                for (Node n : visited) {
                    n.founder = null;
                }
                while (!queue.isEmpty()) {
                    queue.remove().founder = null;
                }
                return step;
            }
            Vector2 coordinates = getNodeCoordinates(current);
            //System.out.println("coords: " + coordinates);
            if (coordinates.x != Constants.CHUNK_SIZE / Constants.TILE_SIZE - 1 && grid[(int) coordinates.x + 1][(int) coordinates.y].founder == null) {
                queue.add(grid[(int) coordinates.x + 1][(int) coordinates.y]);
                grid[(int) coordinates.x + 1][(int) coordinates.y].founder = current;
            }
            if (coordinates.x != 0 && grid[(int) coordinates.x - 1][(int) coordinates.y].founder == null) {
                queue.add(grid[(int) coordinates.x - 1][(int) coordinates.y]);
                grid[(int) coordinates.x - 1][(int) coordinates.y].founder = current;
            }
            if (coordinates.y != Constants.CHUNK_SIZE  / Constants.TILE_SIZE - 1 && grid[(int) coordinates.x][(int) coordinates.y + 1].founder == null) {
                queue.add(grid[(int) coordinates.x][(int) coordinates.y + 1]);
                grid[(int) coordinates.x][(int) coordinates.y + 1].founder = current;
            }
            if (coordinates.y != 0 && grid[(int) coordinates.x][(int) coordinates.y - 1].founder == null) {
                queue.add(grid[(int) coordinates.x][(int) coordinates.y - 1]);
                grid[(int) coordinates.x][(int) coordinates.y - 1].founder = current;
            }
            visited.add(current);
        }
        for (Node n : visited) {
            n.founder = null;
        }
        //reset founders
        return null;
    }

    public Node[][] getGrid() {
        return grid;
    }
    public void printGrid() {
        System.out.println("Printing table \n ------------------------");
        for (int i = grid.length - 1; i >= 0; i--){
            String s = String.valueOf(grid.length - i - 1);
            for (int x = s.length(); x < String.valueOf(grid.length).length(); x++) {
                s += " ";
            }
            System.out.print(s + "|");
            for (int j = 0; j < grid[i].length; j++){
                System.out.print(grid[j][i].data);

            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("-----------------------");
    }
    public boolean isFreeGridCell(int x, int y){
        //System.out.println("checking: " + x +" "+  y);
        //System.out.println("Checking at: " + x + "," + y);
        return !(grid.length <= x || grid.length <= y || y < 0 || x < 0 || this.grid[x][y].data != ' ');
    }
    public boolean areAllFreeGridCells(int x, int y, int width, int height){
        //System.out.println("Checking all free from: (x: " + x + " y: " + y + ") width: " + width + " height: " + height);
        for (int i = x; i < x + width; i++){
            for (int j = y; j < y + height; j++){
                if (!isFreeGridCell(i, j)) {
                    //System.out.println("All free check failed at " + x + " " + y);
                    return false;
                }
            }
        }
        return true;
    }
    public boolean occupyAllGridCells(int x, int y, int width, int height, char data){
        //System.out.println("occupying all: " + x + " " + y + " " + width + " " + height);
        for (int i = x; i < x + width; i++){
            for (int j = y; j < y + height; j++){
                occupyGridCell(i, j, data);
            }
        }
        //printGrid();
        return true;
    }
    public boolean occupyGridCell(int x, int y, char data){
        //System.out.println("occupying: " + x + " " + y);
        //Idk but somehow it needs to still check for overwrites
        if (grid.length <= x || grid[0].length <= y) {
            return false;
        }
        if (this.grid[x][y].data != ' ') {
            System.out.println(x + " " + y + "-overwrites");
            return false;
        }
        this.grid[x][y].data = data;
        return true;
    }
    public boolean deoccupyAllGridCells(int x, int y, int width, int height){
        for (int i = x; i < x + width; i++){
            for (int j = y; j < y + height; j++){
                deoccupyGridCell(i, j);
            }
        }
        return true;
    }
    public boolean deoccupyGridCell(int x, int y){
        if (grid.length <= x || grid[0].length <= y) {
            return false;
        }
        if (this.grid[x][y].data == ' ') return false;
        this.grid[x][y].data = ' ';
        return true;
    }

    public int getChunkID(){
        return chunkID;
    }
    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(activeObjects);
    }

    public void setObjects(List<GameObject> objects) {
        this.activeObjects = objects;
    }

    public void addObjects(List<GameObject> objects) {
        this.newObjects.addAll(objects);
    }
    public void addObject(GameObject object) {
        this.newObjects.add(object);
    }

    public void removeObject(GameObject o) {
        //o.getBody().getWorld().destroyBody(o.getBody());
        this.oldObjects.add(o);
        //this.activeObjects.remove(o);
    }

    public void removeObjects(List<GameObject> objects) {
        this.oldObjects.addAll(objects);
    }

    public void updateAll() {
        Collections.sort(activeObjects);
        for (int i = 0; i < activeObjects.size(); i++) {
            GameObject obj = activeObjects.get(i);
            //if (!obj.isAlive()) continue;
        //for (GameObject obj : activeObjects) {
            if (obj instanceof Loot && obj.getBody().getLinearVelocity().x != 0.0f && obj.getBody().getLinearVelocity().y != 0.0f) {
                deoccupyNodesFromHitbox(new Vector2(obj.getPos().x, obj.getPos().y), obj.getHitbox()); //DONOT USE GETPOS ERRRRROR!§ or maybe not man
                obj.update();
                occupyNodesFromHitbox(new Vector2(obj.getPos().x, obj.getPos().y), obj.getHitbox());
            } else if (obj instanceof Mob && ((Mob) obj).getMobType() == MobType.Ku) {
                if (obj.isAlive()) {
                    double distanceToPlayer = Math.sqrt(Math.pow(map.getPlayer().getBody().getPosition().x - obj.getBody().getPosition().x, 2) + Math.pow(map.getPlayer().getBody().getPosition().y - obj.getBody().getPosition().y, 2));
                    if (distanceToPlayer < 7) MobUtils.moveTo((Mob) obj, map.getPlayer());
                }
                //deoccupyNodesFromHitbox(new Vector2(obj.getBody().getPosition().x - obj.getHitbox().width / 2, obj.getBody().getPosition().y - obj.getHitbox().height / 2), obj.getHitbox());
                //deoccupyNodesFromHitbox(obj.getBody().getPosition(), obj.getHitbox());
                deoccupyNodesFromHitbox(new Vector2(obj.getPos().x, obj.getPos().y), obj.getHitbox()); //DONOT USE GETPOS ERRRRROR!§ or maybe not man
                /*Node targetNode = getNextStepBFS(getNodeFromVector(new Vector2(obj.getPos().x, obj.getPos().y)), grid[32][32]);
                if (targetNode != null) {
                    Vector2 coordinates = getNodeCoordinates(targetNode);
                    coordinates.x += Constants.TILE_SIZE / 2;
                    coordinates.y += Constants.TILE_SIZE / 2;
                    float xMoveVector = coordinates.x - obj.getPos().x;
                    float yMoveVector = coordinates.y - obj.getPos().y;
                    ((Mob) obj).setMoveVector(xMoveVector, yMoveVector);
                    ((Mob) obj).setMoving(true);
                }
                else ((Mob) obj).setMoving(false);*/
                int objAtChunk = this.map.objectAtChunk(obj);
                obj.update();
                if (objAtChunk != this.map.objectAtChunk(obj)) {
                    map.getChunks().get(objAtChunk).removeNow(obj);
                    map.getChunks().get(this.map.objectAtChunk(obj)).addNow(obj);
                    obj.setOwnerChunk(map.getChunks().get(map.objectAtChunk(obj)));
                }
                occupyNodesFromHitbox(new Vector2(obj.getPos().x, obj.getPos().y), obj.getHitbox());
            }
            else if (obj instanceof Mob && ((Mob) obj).getMobType() == MobType.Sheep) {
                if (obj.isAlive()) {
                    double distanceToPlayer = Math.sqrt(Math.pow(map.getPlayer().getBody().getPosition().x - obj.getBody().getPosition().x, 2) + Math.pow(map.getPlayer().getBody().getPosition().y - obj.getBody().getPosition().y, 2));
                    if (distanceToPlayer < 3) MobUtils.moveTo((Mob) obj, map.getPlayer().getBody().getPosition());
                }
                deoccupyNodesFromHitbox(new Vector2(obj.getPos().x, obj.getPos().y), obj.getHitbox()); //DONOT USE GETPOS ERRRRROR!§ or maybe not man
                int objAtChunk = this.map.objectAtChunk(obj);
                obj.update();
                if (objAtChunk != this.map.objectAtChunk(obj)) {
                    map.getChunks().get(objAtChunk).removeNow(obj);
                    map.getChunks().get(this.map.objectAtChunk(obj)).addNow(obj);
                    obj.setOwnerChunk(map.getChunks().get(map.objectAtChunk(obj)));
                }
                occupyNodesFromHitbox(new Vector2(obj.getPos().x, obj.getPos().y), obj.getHitbox());
            } else {
                deoccupyNodesFromHitbox(new Vector2(obj.getPos().x, obj.getPos().y), obj.getHitbox());
                obj.update();
                occupyNodesFromHitbox(new Vector2(obj.getPos().x, obj.getPos().y), obj.getHitbox());
            }
        }
    }
    public void addNow(GameObject o) {
        activeObjects.add(o);
    }
    /* removes object from chunk immediately, used to transfer player to other chunk when passing chunk borders */
    public void removeNow(GameObject o) {
        activeObjects.remove(o);
    }
    public void addNewObjects() {
        activeObjects.addAll(newObjects);
        newObjects.clear();
    }

    public void removeOldObjects() {
        activeObjects.removeAll(oldObjects);
        for (GameObject o : oldObjects) {
            deoccupyAllGridCells((int) ((o.getPos().x % Constants.CHUNK_SIZE) / Constants.TILE_SIZE), (int) ((o.getPos().y % Constants.CHUNK_SIZE) / Constants.TILE_SIZE), (int) (o.getHitbox().width / Constants.TILE_SIZE), (int) (o.getHitbox().height / Constants.TILE_SIZE));
            if (!(o instanceof Loot)) {
                world.destroyBody(o.getBody());
            } else {
                GameUtils.returnLootToBank((Loot) o);
                ((Loot) o).resetWaitTime();
                ((Loot) o).resetDespawnTime();
            }
        }
        oldObjects.clear();
    }

    public void drawAll(SpriteBatch batch) {
        int i = 0;

        for (GameObject obj : activeObjects) {
            obj.draw(batch);
            i++;
        }
        /*batch.setColor(1,1,1,0.3f);
        for (GameObject obj : activeObjects) {
            if (obj instanceof Mob) {
                obj.draw(batch);
                i++;
                if (!((Mob) obj).isAlive()) {
                    GameUtils.zoomCameraToObject(obj, camera);
                }
            }
        }
        batch.setColor(1,1,1,1.0f);*/
        //System.out.println(i);

    }
}

