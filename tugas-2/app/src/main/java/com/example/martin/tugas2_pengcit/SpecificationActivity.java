package com.example.martin.tugas2_pengcit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class SpecificationActivity extends AppCompatActivity {
    private static final int N_PARAMETER = 3;
    private Bitmap rawBitmap;
    private ImageView imageView;
    private ImageProcessor imageProcessor;
    private SeekBar[] seekbars;
    private int frequency[];
    private int targetHistogram[];
    private int transformFunction[];

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
        frequency = new int[N_PARAMETER];
        targetHistogram = new int[256];
        transformFunction = new int[256];

        // get raw bitmap from intent
        Intent intent = getIntent();
        rawBitmap = intent.getParcelableExtra("Image");
        imageView.setImageBitmap(rawBitmap);

        // setup seekbar

        // setup transform button
        Button transformButton = findViewById(R.id.specificationButton);
        transformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int w = rawBitmap.getWidth();
                int h = rawBitmap.getHeight();
                for (int i = 0; i < N_PARAMETER; i++) {
                    frequency[i] = Math.round(seekbars[i].getProgress() * w * h);
                }
                generateHistogram();
                specifyHistogram();
                imageView.setImageBitmap(transformHistogram(rawBitmap, transformFunction));
            }
        });
    }

    private void generateHistogram() {

    }

    private void specifyHistogram() {

    }

    private Bitmap transformHistogram(Bitmap b, int[] transformFunction) {
        return b;
    }
}
