package com.example.ruler;

import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Locale;

/**
 * Created by Anas Altair on 8/29/2018.
 */
public enum RulerUnit {
    MM(25.4f, "MM"),
    CM(2.54f, "CM"),
    IN(1f, "IN");

    private final float converter;
    private final String unit;

    RulerUnit(float converter, String unit) {
        this.converter = converter;
        this.unit = unit;
    }
    public static float mmToPx(float mm, float coefficient, DisplayMetrics displayMetrics) {
        return mm * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, coefficient, displayMetrics);
    }

    public static float pxToIn(float px, float coefficient, DisplayMetrics displayMetrics) {
        return px / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, coefficient, displayMetrics);
    }

    public String getUnitString(float distance) {
        switch (this) {
            case CM:
                return String.format("%d CM", (int) distance);
            case MM:
                return String.format("%.2f MM", distance).replace(',', '.');
            case IN:
                return String.format("%.2f IN", distance).replace(',', '.');
            default:
                return String.valueOf(distance);
        }
    }

    public float convert(float value) {
        return value * converter;
    }
}
