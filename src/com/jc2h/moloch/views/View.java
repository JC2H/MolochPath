package com.jc2h.moloch.views;

import com.jc2h.moloch.utils.Node;
import processing.core.*;

import java.util.LinkedList;

public abstract class View extends Node<View>{
    private PGraphics graphics;
    private boolean redraw;

    private PMatrix2D transform;
    private PVector position;
    private float angle;

    public final int width;
    public final int height;

    public View(PApplet base){
        super();
        this.graphics = base.createGraphics(base.width, base.height);
        transform = new PMatrix2D();
        redraw = true;
        position = new PVector(0,0,0);
        angle = 0;
        width = base.width;
        height = base.height;
    }
    public View(View parent){
        super(parent);
        this.graphics = parent.graphics.parent.createGraphics(parent.graphics.parent.width, parent.graphics.parent.height);
        transform = new PMatrix2D();
        redraw = true;
        position = new PVector(0,0,0);
        angle = 0;
        width = parent.graphics.parent.width;
        height = parent.graphics.parent.height;
    }


    public void draw() {
        if(redraw){
            graphics.beginDraw();
            graphics.clear();

            graphics.push();
            graphics.translate(position.x, position.y);
            graphics.rotate(angle);
            redraw(this.graphics);
            drawChildren();
            graphics.pop();

            graphics.endDraw();
            redraw = false;
        }

        if(parent == null){
            drawOnPApplet(graphics.parent);
        }else {
            drawOnGraphics(parent.graphics);
        }
    }

    protected PGraphics getGraphics(){
        return graphics;
    }
    protected void drawOnPApplet(PApplet pApplet){
        pApplet.imageMode(PConstants.CORNER);
        pApplet.image(graphics,0,0);
    }
    protected void drawOnGraphics(PGraphics pGraphics){
        PGraphics canvas = parent.graphics;
        canvas.imageMode(PConstants.CORNER);
        canvas.image(graphics, 0, 0);

    }

    public void triggerUpdate(){
        redraw = true;
        if(parent != null && !parent.redraw)parent.triggerUpdate();
    }
    private void drawChildren(){
        for(View view: children){
            graphics.push();
            view.draw();
            graphics.pop();
        }
    }
    protected void setPosition(PVector position){
        this.position = position.copy();
    }
    protected void bindPosition(PVector ref){this.position = ref;}
    protected void setAngle(float angle){
        this.angle = angle;
    }

    protected PVector getPosition(){
        return this.position;
    }
    protected float getAngle(){
        return angle;
    }
    abstract public void redraw(PGraphics graphics);

}
