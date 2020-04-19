package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;


public class CustomChartMarkerView extends MarkerView {

    private TextView marker;

    public CustomChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        marker = (TextView) findViewById(R.id.marker);
    }

    // Update UI every time the marker is redrawn

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            marker.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {

            marker.setText("" + Utils.formatNumber(e.getY(), 0, true));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
