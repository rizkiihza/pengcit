package com.example.martin.tugas2_pengcit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class HistogramActivity extends AppCompatActivity {
    private ImageView imageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        imageView = findViewById(R.id.imageView1);
        Intent intent = getIntent();
        bitmap = intent.getParcelableExtra("Image");
        imageView.setImageBitmap(bitmap);
    }
}
