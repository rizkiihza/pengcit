package com.example.martin.tugas2_pengcit;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

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

    private boolean[][] visited;
    private int maxx, maxy, minx, miny;

    boolean isValid(int x, int y, int w, int h) {
        return (x>=0 && x<w && y>=0 && y<h);
    }

    int dfs(int[][] gr, int x, int y, int w, int h, int color) {
        visited[x][y] = true;
        int sum = 1;
        maxx = Math.max(maxx, x);
        minx = Math.min(minx, x);
        maxy = Math.max(maxy, y);
        miny = Math.min(miny, y);

        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};

        for (int k = 0; k < dx.length; k++) {
            if (isValid(x+dx[k], y+dy[k], w, h) && !visited[x+dx[k]][y+dy[k]] && gr[x+dx[k]][y+dy[k]] == color) {
                sum += dfs(gr, x+dx[k], y+dy[k], w, h, color);
            }
        }

        return sum;
    }

    int dfsReset(int[][] gr, int x, int y, int w, int h, int color) {
        visited[x][y] = false;
        int sum = 1;
        maxx = Math.max(maxx, x);
        minx = Math.min(minx, x);
        maxy = Math.max(maxy, y);
        miny = Math.min(miny, y);

        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};

        for (int k = 0; k < dx.length; k++) {
            if (isValid(x+dx[k], y+dy[k], w, h) && visited[x+dx[k]][y+dy[k]] && gr[x+dx[k]][y+dy[k]] == color) {
                sum += dfsReset(gr, x+dx[k], y+dy[k], w, h, color);
            }
        }

        return sum;
    }

    ArrayList<int[]> getFace(int[][] gr, int w, int h) {
        visited = new boolean[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                visited[i][j] = false;
            }
        }

        ArrayList<int[]> candidate = new ArrayList<>();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (gr[i][j]>0 && !visited[i][j]) { // putih
                    maxx = i; minx = i;
                    maxy = j; miny = j;
                    dfs(gr, i, j, w, h, 255);
                    int[] result = {minx, maxx, miny, maxy};
                    candidate.add(result);
                }
            }
        }
        return candidate;
    }

    void resetOutside(int[][] gr, int w, int h) {
        ArrayList<int[]> q = new ArrayList<>();
        q.add(new int[]{0, 0});
        visited[0][0] = true;
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        while (q.size() > 0) {
            int[] a = q.get(0);
            int x = a[0], y = a[1];
            q.remove(0);

            for (int k = 0; k < dx.length; k++) {
                if (isValid(x+dx[k], y+dy[k], w, h) && !visited[x+dx[k]][y+dy[k]] && gr[x+dx[k]][y+dy[k]] == 0) {
                    visited[x+dx[k]][y+dy[k]] = true;
                    q.add(new int[]{x+dx[k], y+dy[k]});
                }
            }
        }
    }

    ArrayList<int[]> getFeature(int[][] gr, int cminx, int cmaxx, int cminy, int cmaxy, int w, int h) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                visited[i][j] = false;
            }
        }

        ArrayList<int[]> result = new ArrayList<>();

        int cmidx = (cminx + cmaxx) / 2;
        int featureCount = 1;
        for (int j = cminy; j <= cmaxy; j++) {
            if (featureCount <= 2) {
                int xleft = -1, yleft = -1, xright = -1, yright = -1;
                int lminx = -1, lmaxx = -1, lminy = -1, lmaxy = -1;
                int rminx = -1, rmaxx = -1, rminy = -1, rmaxy = -1;
                boolean found = false;

                // get left
                int size_left = 0;
                for (int i = cmidx; i >= cminx; i--) {
                    if (gr[i][j] == 0 && !visited[i][j]) {
                        maxx = i; minx = i;
                        miny = j; maxy = j;
                        size_left = dfs(gr, i, j, w, h, 0);
                        lminx = minx; lmaxx = maxx; lminy = miny; lmaxy = maxy;
                        xleft = i;
                        yleft = j;
                        break;
                    }
                }

                // get right
                int size_right = 0;
                for (int i = cmidx+1; i <= cmaxx; i++) {
                    if (gr[i][j] == 0 && !visited[i][j]) {
                        maxx = i; minx = i;
                        miny = j; maxy = j;
                        size_right = dfs(gr, i, j, w, h, 0);
                        rminx = minx; rmaxx = maxx; rminy = miny; rmaxy = maxy;
                        xright = i;
                        yright = j;
                        break;
                    }
                }

                if (size_left > 100 && size_right > 100 && size_left < 300 && size_right < 300) {
                    found = true;
                } else {
                    if (xleft != -1) {
                        dfsReset(gr, xleft, yleft, w, h, 0);
                    }
                    if (xright != -1) {
                        dfsReset(gr, xright, yright, w, h, 0);
                    }

                }
                if (found) {
                    result.add(new int[]{lminx, lmaxx, lminy, lmaxy});
                    result.add(new int[]{rminx, rmaxx, rminy, rmaxy});
                    featureCount += 1;
                }
            }

            else if (featureCount <= 4) {
                boolean found = false;
                int hminx = w+1, hminy = h+1, hmaxx = -1, hmaxy = -1;

                // mata kiri
                int xmkiri = (featureCount == 3)? result.get(2)[1]:result.get(2)[0];
                int xmnkanan = (featureCount == 3)?result.get(3)[0]:result.get(3)[1];

                // hidung dan mulut
                int thresholdL = (featureCount == 3)? 100: 200;
                int thresholdU = (featureCount == 3)? 200: 800;

                ArrayList<int[]> points = new ArrayList<>();
                for (int i = xmkiri; i <= xmnkanan; i++) {
                    if (gr[i][j] == 0 && !visited[i][j]) {
                        maxx = i; minx = i; maxy = j; miny = j;
                        dfs(gr, i, j, w, h, 0);

                        points.add(new int[]{i, j});
                        hminx = Math.min(hminx, minx);
                        hmaxx = Math.max(hmaxx, maxx);
                        hminy = Math.min(hminy, miny);
                        hmaxy = Math.max(hmaxy, maxy);
                    }
                }

                int luas = (hmaxx - hminx + 1) * (hmaxy - hminy + 1);
                if (luas > thresholdL && luas < thresholdU) {
                    found = true;
                } else {
                    for (int[] p : points) {
                        dfsReset(gr, p[0], p[1], w, h, 0);
                    }
                }

                if (found) {
                    result.add(new int[]{hminx, hmaxx, hminy, hmaxy});
                    featureCount += 1;
                }
            }
        }
        return result;
    }

    int[] getNose(int[][] gr, int cminx, int cmaxx, int cminy, int cmaxy, int w, int h) {
        visited = new boolean[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                visited[i][j] = false;
            }
        }
        //dfs(gr, cminx, cminy, w, h, 0);/;p0

        int midx = (cmaxx + cminx) / 2;
        for (int j = cminy; j <= cmaxy; j++) {
            if (gr[midx][j]==0 && !visited[midx][j]) {
                minx = midx; maxx = midx;
                miny = j; maxy = j;
                int sum = dfs(gr, midx, j, w, h, 0);
                int[] tmp = {minx, maxx, miny, maxy};
                Log.d("bound", Arrays.toString(tmp));
                Log.d("bound", Integer.toString(sum));
                if (sum >= 20 && Math.abs(((maxx + minx) / 2) - midx) <= 10 && maxy - miny <= 8) {
                    int[] result = {minx - 5, maxx + 5, miny - 10, maxy + 2};
                    Log.d("bound", Arrays.toString(result));
                    return result;
                }
            }
        }
        return null;
    }
}
