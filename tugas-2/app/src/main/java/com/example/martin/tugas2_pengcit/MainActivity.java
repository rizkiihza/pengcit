package com.example.martin.tugas2_pengcit;

/* Source :
https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
Diakses 2 September 2018
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 0;
    private ImageView imageView;
    private Bitmap rawBitmap;
    private ImageProcessor imageProcessor;
    private Spinner algoSpinner;
    private String[] algoChoice;
    private String algoSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imageView = this.findViewById(R.id.imageView1);
        imageProcessor = new ImageProcessor();

        final Context ctx = this;

        // setup spinner
        algoSelected = "ALU";
        algoSpinner = this.findViewById(R.id.algorithmSpinner);

        algoChoice = new String[] {"ALU", "Linear Stretching"};
        final ArrayAdapter<String> algorithmList = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, algoChoice);
        algoSpinner.setAdapter(algorithmList);
        algoSpinner.setSelection(0);
        algoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                algoSelected = algoChoice[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // setup photo button
        Button photoButton = this.findViewById(R.id.photoButton);
        photoButton.setOnClickListener(new View.OnClickListener() {

            private static final String TAG = "debug";

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On Click Listener");
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            }
        });

        // setup histogram button
        Button histButton = this.findViewById(R.id.histButton);
        histButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, HistogramActivity.class);
                intent.putExtra("Image", rawBitmap);
                startActivity(intent);
            }
        });

        // setup toggle
        ToggleButton toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imageView.setImageBitmap(transformBitmap(rawBitmap, algoSelected));
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
            rawBitmap = adjustOrientation((Bitmap) data.getExtras().get("data"));

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
            matrix.postRotate(90);
            return Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
        }
        return b;
    }

    private Bitmap transformBitmap(Bitmap bitmap, String algoName) {
        // do transformation

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Log.d("debug", "w : " + Integer.toString(w));
        Log.d("debug", "h : " + Integer.toString(h));

        int[][] r = new int[h][w];
        int[][] g = new int[h][w];
        int[][] b = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int colour = bitmap.getPixel(j, i);
                r[i][j] = Color.red(colour);
                g[i][j] = Color.green(colour);
                b[i][j] = Color.blue(colour);
            }
        }

        if (algoName.equals("ALU")) {
            r = imageProcessor.transformCumulative(r, h, w);
            g = imageProcessor.transformCumulative(g, h, w);
            b = imageProcessor.transformCumulative(b, h, w);
            Log.d("debug", "algo used: ALU");
        } else if( algoName.equals("Linear Stretching")) {
            r = imageProcessor.linearStretching(r, h, w);
            g = imageProcessor.linearStretching(g, h, w);
            b = imageProcessor.linearStretching(b, h, w);
            Log.d("debug", "algo used Linear Stretching");
        }

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap transformed_bitmap = Bitmap.createBitmap(w, h, config);

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int colour = Color.rgb(r[i][j], g[i][j], b[i][j]);
                transformed_bitmap.setPixel(j, i, colour);
            }
        }

        return transformed_bitmap;
    }
}
