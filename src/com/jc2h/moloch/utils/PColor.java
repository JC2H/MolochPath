package com.jc2h.moloch.utils;

import processing.core.PConstants;

import static processing.core.PConstants.HSB;
import static processing.core.PConstants.RGB;

public class PColor {
    public final int a,b,c;
    public final int mode;
    private PColor(int a, int b, int c, int mode){
       this.a = a;
       this.b = b;
       this.c = c;
       this.mode = RGB;
    }
    static public PColor createHSVColor(int h, int s, int v){
        return new PColor(h,s,v, HSB);
    }
    static public PColor createRGBColor(int r, int g, int b){
        return new PColor(r,g,b, RGB);
    }
}
