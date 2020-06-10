package com.jc2h.moloch.models;

import com.jc2h.moloch.Configuration;
import com.jc2h.moloch.controllers.Actor;
import com.jc2h.moloch.controllers.Controller;
import com.jc2h.moloch.utils.PColor;
import com.jc2h.moloch.views.View;
import com.jc2h.poly2tri.Poly2Tri;
import com.jc2h.poly2tri.geometry.polygon.Polygon;
import com.jc2h.poly2tri.geometry.polygon.PolygonPoint;
import com.jc2h.poly2tri.geometry.primitives.Point;
import com.jc2h.poly2tri.triangulation.TriangulationPoint;
import com.jc2h.poly2tri.triangulation.delaunay.DelaunayTriangle;
import com.jc2h.poly2tri.triangulation.point.TPoint;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static processing.core.PApplet.println;
import static processing.core.PConstants.*;

public class Triangulation extends Model{
    private LinkedList<PVector> pointList;
    private Polygon board;
    private DelaunayTriangle start;
    PVector startPoint;
    private DelaunayTriangle goal;
    PVector goalPoint;
    private List<DelaunayTriangle> path;
    private Actor robot;
    public Triangulation(Model parent){
        super(parent);
        this.view = new ViewBowyerWatson(parent.view);
        this.controller = new ControllerBowyerWatson();
        pointList = new LinkedList<>();
        board = new Polygon(Arrays.asList(
                new PolygonPoint(0,0,0),
                new PolygonPoint(0,view.height,0),
                new PolygonPoint(view.width,view.height,0),
                new PolygonPoint(view.width,0,0)
        ));
        Poly2Tri.triangulate(board);
        robot = new Actor(this);
        addChild(robot);
        robot.followCursor = false;
    }

    public void addHole(){
        LinkedList<PolygonPoint> pps = new LinkedList<>();
       for(PVector point :pointList) {
            pps.add(new PolygonPoint(point.x,point.y));
       }
       Polygon hole = new Polygon(pps);
       board.addHole(hole);
       Poly2Tri.triangulate(board);
       Poly2Tri.triangulate(hole);
       this.robot.setMesh(board.getHoles());
       updateStartGoal();
    }

    public void setGoal(PVector goal){
        this.goalPoint = goal;
        updateStartGoal();
    }
    public void setStart(PVector start){
        this.startPoint = start;
        this.robot.setGoals(Collections.singletonList(start));
        this.robot.setPosition(start);
        updateStartGoal();
    }
    private void findCellPath(){
        println("Find path start");
        if(start != null && goal != null) {
            println("Find started");
            start.parentPath = null;
            LinkedList<DelaunayTriangle> visited = new LinkedList<>();
            LinkedList<DelaunayTriangle> open = new LinkedList<>();

            DelaunayTriangle current = null;
            open.add(start);

            int maxCount = 10000;
            while (current != goal && !open.isEmpty() && maxCount > 0){
                current = open.pop();
                for(DelaunayTriangle triangle: current.neighbors){
                    if(triangle != null && triangle.isInterior()) {
                        boolean alreadyCheck = false;
                        for (DelaunayTriangle v : visited) {
                            if (triangle == v) {
                                alreadyCheck = true;
                                break;
                            }
                        }
                        if (!alreadyCheck) {
                            PVector centroid = new PVector(triangle.centroid().getXf(),triangle.centroid().getYf());
                            if(centroid.dist(new PVector(triangle.points[0].getXf(),triangle.points[0].getYf())) <= robot.getRadius()){
                               visited.add(triangle);
                            }else {
                                triangle.parentPath = current;
                                open.add(triangle);
                            }
                        }
                    }
                }
                --maxCount;
                visited.add(current);
            }
            println("Find path finished");
            if(current == goal){
                println("Find path success");
                current = goal.parentPath;
                assert current != null;
                TPoint point;
                LinkedList<PVector> positions = new LinkedList<>();
                path = new LinkedList<>();
                while (current != start){
                    path.add(current);
                    point = current.centroid();
                    positions.addFirst(new PVector(point.getXf(), point.getYf()));

                    current = current.parentPath;
                }
                positions.addFirst(startPoint);
                positions.addLast(goalPoint);
                robot.setGoals(positions);
                robot.startRun();
            }else {
                println("Find path fail");
            }
            if(maxCount == 0){
                println("Max count reached, path not found");
            }
            println("Path: " + path.size());
            view.triggerUpdate();
        }
    }
    private void updateStartGoal(){
        goal = null;
        start = null;
        TriangulationPoint s = null,g = null;
        if(startPoint != null)
            s = new PolygonPoint(startPoint.x, startPoint.y);
        if(goalPoint != null)
            g = new PolygonPoint(goalPoint.x, goalPoint.y);
        for(DelaunayTriangle triangle: board.getTriangles()){
            if(s != null) {
                if (triangle.isPointInside(s)) start = triangle;
            }
            if(g != null) {
                if (triangle.isPointInside(g)) goal = triangle;
            }
            if((g == null || goal != null) && (s == null && start != null)) {
                break;
            }
        }
    }
    class ViewBowyerWatson extends View{
        public boolean showTriangulation = false;
        public ViewBowyerWatson(View parent) {
            super(parent);
        }

        private void drawPointElementOnMesh(PGraphics graphics, PVector point, DelaunayTriangle triangle, PColor colorStroke, PColor colorFill){
            graphics.strokeWeight(5);
            graphics.colorMode(colorStroke.mode);
            graphics.stroke(colorStroke.a, colorStroke.b, colorStroke.c);
            graphics.noFill();
            graphics.beginShape(TRIANGLES);
            for (TriangulationPoint pt : triangle.points) {
                graphics.vertex(pt.getXf(),pt.getYf());
            }
            graphics.endShape(CLOSE);
            graphics.colorMode(colorFill.mode);
            graphics.fill(colorFill.a, colorFill.b, colorFill.c);
            graphics.ellipse(point.x, point.y, 10,10);
        }
        @Override
        public void redraw(PGraphics graphics) {
            List<DelaunayTriangle> triangles = board.getTriangles();
            PColor fill = Configuration.secondary3;
            graphics.fill(fill.a, fill.b, fill.c);
            graphics.noStroke();
            graphics.smooth();
            if (board != null && board.getHoles() != null) {
                for (Polygon p : board.getHoles()) {
                    graphics.beginShape();
                    for (TriangulationPoint point : p.getPoints()) {
                        graphics.vertex(point.getXf(), point.getYf());
                    }
                    graphics.endShape(CLOSE);
                }
            }
            if(path != null){
                for(DelaunayTriangle triangle: path){
                    if(triangle != start && triangle != goal){
                        graphics.strokeWeight(2);
                        graphics.stroke(255,255,255);
                        graphics.noFill();
                        for(DelaunayTriangle p:path) {
                            graphics.beginShape(TRIANGLES);
                            for (TriangulationPoint point : p.points){
                                graphics.vertex(point.getXf(), point.getYf());
                            }
                            graphics.endShape(CLOSE);
                        }
                    }
                }
            }
            if (goal != null){
                drawPointElementOnMesh(graphics,goalPoint, goal, PColor.createRGBColor(0,255,0), PColor.createRGBColor(0,255,0));
            }
            if(start != null) {
                drawPointElementOnMesh(graphics,startPoint, start, PColor.createRGBColor(100,100,255), PColor.createRGBColor(100,100,255));
            }
            if(Configuration.renderDebug){
                for(PVector point: pointList){
                    graphics.noStroke();
                    graphics.fill(255);
                    graphics.ellipse(point.x,point.y,10,10);
                }
            }
        }
    }

    class ControllerBowyerWatson extends Controller{
        @Override
        public void handleMouseEvent(MouseEvent event) {
            super.handleMouseEvent(event);
            if(event.getAction() == MouseEvent.PRESS && event.getButton() == LEFT){
                if(event.isControlDown()){
                    if(event.isShiftDown() && event.isAltDown()){
                        //CTR - SHIFT - ALT

                    }else if(event.isShiftDown()) {
                        //CTR - SHIFT
                        setStart(new PVector(event.getX(), event.getY()));
                        view.triggerUpdate();

                    }else if(event.isAltDown()){
                        //CTR - ALT
                        setGoal(new PVector(event.getX(), event.getY()));
                        view.triggerUpdate();

                    }else{
                        //CTR
                        PVector point = new PVector(event.getX(),event.getY());
                        pointList.add(point);
                        view.triggerUpdate();

                    }
                }else if(event.isShiftDown()){
                   if(event.isAltDown()) {
                       //SHIFT - ALT


                   }else{
                       //SHIFT
                       PVector mouse = new PVector(event.getX(), event.getY());
                       for(PVector point: pointList){
                           if(mouse.dist(point) < 10){
                               pointList.remove(point);
                               println("Point removed ",point);
                               view.triggerUpdate();
                               break;
                           }
                       }
                   }
                }else if(event.isAltDown()){
                    //ALT
                }else{
                    //NONE

                }
            }
        }

        @Override
        public void handleKeyboardEvent(KeyEvent event) {
            super.handleKeyboardEvent(event);
            if (event.getAction() == KeyEvent.PRESS) {
                switch (event.getKeyCode()) {
                    case ENTER:
                        if (pointList.size() >= 3) {
                            addHole();
                            pointList.clear();
                            view.triggerUpdate();
                        } else {
                            println("Error can't create polygon with less than 3 vertex", pointList);
                        }
                        break;
                    case BACKSPACE:
                        if(!pointList.isEmpty()){
                            pointList.removeLast();
                            view.triggerUpdate();
                        }
                        break;
                }
                switch (event.getKey()) {
                    case 'f':
                    case 'F':
                            println("Find path triggered");
                            findCellPath();
                        break;
                    case 't':
                    case 'T':

                        break;
                }
            }
            if(event.getKeyCode() == ENTER && event.getAction() == KeyEvent.PRESS){
            }
        }
    }
}
