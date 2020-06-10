package com.jc2h.moloch.utils;

import com.jc2h.moloch.views.View;

import java.util.LinkedList;
import java.util.List;

public abstract class Node< T extends Node<T>>{
    protected List<T> children;
    protected T parent;

    public Node(){
        this.parent = null;
        this.children = new LinkedList<>();
    }
    public Node(T parent){
        this.parent = parent;
        this.children = new LinkedList<>();
    }

    public void addChild(T child){
        this.children.add(child);
        child.parent = (T)this;
    }

    public void removeChild(T child){
        children.remove(child);
        child.setParent(null);
    }

    public T getParent(){
        return this.parent;
    }

    public void setParent(T parent){
        this.parent = parent;
    }
    public boolean isLeaf(){
        return children.isEmpty();
    }

    public boolean isRoot(){
        return parent == null;
    }
}
