package com.example.martin.tugas2_pengcit;

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

}
