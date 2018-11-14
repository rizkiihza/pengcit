package com.example.martin.tugas2_pengcit;

public class FaceDetector {
    public double[][] getY(int[][] r, int[][]g, int[][] b) {
        int row = r.length;
        int col = r[0].length;
        double[][] y = new double[row][col];

        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                y[i][j] = 0.2999*r[i][j] + 0.287*g[i][j] + 0.11*b[i][j];
            }
        }

        return y;
    }

    public double[][] getCr(int[][] r, double[][] y) {
        int row = r.length;
        int col = r[0].length;

        double[][] result = new double[row][col];

        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                result[i][j] = r[i][j] - y[i][j];
            }
        }

        return result;
    }

    public double[][] getCb(int[][] b, double[][] y) {
        int row = b.length;
        int col = b[0].length;

        double[][] result = new double[row][col];

        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                result[i][j] = b[i][j] - y[i][j];
            }
        }

        return result;
    }
}
