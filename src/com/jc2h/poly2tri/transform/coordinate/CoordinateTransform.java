package com.jc2h.poly2tri.transform.coordinate;

import java.util.List;

import com.jc2h.poly2tri.geometry.primitives.Point;

public interface CoordinateTransform
{
    void transform(Point p, Point store);
    void transform(Point p);
    void transform(List<? extends Point> list);
}
