package com.example.martin.tugas2_pengcit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;

public class MainActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 0;
    private static final int SPECIFICATION_REQUEST = 1;
    private static final int TRANSFORMATION_REQUEST = 2;
    private Bitmap rawBitmap;
    private ImageView imageView;
    private String buttonSelected;
    private String[] buttonChoice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = this.findViewById(R.id.imageView1);

        final Context ctx = this;

        // setup gallery
        Button galleryButton = this.findViewById(R.id.galleryButton);
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

        // setup photo button
        Button photoButton = this.findViewById(R.id.photoButton);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            }
        });

        buttonSelected = "Histogram";
        Spinner choiceSpinner = findViewById(R.id.choiceSpinner);

        buttonChoice = new String[] {"Histogram", "Transform", "Specification", "Number", "ASCII", "Convolution"};
        final ArrayAdapter<String> choiceList = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, buttonChoice);
        choiceSpinner.setAdapter(choiceList);
        choiceSpinner.setSelection(0);
        choiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                buttonSelected = buttonChoice[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // setup histogram button
        Button choiceButton = this.findViewById(R.id.choiceButton);
        choiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                int request = 0;
                if (buttonSelected.equals("Histogram")) {
                    intent = new Intent(ctx, HistogramActivity.class);
                } else if (buttonSelected.equals("Transform")) {
                    intent = new Intent(ctx, TransformationAcitivty.class);
                    request = TRANSFORMATION_REQUEST;
                } else if (buttonSelected.equals("Specification")) {
                    intent = new Intent(ctx, SpecificationActivity.class);
                    request = SPECIFICATION_REQUEST;
                } else if (buttonSelected.equals("Number")){
                    intent = new Intent(ctx, ChainActivity.class);
                } else if (buttonSelected.equals("ASCII")){
                    intent = new Intent(ctx, asciiActivity.class);
                } else if (buttonSelected.equals("Convolution")) {
                    intent = new Intent(ctx, Convolution.class);
                } else {
                    intent = new Intent();
                }

                intent.putExtra("Image", rawBitmap);
                startActivityForResult(intent, request);
            }
        });
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            rawBitmap = adjustOrientation((Bitmap) data.getExtras().get("data"));
            rawBitmap = Bitmap.createScaledBitmap(rawBitmap, 300, 400, false);

            imageView.setImageBitmap(rawBitmap);
        }

        else if (requestCode == 200 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                InputStream stream = getContentResolver().openInputStream(uri);
                rawBitmap = BitmapFactory.decodeStream(stream);
                rawBitmap = adjustOrientation(rawBitmap);
                rawBitmap = Bitmap.createScaledBitmap(rawBitmap, 300, 400, false);
                imageView.setImageBitmap(rawBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == SPECIFICATION_REQUEST && resultCode == RESULT_OK) {
            rawBitmap = data.getParcelableExtra("Image");
            imageView.setImageBitmap(rawBitmap);
        }

        else if (requestCode == TRANSFORMATION_REQUEST && resultCode == RESULT_OK) {
            rawBitmap = data.getParcelableExtra("Image");
            imageView.setImageBitmap(rawBitmap);
        }
    }

    private Bitmap adjustOrientation(Bitmap b) {
        if (b == null) {
            return null;
        }
        int w = b.getWidth();
        int h = b.getHeight();
        if (w>h) {
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            return Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
        }
        return b;
    }
}
