package com.example.narasimha.ondevice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Queue;

public class
Statistics extends AppCompatActivity {

    GraphView timeDomain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        timeDomain = findViewById(R.id.graph);
        ArrayList<Double> time = DomainsRepresentation.getTimes();
        double[] time_float = new double[time.size()];
        for(int i = 0; i<time.size(); i++)
            time_float[i] = time.get(i);
        int count_size = time.size();
        plot(count_size, time_float);
    }

    //Prepare plots and draw
    public void plot(int count_size, double time[]){
        DataPoint[] points = new DataPoint[count_size];
        for(int i = 0; i<count_size; i++)
            points[i] = new DataPoint(count_size, time[i]);
        BarGraphSeries<DataPoint> graph = new BarGraphSeries<>(points);
        timeDomain.addSeries(graph);
        timeDomain.setTitle("Time vs Number of predictions");
        timeDomain.setScaleX(1.0f);
    }
}
