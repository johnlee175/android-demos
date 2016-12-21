package com.johnsoft.app.mymvp.view.ui.chart;

import java.util.List;

import com.johnsoft.app.mymvp.view.bean.ChartBean;

import android.graphics.Canvas;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class LineChart extends AbstractChart {
    private ChartBean bean;
    public LineChart(ChartBean bean) {
        this.bean = bean;
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawHAxis(canvas, bean.catagories);
        drawLine(canvas, bean.values);
    }

    private void drawLine(Canvas canvas, List<Integer> values) {
    }

    private void drawHAxis(Canvas canvas, List<String> catagories) {
    }
}
