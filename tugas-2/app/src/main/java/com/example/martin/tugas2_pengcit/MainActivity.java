package com.example.martin.tugas2_pengcit;

/* Source :
https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
Diakses 2 September 2018
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

/*private class ImageProcessor {
    public int[][] transform_cumulative(int[][] pixels) {
        int row = pixels.length;
        int col = pixels[0].length;

        int[] count_pixels = new int[256];
        int[][] new_pixels = new int[row][col];

        for (int i = 0; i < count_pixels.length; i += 1) {
            count_pixels[i] = 0;
        }

        for (int[] row : pixels) {
            for (int item : row) {
                count_pixels[item] += 1;
            }
        }

        for (int i = 1; i < count_pixels.length; i += 1) {
            count_pixels[i] += count_pixels[i-1];
        }

        for (int i = 0; i < row; i+= 1) {
            for (int j = 0; j < col; j += 1) {
                new_pixels[i] = (255*count_pixels[pixels[i][j]]) / (row*col);
            }
        }

        return new_pixels;
    }
}*/

public class MainActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 0;
    private ImageView imageView;
    private Bitmap rawBitmap;
    private Bitmap processedBitmap;
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Start everything");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imageView = this.findViewById(R.id.imageView1);

        // setup button
        Button photoButton = this.findViewById(R.id.photoButton);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On Click Listener");
                Log.d(TAG, checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ? "granted" : "not granted");
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Log.d(TAG, "Initiate intent");
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Log.d(TAG, "Start get package manager");

                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        Log.d(TAG, "Start activity");
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    } else {
                        Log.d(TAG, "Null activity");
                    }

                    Log.d(TAG, "Finish");
                }
                Log.d(TAG, "Really Finish");
            }
        });

        // setup toggle
        ToggleButton toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imageView.setImageBitmap(processedBitmap);
                } else {
                    imageView.setImageBitmap(rawBitmap);
                }
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
            rawBitmap = (Bitmap) data.getExtras().get("data");
            processedBitmap = transformBitmap(rawBitmap);

            imageView.setImageBitmap(rawBitmap);
        }
    }

    private Bitmap transformBitmap(Bitmap b) {
        // do transformation here
        return b;
    }
}
