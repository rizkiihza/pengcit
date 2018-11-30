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

import uk.co.senab.photoview.PhotoViewAttacher;

public class FFTActivity extends AppCompatActivity {
    FourierTransformer fourierTransformer;
    Bitmap rawBitmap, curBitmap;
    ComplexNumber[][] cr, cg, cb, cgr;
    int[][] r,g,b,gr;
    ImageView fftImageView;
    Button goButton, backButton;
    boolean transformed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fft);

        fourierTransformer = new FourierTransformer();
        transformed = false;

        // read data from
        Intent intent = getIntent();
        rawBitmap = intent.getParcelableExtra("Image");
        curBitmap = rawBitmap;

        int w = rawBitmap.getWidth();
        int h = rawBitmap.getHeight();

        r = new int[w][h];
        g = new int[w][h];
        b = new int[w][h];
        gr = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int colour = rawBitmap.getPixel(i, j);
                r[i][j] = Color.red(colour);
                g[i][j] = Color.green(colour);
                b[i][j] = Color.blue(colour);
                gr[i][j] = (r[i][j] + g[i][j] + b[i][j]) / 3;
            }
        }

        // initiate android element variable
        fftImageView = findViewById(R.id.fftImageView);
        fftImageView.setImageBitmap(rawBitmap);

        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(fftImageView);
        photoViewAttacher.update();

        // setting search button
        goButton = findViewById(R.id.fftGoButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fourierTransform();
                fftImageView.setImageBitmap(curBitmap);
            }
        });

        // setup back button
        backButton = findViewById(R.id.fftBackButton);
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

    public void fourierTransform() {
        Log.d("fft", "start");
        int w = rawBitmap.getWidth();
        int h = rawBitmap.getHeight();
        if (!transformed) {
            cr = fourierTransformer.fftImageForward(r, w, h);
            int[][] tmp_r = fourierTransformer.convert(cr, w, h);
            cg = fourierTransformer.fftImageForward(g, w, h);
            int[][] tmp_g = fourierTransformer.convert(cg, w, h);
            cb = fourierTransformer.fftImageForward(b, w, h);
            int[][] tmp_b = fourierTransformer.convert(cb, w, h);
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int colour = Color.rgb(tmp_r[i][j], tmp_g[i][j], tmp_b[i][j]);
                    curBitmap.setPixel(i, j, colour);
                }
            }
            transformed = true;
        } else {
            r = fourierTransformer.fftImageBackward(cr, w, h);
            g = fourierTransformer.fftImageBackward(cg, w, h);
            b = fourierTransformer.fftImageBackward(cb, w, h);
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int colour = Color.rgb(r[i][j], g[i][j], b[i][j]);
                    curBitmap.setPixel(i, j, colour);
                }
            }

            transformed = false;
        }

        Log.d("fft", "done");
    }
}
