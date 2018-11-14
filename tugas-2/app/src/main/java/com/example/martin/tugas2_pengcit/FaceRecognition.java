package com.example.martin.tugas2_pengcit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        // read data from
        Intent intent = getIntent();
        rawBitmap = intent.getParcelableExtra("Image");
        curBitmap = rawBitmap;

        // initiate android element variable
        faceImageView = findViewById(R.id.faceImageView);
        searchButton = findViewById(R.id.faceSearchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFace();
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

    }
}
