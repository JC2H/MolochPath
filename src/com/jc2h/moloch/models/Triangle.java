package com.jc2h.moloch.models;

import processing.core.PVector;

import java.util.LinkedList;

public class Triangle {
    PVector a,b,c;
    LinkedList<Triangle> neighbours;
    public Triangle(PVector a,PVector b,PVector c){
        this.a = a;
        this.b = b;
        this.c = c;
        neighbours = new LinkedList<>();
    }
}
