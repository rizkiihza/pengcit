package com.example.martin.tugas2_pengcit;

import android.graphics.Color;

public class FaceDetector {
    float[][] getHSV(int[][] r, int[][] g, int[][] b, int w, int h, int index) {
        float[][] result = new float[w][h];
        float[] hsv = new float[3];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color.RGBToHSV(r[i][j], g[i][j], b[i][j], hsv);
                result[i][j] = hsv[index];
            }
        }

        return result;
    }

    int getSkin(int[][] r, int[][] b, int[][] b, int w, int h) {

    }
}
