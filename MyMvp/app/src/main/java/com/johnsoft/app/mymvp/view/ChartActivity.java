package com.johnsoft.app.mymvp.view;

import javax.inject.Inject;

import com.johnsoft.app.mymvp.R;
import com.johnsoft.app.mymvp.presenter.IChartPresenter;
import com.johnsoft.app.mymvp.view.bean.ChartBean;
import com.johnsoft.app.mymvp.view.protocol.IChartUi;
import com.johnsoft.app.mymvp.view.ui.ChartView;
import com.johnsoft.app.mymvp.view.ui.chart.BarChart;
import com.johnsoft.app.mymvp.view.ui.chart.LineChart;
import com.johnsoft.app.mymvp.view.ui.chart.PieChart;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class ChartActivity extends BaseActivity implements IChartUi {
    private IChartPresenter chartPresenter;
    private ChartBean chartBean;

    private ChartView chartView;
    private RadioGroup radioGroup;

    @Inject
    public void setPresenter(IChartPresenter chartPresenter) {
        this.chartPresenter = chartPresenter;
    }

    @Override
    public IChartPresenter getPresenter() {
        return chartPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        presenterComponent.inject(this);
        chartView = (ChartView) findViewById(R.id.chart);
        radioGroup = (RadioGroup) findViewById(R.id.options);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                changeChartState(checkedId);
            }
        });
        chartPresenter.calcChartBean(getIntent().getStringExtra("term"), this);
    }

    private void changeChartState(@IdRes int checkedId) {
        if (chartBean != null) {
            if (R.id.pie == checkedId) {
                chartView.setChart(new PieChart(chartBean));
            } else if (R.id.bar == checkedId) {
                chartView.setChart(new BarChart(chartBean));
            } else if (R.id.line == checkedId) {
                chartView.setChart(new LineChart(chartBean));
            }
        } else {
            Toast.makeText(ChartActivity.this, "chart data still loading", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChartBean(ChartBean chartBean) {
        this.chartBean = chartBean;
        changeChartState(radioGroup.getCheckedRadioButtonId());
    }
}
