package com.jc2h.moloch.models;

import com.jc2h.moloch.Configuration;
import com.jc2h.moloch.controllers.Controller;
import com.jc2h.moloch.utils.PColor;
import com.jc2h.moloch.views.View;
import processing.core.PApplet;
import static processing.core.PApplet.println;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
public class Pointer  extends Model{

    public Pointer(Model model){
        super(model);
        this.controller = new PointerController(model.controller);
        this.view = new PointerView(model.view);
    }
    class PointerView extends View{
        private PGraphics cursor;
        public PointerView(View parent) {
            super(parent);
            PGraphics graphics = getGraphics();
            cursor = graphics.parent.createGraphics(graphics.parent.width, graphics.parent.height);
            cursor.beginDraw();
            initCursor(cursor);
            cursor.endDraw();
            this.bindPosition(Pointer.this.getPosition());
        }
        private void initCursor(PGraphics graphics){
            int width = 20;
            PColor stroke = Configuration.base;
            PColor fill = Configuration.secondary2;
            graphics.clear();
            graphics.colorMode(stroke.mode);
            graphics.stroke(stroke.a, stroke.b, stroke.c);
            graphics.strokeWeight(2);
            graphics.colorMode(fill.mode);
            graphics.fill(fill.a, fill.b, fill.c);
            graphics.noStroke();
            graphics.smooth();
            graphics.beginShape();
            graphics.vertex(0,0);
            graphics.vertex(width,width/2.0f);
            graphics.vertex(width/2.0f,width/2.0f);
            graphics.vertex(width/2.0f, width);
            graphics.endShape(PConstants.CLOSE);
        }

        @Override
        public void redraw(PGraphics graphics) {
            graphics.imageMode(PConstants.CORNER);
            graphics.image(cursor, 0,0);
        }

    }

    class PointerController extends Controller{
        PointerController(Controller controller){
            super(controller);
        }

        @Override
        public void handleMouseEvent(MouseEvent event) {
            View view = Pointer.this.view;
            super.handleMouseEvent(event);
            PVector pos = new PVector(event.getX(), event.getY());
            Pointer.this.setPosition(pos.copy());
            view.triggerUpdate();
        }

        @Override
        public void handleKeyboardEvent(KeyEvent event) {
            super.handleKeyboardEvent(event);
        }
    }
}
