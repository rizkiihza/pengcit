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
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ChainActivity extends AppCompatActivity {
    private Bitmap rawBitmap;
    private int[][] r;
    private int[][] g;
    private int[][] b;
    private int[][] bw;
    private int[][] bw_transpose;
    private int[][] processed;
    private ImageView imageView;
    private ImageProcessor imageProcessor;
    private ThinningProcessor thinningProcessor;
    private TextView resultText;
    private SeekBar threshold;
    private ChainCodeDigit digits;
    private boolean thinned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chain);

        imageView = findViewById(R.id.numberView);
        imageProcessor = new ImageProcessor();
        digits = new ChainCodeDigit();
        resultText = findViewById(R.id.resultText);
        thinningProcessor = new ThinningProcessor();
        threshold = findViewById(R.id.threshold);
        threshold.setMax(255);

        // set zoomable image view
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.update();

        // get raw bitmap from intent
        Intent intent = getIntent();
        rawBitmap = intent.getParcelableExtra("Image");
        imageView.setImageBitmap(rawBitmap);

        // process bitmap
        final int w = rawBitmap.getWidth();
        final int h = rawBitmap.getHeight();

        r = new int[w][h];
        g = new int[w][h];
        b = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int colour = rawBitmap.getPixel(i, j);
                r[i][j] = Color.red(colour);
                g[i][j] = Color.green(colour);
                b[i][j] = Color.blue(colour);
            }
        }

        // convert to black and white
        bw = imageProcessor.convertToBW(r, g, b, w, h, 128);
        bw_transpose = new int[h][w];
        processed = bw;

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                bw_transpose[i][j] = bw[j][i];
            }
        }

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap transformed_bitmap = Bitmap.createBitmap(w, h, config);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (bw[i][j] > 0) transformed_bitmap.setPixel(i, j, Color.BLACK);
                else transformed_bitmap.setPixel(i, j, Color.WHITE);
            }
        }

        imageView.setImageBitmap(transformed_bitmap);
    }

    public void convertBW(View target) {
        int h = bw[0].length;
        int w = bw.length;

        bw = imageProcessor.convertToBW(r, g, b, w, h, threshold.getProgress());
        bw_transpose = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                bw_transpose[i][j] = bw[j][i];
            }
        }

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap transformed_bitmap = Bitmap.createBitmap(w, h, config);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (bw[i][j] > 0) transformed_bitmap.setPixel(i, j, Color.BLACK);
                else transformed_bitmap.setPixel(i, j, Color.WHITE);
            }
        }

        imageView.setImageBitmap(transformed_bitmap);
    }

    public void thinningView(View target) {
        int h = bw[0].length;
        int w = bw.length;

        thinned = true;
        processed = thinningProcessor.thinning(bw_transpose, w, h);
        processed = thinningProcessor.removeNoise(processed, w, h);

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap transformed_bitmap = Bitmap.createBitmap(w, h, config);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (processed[i][j] > 0) transformed_bitmap.setPixel(i, j, Color.BLACK);
                else transformed_bitmap.setPixel(i, j, Color.WHITE);
            }
        }

        imageView.setImageBitmap(transformed_bitmap);
    }

    public void getNumber(View target) {
        if (!thinned) {
            double[] freqRatio = imageProcessor.getChainFrequency(processed,
                    processed.length, processed[0].length);
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
        } else {
            int loop_count = thinningProcessor.countLoop(processed, processed.length, processed[0].length);
            int[] neighbor_count = thinningProcessor.countNeighbors(processed, processed.length, processed[0].length);
        }
    }
}
