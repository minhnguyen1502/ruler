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

    public interface OnRulerChangeListener {
        void onDistanceChanged(float distance);
    }

    private OnRulerChangeListener mListener;

    private static final int UpperSection = 1;
    private static final int LowerSection = 2;

    private Paint colorPaintMask;
    private Paint backgroundPaint; // Paint cho màu nền

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

    private RulerUnit unit = RulerUnit.IN;
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
        colorPaintMask.setColor(Color.TRANSPARENT);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(dpTOpx(25f));
        textPaint.setColor(getContext().getResources().getColor(R.color.black));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaintReplace = new TextPaint(textPaint);
        textPaintReplace.setColor(Color.WHITE);
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

        canvas.drawText(unit.getUnitString(RulerUnit.pxToIn(Math.abs(upperY - lowerY), coefficient, getResources().getDisplayMetrics())),
                getWidth() * .5f, textPaint.getTextSize() + 5, textPaint);

        canvas.drawRect(0f, 0f, getWidth(), upperY, colorPaintMask);
        canvas.drawRect(0f, lowerY, getWidth(), getHeight(), colorPaintMask);

        canvas.drawText(unit.getUnitString(RulerUnit.pxToIn(Math.abs(upperY - lowerY), coefficient, getResources().getDisplayMetrics())),
                getWidth() * .5f, textPaint.getTextSize() + 5, textPaintReplace);
        if (mListener != null) {
            float distance = getDistance();
            mListener.onDistanceChanged(distance);
        }
    }

    public void setOnRulerChangeListener(OnRulerChangeListener listener) {
        mListener = listener;
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
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
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

    /**
     * Convert dp to pixels.
     * @param dp Value in dp to convert.
     * @return Dimension in pixels.
     */
    private float dpTOpx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}
