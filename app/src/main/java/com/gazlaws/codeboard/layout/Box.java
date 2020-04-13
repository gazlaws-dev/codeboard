package com.gazlaws.codeboard.layout;

public class Box {
    public float x;
    public float y;
    public float width;
    public float height;

    public static Box create(float x, float y, float width, float height) {
        Box box = new Box();
        box.x = x;
        box.y = y;
        box.width = width;
        box.height = height;
        return box;
    }

    public float getArea(){
        return width * height;
    }

    public float getLeft(){
        return x;
    }

    public float getRight(){
        return x + width;
    }

    public float getTop(){
        return y;
    }

    public float getBottom(){
        return y + height;
    }
}
