package com.example.ruler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class OneDimensionRulerView extends View {

    private static final int UpperSection = 1;
    private static final int LowerSection = 2;
    private PreferencesManager preferencesManager;
    private RulerUnit unit;
    private float distance;
    private Paint colorPaintMask;
    private float upperY = 0f;
    private float lowerY = 1f;
    private final float minDistance = dpTOpx(10f);
    private int currentSection = 0;
    private float pointerY = 0f;
    private float coefficient = 1f;

    public OneDimensionRulerView(Context context) {
        this(context, null);
    }

    public OneDimensionRulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        preferencesManager = new PreferencesManager(context);
        restoreState(); // Khôi phục trạng thái từ PreferencesManager
    }

    public OneDimensionRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("ResourceAsColor")
    private void init() {
        colorPaintMask = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorPaintMask.setColor(Color.parseColor("#4FC606")); // Set color to green
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        upperY = h * .3f;
        lowerY = h * .7f;
        notifyDistanceChangeListener();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0f, 0f, getWidth(), upperY, colorPaintMask);
        canvas.drawRect(0f, lowerY, getWidth(), getHeight(), colorPaintMask);

        Paint middleSectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        middleSectionPaint.setColor(Color.parseColor("#001003")); // Set color to middle section
        canvas.drawRect(0f, upperY, getWidth(), lowerY, middleSectionPaint);
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
        return RulerUnit.pxToIn(Math.abs(upperY - lowerY), coefficient, getResources().getDisplayMetrics());
    }


    // Custom listener interface for distance changes
    public interface OnRulerChangeListener {
        void onRulerChange(float distance, String distanceString);
    }
    public void setUnitAndUpdate(RulerUnit unit) {
        this.unit = unit;
        // Notify the listener with the updated distance value in the new unit
        notifyDistanceChangeListener();
        // Force redraw to update the view
        invalidate();
    }


    private OnRulerChangeListener onRulerChangeListener;

    public void setOnRulerChangeListener(OnRulerChangeListener listener) {
        this.onRulerChangeListener = listener;
        // Notify the listener with the initial distance value
        notifyDistanceChangeListener();
    }

    private float dpTOpx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    // Lưu trạng thái
    private void saveState() {
        preferencesManager.saveUnit(unit);
        preferencesManager.saveDistance(distance);
    }

    private void restoreState() {
        unit = preferencesManager.getSavedUnit();
        distance = preferencesManager.getSavedDistance();
        notifyDistanceChangeListener(); // Cập nhật giao diện người dùng
    }

    // Phương thức setUnitAndUpdate() đã có

    // Ghi đè phương thức onDetachedFromWindow để lưu trạng thái khi View bị hủy
    @Override
    protected void onDetachedFromWindow() {
        saveState();
        super.onDetachedFromWindow();
    }
}
