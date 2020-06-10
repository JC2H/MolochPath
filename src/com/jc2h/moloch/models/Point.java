package com.jc2h.moloch.models;

import com.jc2h.moloch.Configuration;
import processing.core.PVector;
import static processing.core.PApplet.abs;

public class Point{
    PVector position;
    public Point(float x, float y){
        this.position = new PVector(x,y);
    }

    public Point(PVector position){
        this.position = position.copy();
    }

    @Override
    public boolean equals(Object o) {
       if(o instanceof Point) {
           Point comp = (Point)o;
           return abs(comp.position.x - this.position.x) < Configuration.threshold && abs(comp.position.y - this.position.y) < Configuration.threshold;
       }
       else return false;
    }
}
