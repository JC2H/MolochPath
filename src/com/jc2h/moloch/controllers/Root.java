package com.jc2h.moloch.controllers;

import com.jc2h.moloch.Configuration;
import com.jc2h.moloch.models.Model;
import com.jc2h.moloch.utils.PColor;
import com.jc2h.moloch.views.View;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class Root extends Model{

    public Root(PApplet pApplet){
        super();
        view = new RootView(pApplet);
        controller =  new Controller();
    }

    static class RootView extends View{
        RootView(PApplet applet){
            super(applet);
        }

        @Override
        public void redraw(PGraphics graphics) {
            PColor base = Configuration.base;
            graphics.colorMode(base.mode);
            graphics.background(base.a, base.b, base.c);
        }
    }
}
