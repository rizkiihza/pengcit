package com.example.martin.tugas2_pengcit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class ChainActivity extends AppCompatActivity {
    private Bitmap rawBitmap;
    private ImageView imageView;
    private ImageProcessor imageProcessor;
    private ThinningProcessor thinningProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chain);

        imageView = findViewById(R.id.numberView);
        imageProcessor = new ImageProcessor();
        thinningProcessor = new ThinningProcessor();

        // get raw bitmap from intent
        Intent intent = getIntent();
        rawBitmap = intent.getParcelableExtra("Image");
        imageView.setImageBitmap(rawBitmap);

        // process bitmap
        final int w = rawBitmap.getWidth();
        final int h = rawBitmap.getHeight();

        int[][] r = new int[w][h];
        int[][] g = new int[w][h];
        int[][] b = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int colour = rawBitmap.getPixel(i, j);
                r[i][j] = Color.red(colour);
                g[i][j] = Color.green(colour);
                b[i][j] = Color.blue(colour);
            }
        }
        final int[][] bw = imageProcessor.convertToBW(r, g, b, w, h);

        final ChainCodeDigit digits = new ChainCodeDigit();

        // setup thinning
        Button thinningButton = findViewById(R.id.thinningButton);
        thinningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[][] bw_transpose = new int[h][w];

                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        bw_transpose[i][j] = bw[j][i];
                    }
                }

                int[][] thin_bitmap = thinningProcessor.thinning(bw_transpose, w, h);

                Bitmap.Config config = Bitmap.Config.ARGB_8888;
                Bitmap transformed_bitmap = Bitmap.createBitmap(w, h, config);


                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        int colour = Color.rgb(thin_bitmap[i][j], thin_bitmap[i][j], thin_bitmap[i][j]);
                        transformed_bitmap.setPixel(j, i, colour);
                    }
                }

                imageView.setImageBitmap(transformed_bitmap);
            }
        });

        // setup get number button and result textview
        final TextView resultText = findViewById(R.id.resultText);
        Button getNumberButton = findViewById(R.id.getNumberButton);
        getNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double[] freqRatio = imageProcessor.getChainFrequency(bw, w, h);
                Log.d("CHAIN", Arrays.toString(freqRatio));
                double[] distance = new double[10];
                int minDistanceIdx = -1;
                for (int i = 0; i < 10; i++) {
                    distance[i] = imageProcessor.errorSum(freqRatio, digits.ratio[i]);
                    if (minDistanceIdx < 0 || distance[i] < distance[minDistanceIdx]) {
                        minDistanceIdx = i;
                    }
                }
                resultText.setText(Integer.toString(minDistanceIdx));
                resultText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 90f);
                resultText.setTextColor(Color.BLACK);
            }
        });
    }
}
