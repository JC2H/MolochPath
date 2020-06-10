package com.jc2h.moloch.utils;

import com.jc2h.poly2tri.geometry.polygon.Polygon;
import com.jc2h.poly2tri.triangulation.TriangulationPoint;
import com.jc2h.poly2tri.triangulation.delaunay.DelaunayTriangle;
import processing.core.PMatrix2D;
import processing.core.PVector;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.min;
import static processing.core.PConstants.PI;

public abstract class MathUtils {
    public static PMatrix2D getIdentityMatrix2D(){
        PMatrix2D matrix = new PMatrix2D();
        matrix.m00 = 1;
        matrix.m11  = 1;
        return matrix;
    }

    public static PVector mult(PMatrix2D matrix, PVector vector){
        PVector result = new PVector();
        matrix.mult(vector,result);
        return  result;
    }

    public static PMatrix2D mult(PMatrix2D matrixA, PMatrix2D matrixB){
        PMatrix2D result = new PMatrix2D(matrixA);
        result.apply(matrixB);
        return result;
    }

    public static float distToPoint(Polygon polygon, PVector point){
        float min = Float.MAX_VALUE;
        for(DelaunayTriangle triangle: polygon.getTriangles()){
            min = Float.min(distToPoint(triangle, point),min);
        }
        return min;
    }
    public static float distToPoint(DelaunayTriangle triangle, PVector point){
        PVector ta,tb,tc;
        TriangulationPoint tpa= triangle.points[0];
        TriangulationPoint tpb= triangle.points[1];
        TriangulationPoint tpc= triangle.points[2];

        ta = new PVector(tpa.getXf(), tpa.getYf());
        tb = new PVector(tpb.getXf(), tpb.getYf());
        tc = new PVector(tpc.getXf(), tpc.getYf());
        return min(distToPoint(ta,tb, point), distToPoint(tb,tc, point),distToPoint(tc,ta,point));

    }

    public static float distToPoint(PVector lineA, PVector lineB, PVector point){
        PVector dir = PVector.sub(lineB, lineA);
        float mag = dir.mag();
        dir = dir.normalize();
        PVector norm = dir.copy().rotate(PI/2);

        float proyP = PVector.dot(point, dir);
        if(proyP < 0)return PVector.dist(lineA, point);
        if(proyP > mag)return  PVector.dist(lineB, point);
        return abs(PVector.dot(point, norm));
    }

    public static boolean rayCast(DelaunayTriangle triangle, PVector start, PVector end){
        PVector ta,tb,tc;
        TriangulationPoint tpa= triangle.points[0];
        TriangulationPoint tpb= triangle.points[1];
        TriangulationPoint tpc= triangle.points[2];

        ta = new PVector(tpa.getXf(), tpa.getYf());
        tb = new PVector(tpb.getXf(), tpb.getYf());
        tc = new PVector(tpc.getXf(), tpc.getYf());

        //Segment AB
        if(doIntersect(ta,tb, start,end))return true;
        //Segment BC
        if(doIntersect(tb,tc, start,end))return true;
        //Segment CA
        if(doIntersect(tc,ta, start,end))return true;

        return false;
    }

    static boolean onSegment(PVector p, PVector q, PVector r)
    {
        return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y);
    }

    static int orientation(PVector p, PVector q, PVector r)
    {
        float val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);

        if (val == 0) return 0;

        return (val > 0)? 1: 2;
    }

    static boolean doIntersect(PVector p1, PVector q1, PVector p2, PVector q2)
    {
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4)
            return true;

        if (o1 == 0 && onSegment(p1, p2, q1)) return true;

        if (o2 == 0 && onSegment(p1, q2, q1)) return true;

        if (o3 == 0 && onSegment(p2, p1, q2)) return true;

        return o4 == 0 && onSegment(p2, q1, q2);// Doesn't fall in any of the above cases
    }
}
