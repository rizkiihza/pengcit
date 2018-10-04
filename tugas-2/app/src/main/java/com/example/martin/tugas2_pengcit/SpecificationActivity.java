package com.example.martin.tugas2_pengcit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class SpecificationActivity extends AppCompatActivity {
    private static final int N_PARAMETER = 3;
    private Bitmap rawBitmap;
    private Bitmap curBitmap;
    private ImageView imageView;
    private ImageProcessor imageProcessor;
    private SeekBar[] seekbars;
    private double frequency[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specification);

        imageView = findViewById(R.id.specificationImageView);
        imageProcessor = new ImageProcessor();
        seekbars = new SeekBar[N_PARAMETER];
        seekbars[0] = findViewById(R.id.frequency0);
        seekbars[1] = findViewById(R.id.frequency1);
        seekbars[2] = findViewById(R.id.frequency2);
        frequency = new double[N_PARAMETER];

        // get raw bitmap from intent
        Intent intent = getIntent();
        rawBitmap = intent.getParcelableExtra("Image");
        curBitmap = rawBitmap;
        imageView.setImageBitmap(rawBitmap);

        // setup seekbars
        for (int i = 0; i < N_PARAMETER; i++) {
            seekbars[i].setMax(10);
        }

        // setup transform button
        final Button transformButton = findViewById(R.id.specificationButton);
        transformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int w = rawBitmap.getWidth();
                int h = rawBitmap.getHeight();
                for (int i = 0; i < N_PARAMETER; i++) {
                    frequency[i] = getSeekBarValue(seekbars[i].getProgress());
                    Log.d("FREQUENCY", Double.toString(frequency[i]));
                }
                curBitmap = transformHistogram(rawBitmap, h, w, frequency);
                imageView.setImageBitmap(curBitmap);
                Log.d("FREQUENCY", "DONE");
            }
        });

        // setup back button
        Button backButton = findViewById(R.id.backSpecificationButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resIntent = new Intent();
                resIntent.putExtra("Image", curBitmap);
                setResult(Activity.RESULT_OK, resIntent);
                finish();
            }
        });
    }

    private double getSeekBarValue(int value) {
        return 0.5 + (value * 0.05);
    }

    private Bitmap transformHistogram(Bitmap bitmap, int h, int w, double[] frequency) {
        // do transformation

        int[][] r = new int[h][w];
        int[][] g = new int[h][w];
        int[][] b = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int colour = bitmap.getPixel(j, i);
                r[i][j] = Color.red(colour);
                g[i][j] = Color.green(colour);
                b[i][j] = Color.blue(colour);
            }
        }

        int[][] new_r = imageProcessor.histogramSpecificationSort(r, h, w, frequency);
        int[][] new_g = imageProcessor.histogramSpecificationSort(g, h, w, frequency);
        int[][] new_b = imageProcessor.histogramSpecificationSort(b, h, w, frequency);

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap transformed_bitmap = Bitmap.createBitmap(w, h, config);

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int colour = Color.rgb(new_r[i][j], new_g[i][j], new_b[i][j]);
                transformed_bitmap.setPixel(j, i, colour);
            }
        }

        return transformed_bitmap;
    }
}
