package com.adam.lumberjack;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public char data;
    //public Color color;
    public Node founder;
    public Node(char data) {
        this.data = data;
        //this.color = Color.White;
        this.founder = null;
    }
    public enum Color {
        White, Gray, Black
    }
}
