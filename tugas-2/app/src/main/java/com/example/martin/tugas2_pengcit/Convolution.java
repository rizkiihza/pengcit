package com.example.martin.tugas2_pengcit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

public class Convolution extends AppCompatActivity {

    private Bitmap rawBitmap;
    private Bitmap curBitmap;
    private ImageView convoImageView;
    private ConvolutionProcessor convoProcessor;
    private String[] convoChoice;
    private String convoSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convolution);

        convoImageView = findViewById(R.id.convoImageView);
        convoProcessor = new ConvolutionProcessor();

        Intent intent = getIntent();
        rawBitmap = intent.getParcelableExtra("Image");
        curBitmap = rawBitmap;
        convoImageView.setImageBitmap(rawBitmap);

        // setup spinner
        convoSelected = "Original";
        Spinner convoSpinner = this.findViewById(R.id.convoSpinner);

        convoChoice = new String[] {"Original", "Smoothing", "Gradien", "Difference", "Sobel",
                "Prewitt", "Roberts", "Frei-Chen", "Custom"};
        final ArrayAdapter<String> algorithmList = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, convoChoice);
        convoSpinner.setAdapter(algorithmList);
        convoSpinner.setSelection(0);
        convoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                convoSelected = convoChoice[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // setup convolution button
        Button transformButton = findViewById(R.id.convoTransformButton);
        transformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curBitmap = transformBitmap(rawBitmap, convoSelected);
                convoImageView.setImageBitmap(curBitmap);
            }
        });

        // setup back button
        Button backButton = findViewById(R.id.convoBackButton);
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

    private Bitmap transformBitmap(Bitmap bitmap, String convoName) {
        // do transformation
        if (convoName.equals("Original")) {
            return bitmap;
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[][] r = new int[w][h];
        int[][] g = new int[w][h];
        int[][] b = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int colour = bitmap.getPixel(i, j);
                r[i][j] = Color.red(colour);
                g[i][j] = Color.green(colour);
                b[i][j] = Color.blue(colour);
            }
        }

        if (convoName.equals("Smoothing")) {
            r = convoProcessor.smoothing(r, w, h);
            g = convoProcessor.smoothing(g, w, h);
            b = convoProcessor.smoothing(b, w, h);
        } else if( convoName.equals("Gradien")) {
            r = convoProcessor.gradien(r, w, h);
            g = convoProcessor.gradien(g, w, h);
            b = convoProcessor.gradien(b, w, h);
        } else if ( convoName.equals("Difference")) {
            r = convoProcessor.difference(r, w, h);
            g = convoProcessor.difference(g, w, h);
            b = convoProcessor.difference(b, w, h);
        } else if ( convoName.equals("Sobel")) {
            r = convoProcessor.sobel(r, w, h);
            g = convoProcessor.sobel(g, w, h);
            b = convoProcessor.sobel(b, w, h);
        } else if ( convoName.equals("Prewitt")) {
            r = convoProcessor.prewitt(r, w, h);
            g = convoProcessor.prewitt(g, w, h);
            b = convoProcessor.prewitt(b, w, h);
        } else if( convoName.equals("Roberts")) {
            r = convoProcessor.roberts(r, w, h);
            g = convoProcessor.roberts(g, w, h);
            b = convoProcessor.roberts(b, w, h);
        } else if ( convoName.equals("Frei-Chen")) {
            r = convoProcessor.frei_chen(r, w, h);
            g = convoProcessor.frei_chen(g, w, h);
            b = convoProcessor.frei_chen(b, w, h);
        } else if (convoName.equals("Custom")) {
            String[][] kernelStr = new String[3][3];
            kernelStr[0][0] = ((EditText)findViewById(R.id.element00)).getText().toString();
            kernelStr[0][1] = ((EditText)findViewById(R.id.element01)).getText().toString();
            kernelStr[0][2] = ((EditText)findViewById(R.id.element02)).getText().toString();
            kernelStr[1][0] = ((EditText)findViewById(R.id.element10)).getText().toString();
            kernelStr[1][1] = ((EditText)findViewById(R.id.element11)).getText().toString();
            kernelStr[1][2] = ((EditText)findViewById(R.id.element12)).getText().toString();
            kernelStr[2][0] = ((EditText)findViewById(R.id.element20)).getText().toString();
            kernelStr[2][1] = ((EditText)findViewById(R.id.element21)).getText().toString();
            kernelStr[2][2] = ((EditText)findViewById(R.id.element22)).getText().toString();
            int[][] kernel = new int[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    kernel[i][j] = Integer.parseInt(kernelStr[i][j]);
                }
            }
            r = convoProcessor.custom(r, w, h, kernel);
            g = convoProcessor.custom(g, w, h, kernel);
            b = convoProcessor.custom(b, w, h, kernel);
        }

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap transformed_bitmap = Bitmap.createBitmap(w, h, config);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int colour = Color.rgb(r[i][j], g[i][j], b[i][j]);
                transformed_bitmap.setPixel(i, j, colour);
            }
        }

        return transformed_bitmap;
    }

}
