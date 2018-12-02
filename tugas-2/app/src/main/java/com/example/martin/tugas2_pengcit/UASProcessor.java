package com.example.martin.tugas2_pengcit;

import android.util.Log;

import java.util.ArrayList;

public class UASProcessor {
    FaceDetector faceDetector;

    void createRectangle(int[][] r, int[][] g, int[][] b, int[][] gr, int[][] bw, int minx, int maxx, int miny, int maxy, int rr, int gg, int bb) {
        for (int i = minx; i <= maxx; i++) {
            r[i][miny] = r[i][maxy] = rr;
            g[i][miny] = g[i][maxy] = gg;
            b[i][miny] = b[i][maxy] = bb;
            gr[i][miny] = gr[i][maxy] = 128;
            bw[i][miny] = bw[i][maxy] = 128;
        }
        int midx = (minx + maxx) / 2;
        for (int i = miny; i <= maxy; i++) {
            r[minx][i] = r[maxx][i] = rr;
            g[minx][i] = g[maxx][i] = gg;
            b[minx][i] = b[maxx][i] = bb;
            gr[minx][i] = gr[maxx][i] = 128;
            bw[minx][i] = bw[maxx][i] = 128;
        }
    }

    void createPoint(int[][] r, int[][] g, int[][] b, int[][] gr, int[][] bw, int x, int y, int w, int h, int rr, int gg, int bb) {
        for (int i = Math.max(0, x - 1); i <= Math.min(w - 1, x + 1); i++) {
            for (int j = Math.max(0, y - 1); j <= Math.min(h - 1, y + 1); j++) {
                r[i][j]  = rr;
                g[i][j] = gg;
                b[i][j] = bb;
                gr[i][j] = 128;
                bw[i][j] = 128;
            }
        }
    }

    void processBounds(ArrayList<int[]> boundFace, int[][] r, int[][] g, int[][] b, int[][] gr,
                       int[][] bw, int w, int h) {
        for (int[] bound : boundFace) {
            int minx = bound[0], maxx = bound[1], miny = bound[2], maxy = bound[3];
            ArrayList<int[]> featureBound = faceDetector.getFeature(bw, minx, maxx, miny, maxy, w, h);
            if (featureBound.size() >= 3) {
                if (featureBound.get(featureBound.size() - 1).length <= 1) {
                    maxy = featureBound.get(featureBound.size() - 1)[0];
                    featureBound.remove(featureBound.size() - 1);
                }
                for (int[] c : featureBound) {
                    createRectangle(r, g, b, gr, bw, c[0], c[1], c[2], c[3], 0, 255, 0);
                }
                createRectangle(r, g, b, gr, bw, minx, maxx, miny, maxy, 0, 255, 0);

                int num_points = 6;

                for (int i = 0; i < 4; i++) {
                    if (featureBound.size() > i) {
                        Log.d("controlpoint", "New Feature");

                        ArrayList<int[]> points;

                        if (i < 2) {
                            points = faceDetector.getEyesAndMouthControlPoints(bw,
                                    featureBound.get(i), num_points, false);
                        } else {
                            faceDetector.fill(bw, featureBound.get(i));
                            points = faceDetector.getEyesAndMouthControlPoints(bw,
                                    featureBound.get(i), num_points, true);
                        }


                        for (int[] point : points) {
                            Log.d("controlpoint", Integer.toString(point[0]) + ' ' + Integer.toString(point[1]));
                            createPoint(r, g, b, gr, bw, point[0], point[1], w, h, 0, 255, 0);
                        }
                    }
                }
                if (featureBound.size() > 4) {
                    ArrayList<int[]> points = faceDetector.getNoseControlPoints(bw, featureBound.get(4));
                    for (int[] point: points) {
                        createPoint(r, g, b, gr, bw, point[0], point[1], w, h, 0, 255, 0);
                    }
                }
                if (featureBound.size() > 5) {
                    faceDetector.fill(bw, featureBound.get(5));
                    ArrayList<int[]> points = faceDetector.getEyesAndMouthControlPoints(bw,
                            featureBound.get(5),num_points, true);
                    for (int[] point : points) {
                        createPoint(r, g, b, gr, bw, point[0], point[1], w, h, 0, 255, 0);
                    }
                }
            }
        }
    }

}
