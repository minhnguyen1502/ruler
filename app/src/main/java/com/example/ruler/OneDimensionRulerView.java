package com.example.ruler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class OneDimensionRulerView extends View {

    private static final int UpperSection = 1;
    private static final int LowerSection = 2;
    private RulerUnit unit = RulerUnit.MM;
    private Paint colorPaintMask;
    private float upperY = 0f;
    private float lowerY = 1f;
    private final float minDistance = dpTOpx(0f);
    private int currentSection = 0;
    private float pointerY = 5f;
    private float coefficient = 1f;

    public OneDimensionRulerView(Context context) {
        this(context, null);
    }

    public OneDimensionRulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OneDimensionRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("ResourceAsColor")
    private void init() {
        colorPaintMask = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorPaintMask.setColor(Color.parseColor("#4FC606"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0f, 0f, getWidth(), upperY, colorPaintMask);
        canvas.drawRect(0f, lowerY, getWidth(), getHeight(), colorPaintMask);

        Paint middleSectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        middleSectionPaint.setColor(Color.parseColor("#001003"));
        canvas.drawRect(0f, upperY, getWidth(), lowerY, middleSectionPaint);
    }

    public void save(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("upperY", upperY);
        editor.putFloat("lowerY", lowerY);
        editor.putString("unit", unit.name()); // Save the current unit
        editor.apply();
    }

    public void restore(SharedPreferences sharedPreferences) {
        upperY = sharedPreferences.getFloat("upperY", 0f);
        lowerY = sharedPreferences.getFloat("lowerY", getHeight());
        String unitString = sharedPreferences.getString("unit", RulerUnit.MM.name());
        unit = RulerUnit.valueOf(unitString); // Restore the unit
        notifyDistanceChangeListener();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float centerPoint = (lowerY + upperY) / 2;
                currentSection = (event.getY() < centerPoint) ? UpperSection : LowerSection;
                pointerY = event.getY();
                return currentSection != 0;
            case MotionEvent.ACTION_MOVE:
                float dy = event.getY() - pointerY;
                switch (currentSection) {
                    case UpperSection:
                        upperY += dy;
                        upperY = Math.max(0f, Math.min(lowerY - minDistance, upperY));
                        break;
                    case LowerSection:
                        lowerY += dy;
                        lowerY = Math.max(upperY + minDistance, Math.min(getHeight(), lowerY));
                        break;
                }
                pointerY = event.getY();
                notifyDistanceChangeListener();
                invalidate();
                save(getContext().getSharedPreferences("RulerPositions", Context.MODE_PRIVATE));
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return false;
        }
        return false;
    }

    private void notifyDistanceChangeListener() {
        if (onRulerChangeListener != null) {
            float distance = getDistance();
            String distanceString = unit.getUnitString(distance);
            onRulerChangeListener.onRulerChange(distance, distanceString);
        }
    }

    public float getDistance() {
        float distanceInInches = RulerUnit.pxToIn(Math.abs(upperY - lowerY), coefficient, getResources().getDisplayMetrics());
        switch (unit) {
            case CM:
                return distanceInInches * 2.54f;
            case MM:
                return distanceInInches * 25.4f;
            case IN:
            default:
                return distanceInInches;
        }
    }

    public interface OnRulerChangeListener {
        void onRulerChange(float distance, String distanceString);
    }

    public void setUnitAndUpdate(RulerUnit unit) {
        this.unit = unit;
        notifyDistanceChangeListener();
        invalidate();
    }

    private OnRulerChangeListener onRulerChangeListener;

    public void setOnRulerChangeListener(OnRulerChangeListener listener) {
        this.onRulerChangeListener = listener;
        notifyDistanceChangeListener();
    }
    private float dpTOpx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

}
