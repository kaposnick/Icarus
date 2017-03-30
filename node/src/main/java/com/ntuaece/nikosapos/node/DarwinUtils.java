package com.ntuaece.nikosapos.node;

public class DarwinUtils {
    public static double normalizeValue(double v) {
        if (v >= 1) return 1.0f;
        else if (v <= 0) return 0.0f;
        else return v;
    }
}
