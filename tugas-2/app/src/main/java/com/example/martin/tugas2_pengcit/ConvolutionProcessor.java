package com.example.martin.tugas2_pengcit;

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
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int total = 0;
                int neighbour_count = 0;
                for (int k = 0; k < 8; k++) {
                    if (isValid(i+dx[k], j+dy[k], w, h)) {
                        total += givenImage[i+dx[k]][j+dy[k]];
                        neighbour_count += 1;
                    }
                }

                new_matrix[i][j] = total / neighbour_count;
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
                for (int k = 0; k < 8; k++) {
                    if (isValid(i+dx[k], j+dy[k], w ,h)) {
                        int current_pixel = givenImage[i][j];
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
}
