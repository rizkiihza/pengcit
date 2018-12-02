package com.example.martin.tugas2_pengcit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import uk.co.senab.photoview.PhotoViewAttacher;

public class UASActivity extends AppCompatActivity {
    private static final int MY_CAMERA_PERMISSION_CODE = 0;
    private static final int CAMERA_REQUEST = 1888;
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
                startActivityForResult(intent, 200);
            }
        });

        // setting compare button
        Button goButton = findViewById(R.id.fftGoButton);
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
        // compare image
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
