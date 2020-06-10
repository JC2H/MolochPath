package com.jc2h.moloch;

import com.jc2h.moloch.controllers.Actor;
import com.jc2h.moloch.controllers.Root;
import com.jc2h.moloch.models.Triangulation;
import com.jc2h.moloch.models.Model;
import com.jc2h.moloch.models.Pointer;
import processing.core.*;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Window extends PApplet {
    Model root;
    @Override
    public void settings() {
        super.settings();
        size(800,800);
    }

    @Override
    public void setup() {
        super.setup();
        noCursor();
        root = new Root(this);
        root.setParent(null);
        //Actor actor = new Actor(root);
        Triangulation bw = new Triangulation(root);
        //root.addChild(actor);
        root.addChild(bw);
        Pointer pointer = new Pointer(root);
        root.addChild(pointer);
    }

    @Override
    public void draw() {
        root.update(frameRateLastNanos);
        root.view.draw();
    }

    @Override
    protected void handleMouseEvent(MouseEvent event) {
        super.handleMouseEvent(event);
        root.controller.handleMouseEvent(event);
    }

    @Override
    protected void handleKeyEvent(KeyEvent event) {
        super.handleKeyEvent(event);
        root.controller.handleKeyboardEvent(event);
    }
}
