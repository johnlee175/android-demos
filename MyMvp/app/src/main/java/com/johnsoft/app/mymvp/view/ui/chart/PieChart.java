package com.johnsoft.app.mymvp.view.ui.chart;

import java.util.List;

import com.johnsoft.app.mymvp.view.bean.ChartBean;

import android.graphics.Canvas;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class PieChart extends AbstractChart {
    private ChartBean bean;
    public PieChart(ChartBean bean) {
        this.bean = bean;
    }

    @Override
    public void onDraw(Canvas canvas) {
        int sum = sumValues(bean.values);
        for (int i = 0; i < bean.catagories.size(); ++i) {
            drawPie(bean.catagories.get(i), bean.values.get(i) / (float) sum);
        }
    }

    private void drawPie(String catagory, float weight) {
    }

    private int sumValues(List<Integer> values) {
        return 0;
    }
}
