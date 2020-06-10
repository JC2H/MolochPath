package com.jc2h.moloch.controllers;

import com.jc2h.moloch.Configuration;
import com.jc2h.moloch.models.Model;
import com.jc2h.moloch.utils.MathUtils;
import com.jc2h.moloch.utils.PColor;
import com.jc2h.moloch.views.View;
import com.jc2h.poly2tri.Poly2Tri;
import com.jc2h.poly2tri.geometry.polygon.Polygon;
import com.jc2h.poly2tri.geometry.polygon.PolygonPoint;
import com.jc2h.poly2tri.triangulation.TriangulationPoint;
import com.jc2h.poly2tri.triangulation.delaunay.DelaunayTriangle;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static processing.core.PApplet.*;
import static processing.core.PConstants.PI;

public class Actor extends Model {
    public boolean followCursor = true;
    private float radius;
    private PVector currentGoal;
    private LinkedList<PVector> goals;
    private float threshold;

    private float maxOmega;
    private float maxVelocity;
    private ArrayList<Polygon> mesh;

    public Actor(Model model){
        super(model);
        this.view = new ViewActor(model.view);
        this.controller = new ControllerActor(model.controller);
        currentGoal = new PVector(0,0,0);
        goals = new LinkedList<>();
        goals.add(currentGoal);
        this.radius = 30;
        this.threshold = 10;

        maxOmega = 10f* PI/180.0f;
        maxVelocity = 2;
    }

    public void setGoals(List<PVector> goals){
        this.goals.clear();
        this.goals.addAll(goals);
        if(!goals.isEmpty()){
            PVector start = goals.get(0);
            setPosition(start);
            currentGoal = start;
        }
    }

    public void setMesh(ArrayList<Polygon> mesh){
       this.mesh = mesh;
    }

    public void startRun(){
        this.setPosition(goals.getFirst());
        this.currentGoal = goals.get(1);
    }
    @Override
    public void update(float step) {
        PVector errorPosition = PVector.sub(currentGoal,this.getPosition());
        PVector i = getLocalX();
        PVector j = getLocalY();

        if(errorPosition.mag() > threshold) {
            PVector direction = errorPosition.normalize();
            float angleError = PVector.angleBetween(direction, j);
            PVector control = new PVector();
            PVector.cross(j,direction,control);

            PVector v = PVector.mult(j, maxVelocity);
            float w = angleError;
            if(abs(angleError) > maxOmega)w = maxOmega*angleError/abs(angleError);
            if(control.z < 0)w*= -1;
            this.setVelocity(v);
            this.setW(w);
        }else{
            int ci = goals.indexOf(currentGoal);
            if(ci != goals.size() - 1){
               currentGoal = goals.get(ci + 1);
            }else {
                this.setVelocity(new PVector(0, 0, 0));
                this.setW(0);
            }
        }
        this.view.triggerUpdate();
        super.update(step);
        if(goals != null && mesh != null && currentGoal != null) {
            PVector origin = getPosition();
            PVector goal = currentGoal;
            boolean collision;
            for(int index = 0; index < goals.size(); ++index) {
                collision = false;
                PVector end = goals.get(index);
                for (Polygon obstacle : mesh) {
                    for(TriangulationPoint point: obstacle.getPoints()){
                        PVector p = new PVector(point.getXf(), point.getYf());
                        if(radius < MathUtils.distToPoint(origin,end,p)){
                            println(MathUtils.distToPoint(origin,end,p));
                            collision = true;
                            break;
                        }
                    }
                    if(collision)break;
                    for (DelaunayTriangle triangle : obstacle.getTriangles()) {
                        if(MathUtils.rayCast(triangle, origin, end)){
                            collision = true;
                            break;
                        }
                    }
                    if(collision)break;
                }
                if(!collision){
                   this.currentGoal = end;
                }
            }
        }
    }

    public float getRadius() {
        return radius;
    }

    class ViewActor extends View {
        ViewActor(PApplet pApplet){
            super(pApplet);
        }
        ViewActor(View view){
            super(view);
            this.bindPosition(Actor.this.getPosition());
        }
        @Override
        public void redraw(PGraphics graphics) {
            PVector i = PVector.mult(getLocalX(),20);
            PVector j = PVector.mult(Actor.this.getLocalY(),20);

            PColor fill = Configuration.secondary1;
            graphics.colorMode(fill.mode);
            graphics.fill(fill.a, fill.b, fill.c);
            graphics.smooth();
            graphics.noStroke();
            graphics.ellipse(0,0, Actor.this.radius, Actor.this.radius);
            graphics.fill(255);
            graphics.ellipse(currentGoal.x - this.getPosition().x, currentGoal.y - this.getPosition().y, 5,5);
            if(Configuration.renderDebug) {
                graphics.strokeWeight(5);
                graphics.stroke(0, 0, 255);
                graphics.line(0, 0, i.x, i.y);
                graphics.stroke(0, 255, 0);
                graphics.line(0, 0, j.x, j.y);
            }
        }
    }
    class ControllerActor extends Controller {

        ControllerActor(Controller controller){
            super(controller);
        }

        @Override
        public void handleMouseEvent(MouseEvent event) {
            super.handleMouseEvent(event);
            if(followCursor) {
                PVector mouse = new PVector(event.getX(), event.getY(), 0);
                Actor.this.currentGoal.set(mouse);
                Actor.this.view.triggerUpdate();
            }
        }

        @Override
        public void handleKeyboardEvent(KeyEvent event) {
            super.handleKeyboardEvent(event);
        }
    }
}
