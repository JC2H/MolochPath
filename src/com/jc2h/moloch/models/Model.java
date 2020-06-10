package com.jc2h.moloch.models;

import com.jc2h.moloch.controllers.Controller;
import com.jc2h.moloch.utils.Node;
import com.jc2h.moloch.views.View;
import com.sun.org.apache.xpath.internal.operations.Mod;
import processing.core.PVector;

public class Model extends Node<Model>{
    private float theta;
    private float w;

    private PVector position;
    private PVector velocity;
    private PVector acceleration;
    private float a;

    public Controller controller;
    public View view;

    public Model(){
        super();
        initState();
    }
    public Model(Model parent){
        super(parent);
        initState();
    }
    public Model(Model parent, Controller controller, View view){
        super(parent);
        this.parent = parent;
        this.controller = controller;
        this.view = view;
        initState();
    }
    public Model(Model parent, Controller controller){
        super(parent);
        this.parent = parent;
        this.controller = controller;
        initState();
    }
    public Model(Model parent, View view){
        super(parent);
        this.parent = parent;
        this.view  = view;
        initState();
    }
    public Model(Controller controller, View view){
        super();
        this.controller = controller;
        this.view = view;
        initState();
    }
    public Model(Controller controller){
        super();
        this.controller = controller;
        initState();
    }
    public Model(View view){
        super();
        this.view  = view;
        initState();
    }

    private void initState(){
        theta = 0;
        w = 0;
        position = new PVector(0,0,0);
        velocity = new PVector(0,0,0);
        acceleration = new PVector(0,0,0);
    }
    public float getTheta() {
        return theta;
    }

    public float getOmega() {
        return w;
    }

    public float getAlpha() {
        return a;
    }

    public PVector getPosition() {
        return position;
    }

    public PVector getVelocity() {
        return velocity;
    }

    public PVector getAcceleration() {
        return velocity;
    }

    public void setTheta(float theta) {
        this.theta =theta;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void setAlpha(float a) {
        this.a = a;
    }

    public void setPosition(PVector p) {
        this.position.set(p);
    }
    public void bindPosition(PVector ref){
        this.position = ref;
    }

    public void setVelocity(PVector v) {
        this.velocity.set(v);
    }

    public void setAcceleration(PVector a) {
        this.acceleration.set(a);
    }

    public PVector getLocalX(){
        PVector x = new PVector(1,0) ;
        return x.rotate(theta).normalize();
    }
    public PVector getLocalY(){
        PVector y = new PVector(0,1) ;
        return y.rotate(theta).normalize();
    }
    public void update(float step) {
        w += step*a;
        theta += w;

        velocity = velocity.add(acceleration.mult(step));
        position = position.add(velocity);

        updateChildren(step);
    }
    protected void updateChildren(float step){
        for(Model model: children){
            model.update(step);
        }
    }

    @Override
    public void addChild(Model child) {
        super.addChild(child);
        view.addChild(child.view);
        controller.addChild(child.controller);
    }

    @Override
    public void removeChild(Model child) {
        super.removeChild(child);
        view.removeChild(child.view);
        controller.removeChild(child.controller);
    }
}
