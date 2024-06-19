package com.example.ruler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class OneDimensionRulerView extends View {

    private static final int UpperSection = 1;
    private static final int LowerSection = 2;

    private Paint colorPaintMask;
    private TextPaint textPaint;
    private TextPaint textPaintReplace;
    private float upperY = 0f;
    private float lowerY = 1f;
    private final float minDistance = dpTOpx(10f);
    private float markCmWidth = dpTOpx(20f);
    private float markHalfCmWidth = dpTOpx(15f);
    private float markMmWidth = dpTOpx(10f);
    private int currentSection = 0;
    private float pointerY = 0f;
    private RulerUnit unit = RulerUnit.MM;
    private float coefficient = 1f;

    public OneDimensionRulerView(Context context) {
        this(context, null);
    }

    public OneDimensionRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OneDimensionRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("ResourceAsColor")
    private void init() {
        colorPaintMask = new Paint(Paint.ANTI_ALIAS_FLAG);
//        colorPaintMask.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        colorPaintMask.setColor(Color.parseColor("#4FC606")); // Set color to green
        Paint middleSectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        middleSectionPaint.setColor(Color.parseColor("#001003")); // Set color to middle section

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(dpTOpx(25f));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaintReplace = new TextPaint(textPaint);
        textPaintReplace.setColor(Color.BLACK);
        textPaintReplace.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        upperY = h * .3f;
        lowerY = h * .7f;
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

        canvas.drawText(unit.getUnitString(RulerUnit.pxToIn(Math.abs(upperY - lowerY), coefficient, getResources().getDisplayMetrics())),
                getWidth() * .5f, textPaint.getTextSize() + 5, textPaint);

        canvas.drawText(unit.getUnitString(RulerUnit.pxToIn(Math.abs(upperY - lowerY), coefficient, getResources().getDisplayMetrics())),
                getWidth() * .5f, textPaint.getTextSize() + 5, textPaintReplace);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // When the user first touches the screen
                float centerPoint = (lowerY + upperY) / 2;
                // Determine if the touch is in the upper or lower section
                currentSection = (event.getY() < centerPoint) ? UpperSection : LowerSection;
                    // Store the initial touch position
                pointerY = event.getY();
                // Return true to indicate the event was handled
                return currentSection != 0;

            case MotionEvent.ACTION_MOVE:
                // When the user moves their finger on the screen
                float dy = event.getY() - pointerY; // Calculate the change in Y position
                switch (currentSection) {
                    case UpperSection:
                        // Adjust the position of the upper section
                        upperY += dy;
                        // Ensure upperY does not overlap the lower section and stays within bounds
                        upperY = Math.max(0f, Math.min(lowerY - minDistance, upperY));
                        break;
                    case LowerSection:
                        // Adjust the position of the lower section
                        lowerY += dy;
                        // Ensure lowerY does not overlap the upper section and stays within bounds
                        lowerY = Math.max(upperY + minDistance, Math.min(getHeight(), lowerY));
                        break;
                }
                // Update the pointer position
                pointerY = event.getY();
                // Redraw the view
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // When the user lifts their finger or the touch event is canceled
                return false;
        }
        return false;
    }

    public float getDistance() {
        return unit.convert(Math.abs(upperY - lowerY));
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", superState);
        bundle.putFloat("coefficient", coefficient);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            coefficient = bundle.getFloat("coefficient");
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
    private float dpTOpx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}
