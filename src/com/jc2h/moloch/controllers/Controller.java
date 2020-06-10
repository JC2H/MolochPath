package com.jc2h.moloch.controllers;

import com.jc2h.moloch.models.Model;
import com.jc2h.moloch.utils.Node;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Controller extends Node<Controller> {
    private Model model;

    public Controller(){

    }
    public Controller(Controller controller){
        super(controller);
    }
    public void attachModel(Model model) {
        this.model = model;
    }

    public void detachModel() {
        this.model = null;
    }

    public void handleMouseEvent(MouseEvent event){
        for(Controller controller: children){
            controller.handleMouseEvent(event);
        }
    }
    public void handleKeyboardEvent(KeyEvent event){
        for(Controller controller: children){
            controller.handleKeyboardEvent(event);
        }
    }
}
