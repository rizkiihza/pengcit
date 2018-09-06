package com.example.martin.tugas2_pengcit;

public class ImageProcessor {

    public int[] countPixels(int [][] pixels) {
        int result[] = new int[256];
        for (int i = 0; i < result.length; i++) {
            result[i] = 0;
        }

        for (int[] row_data : pixels) {
            for (int item : row_data) {
                result[item]++;
            }
        }

        return result;
    }

    public int[][] linearStretching(int[][] pixels, int h, int w) {
        int minimumPixel = 256;
        int maximumPixel = -1;
        int[] bins = new int[256];
        int[][] new_pixels = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (pixels[i][j] < minimumPixel) {
                    minimumPixel = pixels[i][j];
                }

                if (pixels[i][j] > maximumPixel) {
                    maximumPixel = pixels[i][j];
                }
            }
        }

        int difference = maximumPixel - minimumPixel;
        for (int i = 0; i < difference; i++) {
            bins[minimumPixel+i] = (i * 255) / difference;
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                new_pixels[i][j] = bins[pixels[i][j]];
            }
        }

        return new_pixels;
    }

    public int[][] transformCumulative(int[][] pixels, int h, int w) {
        int[] count_pixels = countPixels(pixels);
        int[][] new_pixels = new int[h][w];

        for (int i = 1; i < count_pixels.length; i ++) {
            count_pixels[i] += count_pixels[i-1];
        }

        for (int i = 0; i < h; i+= 1) {
            for (int j = 0; j < w; j += 1) {
                new_pixels[i][j] = (255*count_pixels[pixels[i][j]]) / (h*w);
            }
        }

        return new_pixels;
    }
}
