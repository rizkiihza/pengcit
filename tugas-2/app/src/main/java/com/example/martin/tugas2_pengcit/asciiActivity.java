package com.example.martin.tugas2_pengcit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import uk.co.senab.photoview.PhotoViewAttacher;

public class asciiActivity extends AppCompatActivity {

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
    private AsciiCode ascii;
    private boolean thinned = false;
    private int thinningState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ascii);

        imageView = findViewById(R.id.asciiView);
        imageProcessor = new ImageProcessor();
        digits = new ChainCodeDigit();
        ascii = new AsciiCode();
        resultText = findViewById(R.id.asciiResultText);
        thinningProcessor = new ThinningProcessor();
        threshold = findViewById(R.id.asciiThreshold);
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
        if (thinningState == 0) {
            processed = thinningProcessor.thinning(bw_transpose, w, h);
            thinningState = (thinningState + 1) % 2;
        } else {
            processed = thinningProcessor.removeNoise(processed, w, h);
            thinningState = (thinningState + 1) % 2;
        }
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

    public void getAscii(View target) {
        ArrayList<Double> features = new ArrayList<>();
        int w = processed.length;
        int h = processed[0].length;
        int loop = thinningProcessor.countLoop(processed, w, h);
        int component = thinningProcessor.getDifferentPart(processed, w, h);
        int[] neighbors = thinningProcessor.countNeighbors(processed, w, h);
        ArrayList<Integer> chainCode = imageProcessor.getChainCode(processed, w, h);
        double[] chainFrequencyDouble = imageProcessor.getChainFrequencyDouble(chainCode);

        features.add((double)loop);
        for(int i = 1; i < 5; i++) {
            if (i != 2) features.add((double) neighbors[i]);
        }

        for (int i = 0; i < chainFrequencyDouble.length; i++) {
            features.add(chainFrequencyDouble[i]);
        }

        double[][] code;
        char[] label;
        if (component == 1) {
            code = ascii.code;
            label = ascii.label;
        } else if (component == 2) {
            code = ascii.codeTwo;
            label = ascii.labelTwo;
        } else {
            resultText.setText(Character.toString('%'));
            resultText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60f);
            resultText.setTextColor(Color.BLACK);
            return;
        }

        Log.d("Features", Arrays.toString(features.toArray()));
        double err, minError = -1;
        int minIndex = -1;

        for (int i = 0; i < code.length; i++) {
            err = 0;
            for (int j = 0; j < features.size(); j++) {
                if (j == 1 || j == 2) {
                    err += 0.25*(features.get(j) - code[i][j])*(features.get(j) - code[i][j]);
                } else {
                    err += (features.get(j) - code[i][j])*(features.get(j) - code[i][j]);
                }
            }
            if (minIndex < 0 || err < minError) {
                minError = err;
                minIndex = i;
            }
        }

        resultText.setText(Character.toString(label[minIndex]));
        resultText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60f);
        resultText.setTextColor(Color.BLACK);
    }
}
