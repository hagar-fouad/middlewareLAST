package com.emw.ShapeDemo;

import java.util.Arrays;

public class Shape {
    private long x;
    private long y;
    private String type;
    private Shape next;
    private  Shape previous;
    private String[] userdata;

    public Shape(long x, long y, String type, Shape next, Shape previous, String[] userdata) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.next = next;
        this.previous = previous;
        this.userdata = userdata;
    }


    public String[] getUserdata() {
        return userdata;
    }

    public void setUserdata(String[] userdata) {
        this.userdata = userdata;
    }



    public Shape getNext() {
        return next;
    }

    public void setNext(Shape next) {
        this.next = next;
    }

    public Shape getPrevious() {
        return previous;
    }

    public void setPrevious(Shape previous) {
        this.previous = previous;
    }



    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Shape{" +
                "x=" + x +
                ", y=" + y +
                ", type='" + type + '\'' +
                ", next=" + next +
                ", previous=" + previous +
                ", userdata=" + Arrays.toString(userdata) +
                '}';
    }
}
