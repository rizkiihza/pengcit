package com.example.martin.tugas2_pengcit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import ImageProcessor.ImageProcessor;

public class HistogramActivity extends AppCompatActivity {
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        Intent intent = getIntent();
        bitmap = intent.getParcelableExtra("Image");

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[][] red = new int[w][h];
        int[][] green = new int[w][h];
        int[][] blue = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int pixel = bitmap.getPixel(i, j);
                red[i][j] = Color.red(pixel);
                blue[i][j] = Color.blue(pixel);
                green[i][j] = Color.green(pixel);
            }
        }

        ImageProcessor imageProcessor = new ImageProcessor();
        int[] redCount = imageProcessor.countPixels(red);
        int[] greenCount = imageProcessor.countPixels(green);
        int[] blueCount = imageProcessor.countPixels(blue);
        int[] grayscaleCount = new int[redCount.length];

        for (int i = 0; i < redCount.length; i++) {
            grayscaleCount[i] = (redCount[i] + greenCount[i] + blueCount[i]) / 3;
        }

        int[] data;
        DataPoint[] dp;

        data = redCount;
        dp = new DataPoint[data.length];
        for (int i = 0; i < data.length; i++) {
            dp[i] = new DataPoint(i + 1, data[i]);
        }

        GraphView redGraph = findViewById(R.id.red_graph);
        BarGraphSeries<DataPoint> redSeries = new BarGraphSeries<>(dp);
        redSeries.setColor(Color.RED);
        redGraph.addSeries(redSeries);
        redGraph.getViewport().setMinX(0);
        redGraph.getViewport().setMaxX(data.length);
        redGraph.getViewport().setXAxisBoundsManual(true);

        data = greenCount;
        dp = new DataPoint[data.length];
        for (int i = 0; i < data.length; i++) {
            dp[i] = new DataPoint(i + 1, data[i]);
        }

        GraphView greenGraph = findViewById(R.id.green_graph);
        BarGraphSeries<DataPoint> greenSeries = new BarGraphSeries<>(dp);
        greenSeries.setColor(Color.GREEN);
        greenGraph.addSeries(greenSeries);
        greenGraph.getViewport().setMinX(0);
        greenGraph.getViewport().setMaxX(data.length);
        greenGraph.getViewport().setXAxisBoundsManual(true);

        data = blueCount;
        dp = new DataPoint[data.length];
        for (int i = 0; i < data.length; i++) {
            dp[i] = new DataPoint(i + 1, data[i]);
        }

        GraphView blueGraph = findViewById(R.id.blue_graph);
        BarGraphSeries<DataPoint> blueSeries = new BarGraphSeries<>(dp);
        blueSeries.setColor(Color.BLUE);
        blueGraph.addSeries(blueSeries);
        blueGraph.getViewport().setMinX(0);
        blueGraph.getViewport().setMaxX(data.length);
        blueGraph.getViewport().setXAxisBoundsManual(true);

        data = grayscaleCount;
        dp = new DataPoint[data.length];
        for (int i = 0; i < data.length; i++) {
            dp[i] = new DataPoint(i + 1, data[i]);
        }

        GraphView grayGraph = findViewById(R.id.grayscale_graph);
        BarGraphSeries<DataPoint> graySeries = new BarGraphSeries<>(dp);
        graySeries.setColor(Color.GRAY);
        grayGraph.addSeries(graySeries);
        grayGraph.getViewport().setMinX(0);
        grayGraph.getViewport().setMaxX(data.length);
        grayGraph.getViewport().setXAxisBoundsManual(true);
    }
}
