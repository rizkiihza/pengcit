package com.example.martin.tugas2_pengcit;

import java.util.ArrayList;
import java.util.Collections;

public class ConvolutionProcessor {
    private boolean isValid(int x, int y, int w, int h) {
        if (x < 0 || x >= w || y < 0 || y >= h) {
            return false;
        }
        return true;
    }

    public int[][] smoothing(int[][] givenImage, int w, int h) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};

        int[][] new_matrix = new int[w][h];
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                arr.clear();
                arr.add(givenImage[i][j]);
                for (int k = 0; k < 8; k++) {
                    if (isValid(i+dx[k], j+dy[k], w, h)) {
                        arr.add(givenImage[i+dx[k]][j+dy[k]]);
                    }
                }

                Collections.sort(arr);

                new_matrix[i][j] = arr.get(arr.size() / 2);
            }
        }

        return new_matrix;
    }

    public int[][] gradien(int[][] givenImage, int w, int h) {
        int[] dx1 = {1, 1, 1, 0};
        int[] dy1 = {-1, 0, 1, 1};

        int[] dx2 = {-1, -1, -1, 0};
        int[] dy2 = {1, 0, -1, -1};

        int[][] new_matrix = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int max_difference = -1;
                for (int k = 0; k < 4; k++) {
                    int pixel1 = isValid(i+dx1[k], j+dy1[k], w, h) ? givenImage[i+dx1[k]][j+dy1[k]] : 0;
                    int pixel2 = isValid(i+dx2[k], j+dy2[k], w, h) ? givenImage[i+dx2[k]][j+dy2[k]] : 0;

                    int delta = Math.abs(pixel1 - pixel2);
                    if (max_difference == -1 || delta > max_difference) {
                        max_difference = delta;
                    }
                }

                new_matrix[i][j] = max_difference;
            }
        }

        return new_matrix;
    }

    public int[][] difference(int[][] givenImage, int w, int h) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};

        int[][] new_matrix = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int max_difference = -1;
                int current_pixel = givenImage[i][j];
                for (int k = 0; k < 8; k++) {
                    if (isValid(i+dx[k], j+dy[k], w ,h)) {
                        int neighbour_pixel = givenImage[i+dx[k]][j+dy[k]];

                        int delta = Math.abs(neighbour_pixel - current_pixel);
                        if (max_difference == -1 || delta > max_difference) {
                            max_difference = delta;
                        }
                    }
                }

                new_matrix[i][j] = max_difference;
            }
        }

        return new_matrix;
    }

    public int[][] sobel(int[][] givenImage, int w, int h) {
        int[][] new_matrix = new int[w][h];

        int[][] gx = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1},
        };
        int[][] gy = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1},
        };

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int totalx = 0;
                int totaly = 0;
                for (int k = 0; k < gx.length; k++) {
                    for (int l = 0; l < gx[0].length; l++) {
                        if (isValid(i+k-1, j+l-1, w, h)) {
                            int pixel = givenImage[i+k-1][j+l-1];
                            totalx += pixel * gx[k][l];
                            totaly += pixel * gy[k][l];
                        }
                    }
                }
                new_matrix[i][j] = Math.min((int)Math.sqrt((double)totalx*totalx + totaly*totaly), 255);
            }
        }

        return new_matrix;
    }

    public int[][] prewitt(int[][] givenImage, int w, int h) {
        int[][] new_matrix = new int[w][h];

        int[][] gx = {
                {-1, 0, 1},
                {-1, 0, 1},
                {-1, 0, 1},
        };
        int[][] gy = {
                {-1, -1, -1},
                {0, 0, 0},
                {1, 1, 1},
        };

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int totalx = 0;
                int totaly = 0;
                for (int k = 0; k < gx.length; k++) {
                    for (int l = 0; l < gx[0].length; l++) {
                        if (isValid(i+k-1, j+l-1, w, h)) {
                            int pixel = givenImage[i+k-1][j+l-1];
                            totalx += pixel * gx[k][l];
                            totaly += pixel * gy[k][l];
                        }
                    }
                }
                new_matrix[i][j] = Math.min((int)Math.sqrt((double)totalx*totalx + totaly*totaly), 255);
            }
        }

        return new_matrix;
    }

    public int[][] roberts(int[][] givenImage, int w, int h) {
        int[][] new_matrix = new int[w][h];

        int[][] gx = {
                {-1, 0},
                {0, 1},
        };
        int[][] gy = {
                {0, -1},
                {1, 0},
        };

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int totalx = 0;
                int totaly = 0;
                for (int k = 0; k < gx.length; k++) {
                    for (int l = 0; l < gx[0].length; l++) {
                        if (isValid(i+k-1, j+l-1, w, h)) {
                            int pixel = givenImage[i+k-1][j+l-1];
                            totalx += pixel * gx[k][l];
                            totaly += pixel * gy[k][l];
                        }
                    }
                }
                new_matrix[i][j] = Math.min((int)Math.sqrt((double)totalx*totalx + totaly*totaly), 255);
            }
        }

        return new_matrix;
    }

    public int[][] frei_chen(int[][] givenImage, int w, int h) {
        int[][] new_matrix = new int[w][h];

        double sqr = Math.sqrt(2);

        double[][] gx = {
                {-1, 0, 1},
                {-sqr, 0, sqr},
                {-1, 0, 1},
        };
        double[][] gy = {
                {-1, -sqr, -1},
                {0, 0, 0},
                {1, sqr, 1},
        };

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                double totalx = 0;
                double totaly = 0;
                for (int k = 0; k < gx.length; k++) {
                    for (int l = 0; l < gx[0].length; l++) {
                        if (isValid(i+k-1, j+l-1, w, h)) {
                            int pixel = givenImage[i+k-1][j+l-1];
                            totalx += pixel * gx[k][l];
                            totaly += pixel * gy[k][l];
                        }
                    }
                }
                new_matrix[i][j] = Math.min((int)Math.sqrt(totalx*totalx + totaly*totaly), 255);
            }
        }

        return new_matrix;
    }
}
