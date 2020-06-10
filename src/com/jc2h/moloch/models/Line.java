package com.jc2h.moloch.models;

import processing.core.PVector;

import java.util.List;

public class Line {
    Point a;
    Point b;

    public Line(PVector a, PVector b){
        this.a = new Point(a);
        this.b = new Point(b);
    }
    public Line(Point a, Point b){
        this.a = a;
        this.b = b;
    }


}
