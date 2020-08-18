package com.adam.lumberjack;

import com.badlogic.gdx.math.Vector2;

public class BankObject {
    private GameObject object;
    private boolean available;
    private Vector2 coordinates;
    BankObject(GameObject object, boolean available) {
        this.object = object;
        this.available = available;
        this.coordinates = object.getBody().getPosition().cpy();
    }
    public GameObject getGameObject() {return object;}
    public boolean getAvailable() {return available;}
    public Vector2 getCoordinates() {return coordinates;}
    public void setAvailable(boolean available) {this.available = available;}

    public boolean equals(Loot l) {
        if (this.object == l) return true;
        return false;
    }
}


