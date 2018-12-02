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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

public class UASActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 1887;
    private FourierTransformer fourierTransformer;
    private FaceDetector faceDetector;
    private Bitmap rawBitmap1, rawBitmap2;
    private int[][] r,g,b,gr,bw;
    ImageView imageView1, imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uas);

        fourierTransformer = new FourierTransformer();
        faceDetector = new FaceDetector();

        // read data from
        Intent intent = getIntent();
        rawBitmap1 = intent.getParcelableExtra("Image");

        int w = rawBitmap1.getWidth();
        int h = rawBitmap1.getHeight();

        r = new int[w][h];
        g = new int[w][h];
        b = new int[w][h];
        gr = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int colour = rawBitmap1.getPixel(i, j);
                r[i][j] = Color.red(colour);
                g[i][j] = Color.green(colour);
                b[i][j] = Color.blue(colour);
                gr[i][j] = (r[i][j] + g[i][j] + b[i][j]) / 3;
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
                imageView1.setImageBitmap(rawBitmap1);
                imageView1.setImageBitmap(rawBitmap2);
            }
        });
    }

    public void compareImage() {
        return;
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

}
