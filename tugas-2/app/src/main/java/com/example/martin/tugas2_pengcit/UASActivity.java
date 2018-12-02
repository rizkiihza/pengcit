package com.example.martin.tugas2_pengcit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class UASActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 1887;
    private FourierTransformer fourierTransformer;
    private FaceDetector faceDetector;
    private Bitmap rawBitmap1, rawBitmap2;
    private Bitmap curBitmap1, curBitmap2;

    private int w1, h1, w2, h2;
    private int[][] a1,r1,g1,b1,gr1,bw1;
    private int[][] a2,r2,g2,b2,gr2,bw2;

    ImageView imageView1, imageView2;

    private TextView resultText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uas);

        fourierTransformer = new FourierTransformer();
        faceDetector = new FaceDetector();
        resultText = findViewById(R.id.deltaResultText);

        // read data from
        Intent intent = getIntent();
        rawBitmap1 = intent.getParcelableExtra("Image");
        curBitmap1 = rawBitmap1;
        w1 = rawBitmap1.getWidth();
        h1 = rawBitmap1.getHeight();

        a1 = new int[w1][h1];
        r1 = new int[w1][h1];
        g1 = new int[w1][h1];
        b1 = new int[w1][h1];
        gr1 = new int[w1][h1];

        for (int i = 0; i < w1; i++) {
            for (int j = 0; j < h1; j++) {
                int colour = rawBitmap1.getPixel(i, j);
                a1[i][j] = Color.alpha(colour);
                r1[i][j] = Color.red(colour);
                g1[i][j] = Color.green(colour);
                b1[i][j] = Color.blue(colour);
                gr1[i][j] = (r1[i][j] + g1[i][j] + b1[i][j]) / 3;
            }
        }

        // initiate android element variable
        imageView1 = findViewById(R.id.uasImageView);
        imageView1.setImageBitmap(rawBitmap1);
        imageView2 = findViewById(R.id.uasImageView2);

        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView1);
        photoViewAttacher.update();
        PhotoViewAttacher photoViewAttacher2 = new PhotoViewAttacher(imageView2);
        photoViewAttacher2.update();

        // setup gallery
        Button galleryButton = this.findViewById(R.id.getPicture2Button);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra("return-data", true);
                startActivityForResult(intent, GALLERY_REQUEST);
            }
        });

        // setting compare button
        Button goButton = findViewById(R.id.uasSearchButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compareImage();
                getComparation();
                imageView1.setImageBitmap(curBitmap1);
                imageView1.setImageBitmap(curBitmap2);
            }
        });
    }


    public void getComparation() {
        double delta = 0;

        resultText.setText(Double.toString(delta));
        resultText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60f);
        resultText.setTextColor(Color.BLACK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                InputStream stream = getContentResolver().openInputStream(uri);
                rawBitmap2 = BitmapFactory.decodeStream(stream);
                rawBitmap2 = adjustOrientation(rawBitmap2);
                rawBitmap2 = Bitmap.createScaledBitmap(rawBitmap2, 300, 400, false);

                curBitmap2 = rawBitmap2;

                w2 = rawBitmap2.getWidth();
                h2 = rawBitmap2.getHeight();

                a2 = new int[w2][h2];
                r2 = new int[w2][h2];
                g2 = new int[w2][h2];
                b2 = new int[w2][h2];
                gr2 = new int[w2][h2];

                for (int i = 0; i < w2; i++) {
                    for (int j = 0; j < h2; j++) {
                        int colour = rawBitmap2.getPixel(i, j);
                        a2[i][j] = Color.alpha(colour);
                        r2[i][j] = Color.red(colour);
                        g2[i][j] = Color.green(colour);
                        b2[i][j] = Color.blue(colour);
                        gr2[i][j] = (r2[i][j] + g2[i][j] + b2[i][j]) / 3;
                    }
                }

                imageView2.setImageBitmap(rawBitmap2);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Log.d("photosize", Integer.toString(rawBitmap1.getWidth()) + ' ' + Integer.toString(rawBitmap1.getHeight()));
        Log.d("photosize", Integer.toString(rawBitmap2.getWidth()) + ' ' + Integer.toString(rawBitmap2.getHeight()));
    }

    private Bitmap adjustOrientation(Bitmap b) {
        if (b == null) {
            return null;
        }
        int w = b.getWidth();
        int h = b.getHeight();
        if (w>h) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            return Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
        }
        return b;
    }

    public void draw1() {
        for (int i = 0; i < w1; i++) {
            for (int j = 0; j < h1; j++) {
                int colour = Color.rgb(r1[i][j], g1[i][j], b1[i][j]);
                //int colour = Color.rgb(gr[i][j], gr[i][j], gr[i][j]);
//                int colour = Color.rgb(bw[i][j], bw[i][j], bw[i][j]);
                curBitmap1.setPixel(i, j, colour);
            }
        }
    }

    public void draw2() {
        for (int i = 0; i < w2; i++) {
            for (int j = 0; j < h2; j++) {
                int colour = Color.rgb(r2[i][j], g2[i][j], b2[i][j]);
                //int colour = Color.rgb(gr[i][j], gr[i][j], gr[i][j]);
//                int colour = Color.rgb(bw[i][j], bw[i][j], bw[i][j]);
                curBitmap2.setPixel(i, j, colour);
            }
        }
    }

    public void compareImage() {
        // image 1

        gr1 = faceDetector.getSkin(a1, r1, g1, b1, w1, h1);
        gr1 = faceDetector.preprocess(gr1, w1, h1);

        ArrayList<int[]> boundFace1 = faceDetector.getFace(gr1, w1, h1);

        bw1 = faceDetector.convolute(r1, g1, b1, w1, h1, 90);
        bw1 = faceDetector.preprocess(bw1, w1, h1);

        // image 2

        gr2 = faceDetector.getSkin(a2, r2, g2, b2, w2, h2);
        gr2 = faceDetector.preprocess(gr2, w2, h2);

        ArrayList<int[]> boundFace2 = faceDetector.getFace(gr2, w2, h2);

        bw2 = faceDetector.convolute(r2, g2, b2, w2, h2, 90);
        bw2 = faceDetector.preprocess(bw2, w2, h2);

        draw1();
        draw2();
    }

}
