package com.jc2h.moloch;

import com.jc2h.moloch.utils.PColor;

public abstract class Configuration {
    public static final PColor base = PColor.createRGBColor(0,0,0);
    public static final PColor main = PColor.createRGBColor(34,0,7);
    public static final PColor secondary = PColor.createRGBColor(64,3,5);
    public static final PColor secondary1= PColor.createRGBColor(73,8,3);
    public static final PColor secondary2= PColor.createRGBColor(86,14,0);
    public static final PColor secondary3= PColor.createRGBColor(116,7,7);

    public static final boolean renderDebug = true;


    public static final float threshold = 0.01f;
}
