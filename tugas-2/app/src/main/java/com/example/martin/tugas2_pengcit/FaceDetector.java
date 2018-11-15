package com.example.martin.tugas2_pengcit;

import android.graphics.Color;
import android.util.Log;

import java.util.Arrays;

public class FaceDetector {



    double[][] getHSV(int[][] r, int[][] g, int[][] b, int w, int h, int index) {
        double[][] result = new double[w][h];
        float[] hsv = new float[3];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color.RGBToHSV(r[i][j], g[i][j], b[i][j], hsv);
                result[i][j] = hsv[index];
            }
        }

        return result;
    }

    public double[][] getY(int[][] r, int[][]g, int[][] b, int w, int h) {
        double[][] y = new double[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                y[i][j] = 0.299*r[i][j] + 0.287*g[i][j] + 0.11*b[i][j];
            }
        }

        return y;
    }

    public double[][] getCr(int[][] r, double[][] y, int w, int h) {
        double[][] result = new double[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                result[i][j] = r[i][j] - y[i][j];
            }
        }

        return result;
    }

    public double[][] getCb(int[][] b, double[][] y, int w, int h) {
        double[][] result = new double[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                result[i][j] = b[i][j] - y[i][j];
            }
        }

        return result;
    }

    int[][] getSkin(int[][] a, int[][] r, int[][] g, int[][] b, int w, int h) {
        double[][] hue = getHSV(r, g, b, w, h, 0);
        double[][] sat = getHSV(r, g, b, w, h, 1);
        double[][] yM = getY(r, g, b, w, h);
        double[][] crM = getCr(r, yM, w, h);
        double[][] cbM = getCb(b, yM, w, h);
        
        int[][] gr = new int[w][h];
        double y,cb,cr;

        boolean[] c = new boolean[10];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                gr[i][j] = 0;

                c[0] = (0<=hue[i][j] && hue[i][j]<=50 && 0.23<=sat[i][j] && sat[i][j]<=0.68);
                c[1] = (r[i][j]>95 && g[i][j]>40 && b[i][j]>20 && r[i][j]>g[i][j]);
                c[2] = (r[i][j]>b[i][j] && Math.abs(r[i][j]-g[i][j])>15 && a[i][j]>15);

                if (c[0] && c[1] && c[2]) {
                    gr[i][j] = 255;
                }

                cb = cbM[i][j]; cr = crM[i][j]; y = yM[i][j];
                c[0] = (cr>135 && cb>85 && y>80 && cr<=1.5862*cb+20 && cr<=-1.15*cb+301.75);
                c[3] = (cr>=0.3448*cb+76.2069 && cr>=-4.5652*cb+234.5652 && cr<=-2.2857*cb+432.85);
                if (c[0] && c[1] && c[2] && c[3]) {
                    gr[i][j] = 255;
                }
            }
        }
        return gr;
    }

    int[][] preprocess(int[][] gr, int w, int h) {
        ConvolutionProcessor processor = new ConvolutionProcessor();
        return processor.smoothing(gr, w, h);
    }

    boolean[][] visited;
    int maxx, maxy, minx, miny;

    boolean isValid(int x, int y, int w, int h) {
        return (x>=0 && x<w && y>=0 && y<h);
    }

    int dfs(int[][] gr, int x, int y, int w, int h) {
        visited[x][y] = true;
        int sum = 1;
        maxx = Math.max(maxx, x);
        minx = Math.min(minx, x);
        maxy = Math.max(maxy, y);
        miny = Math.min(miny, y);

        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};

        for (int k = 0; k < dx.length; k++) {
            if (isValid(x+dx[k], y+dy[k], w, h) && !visited[x+dx[k]][y+dy[k]] && gr[x+dx[k]][y+dy[k]]>0) {
                sum += dfs(gr, x+dx[k], y+dy[k], w, h);
            }
        }

        return sum;
    }

    int[] getFace(int[][] gr, int w, int h) {
        visited = new boolean[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                visited[i][j] = false;
            }
        }
        int curmaxx = -1, curmaxy = -1, curminx = -1, curminy = -1;
        int tmp = -1;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (gr[i][j]>0 && !visited[i][j]) { // putih
                    Log.d("start", Integer.toString(i) + ' ' + Integer.toString(j));
                    maxx = i; minx = i;
                    maxy = j; miny = j;
                    int sum = dfs(gr, i, j, w, h);
                    if (tmp < sum) {
                        tmp = sum;
                        curmaxx = maxx; curmaxy = maxy;
                        curminx = minx; curminy = miny;
                    }
                }
            }
        }
        int[] result = {curminx, curmaxx, curminy, curmaxy};
        Log.d("Bound", Arrays.toString(result));
        return result;
    }
}
