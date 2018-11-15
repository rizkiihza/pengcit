package com.example.martin.tugas2_pengcit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FaceRecognition extends AppCompatActivity {

    private Bitmap rawBitmap;
    private Bitmap curBitmap;
    private ImageView faceImageView;
    private Button searchButton;
    private Button backButton;
    private FaceDetector faceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        faceDetector = new FaceDetector();

        // read data from
        Intent intent = getIntent();
        rawBitmap = intent.getParcelableExtra("Image");
        curBitmap = rawBitmap;

        // initiate android element variable
        faceImageView = findViewById(R.id.faceImageView);
        faceImageView.setImageBitmap(rawBitmap);

        // setting search button
        searchButton = findViewById(R.id.faceSearchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFace();
                faceImageView.setImageBitmap(curBitmap);
            }
        });

        // setup back button
        backButton = findViewById(R.id.faceBackButton);
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

    void getFace() {
        int w = rawBitmap.getWidth();
        int h = rawBitmap.getHeight();

        int[][] a = new int[w][h];
        int[][] r = new int[w][h];
        int[][] g = new int[w][h];
        int[][] b = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int colour = rawBitmap.getPixel(i, j);
                a[i][j] = Color.alpha(colour);
                r[i][j] = Color.red(colour);
                g[i][j] = Color.green(colour);
                b[i][j] = Color.blue(colour);
            }
        }

        int[][] gr = faceDetector.getSkin(a, r, g, b, w, h);
        gr = faceDetector.preprocess(gr, w, h);
        int[] bound = faceDetector.getFace(gr, w, h);

        int minx = bound[0], maxx = bound[1], miny = bound[2], maxy = bound[3];

        int bColor = 255;

        for (int i = minx; i <= maxx; i++) {
            r[i][miny] = r[i][maxy] = bColor;
            g[i][miny] = g[i][maxy] = bColor;
            b[i][miny] = b[i][maxy] = bColor;
            gr[i][miny] = gr[i][maxy] = bColor;
        }
        for (int i = miny; i <= maxy; i++) {
            r[minx][i] = r[maxx][i] = bColor;
            g[minx][i] = g[maxx][i] = bColor;
            b[minx][i] = b[maxx][i] = bColor;
            gr[minx][i] = gr[maxx][i] = bColor;
        }

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int colour = Color.rgb(r[i][j], g[i][j], b[i][j]);
                curBitmap.setPixel(i, j, colour);
            }
        }
    }
}
