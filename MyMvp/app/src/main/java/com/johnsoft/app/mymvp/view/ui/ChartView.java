package com.johnsoft.app.mymvp.view.ui;

import com.johnsoft.app.mymvp.view.protocol.IView;
import com.johnsoft.app.mymvp.view.ui.chart.AbstractChart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class ChartView extends View implements IView {
    private IViewCallback callback;
    private AbstractChart chart;

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartView setChart(AbstractChart chart) {
        this.chart = chart;
        invalidate();
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (chart != null) {
            chart.onDraw(canvas);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (callback != null) {
            callback.onAttach();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (callback != null) {
            callback.onDetach();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (callback != null) {
            if (hasWindowFocus) {
                callback.onEnterSession();
            } else {
                callback.onExitSession();
            }
        }
    }

    @Override
    public void registerCallback(IViewCallback callback) {
        this.callback = callback;
    }

    @Override
    public void unregisterCallback(IViewCallback callback) {
        this.callback = null;
    }
}
