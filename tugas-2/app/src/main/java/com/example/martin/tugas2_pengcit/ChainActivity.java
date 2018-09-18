package com.example.martin.tugas2_pengcit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ChainActivity extends AppCompatActivity {
    private Bitmap rawBitmap;
    private ImageView imageView;
    private ImageProcessor imageProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chain);

        imageView = findViewById(R.id.numberView);
        imageProcessor = new ImageProcessor();

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

        // setup button
        Button button = findViewById(R.id.getNumberButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < w; i++) {
                    for (int j = 0; j < h; j++) {
                        if (bw[i][j] == 0) {
                            rawBitmap.setPixel(i, j, Color.rgb(0, 0, 0));
                        } else {
                            rawBitmap.setPixel(i, j, Color.rgb(255, 255, 255));
                        }
                    }
                }
                imageView.setImageBitmap(rawBitmap);
                int[] chain = imageProcessor.getChainCode(bw, w, h);
                for (int i = 0; i < 10; i++) {
                    Log.d("CHAIN CODE", Integer.toString(i) + ' ' + Integer.toString(chain[i]));
                }
            }
        });
    }
}
