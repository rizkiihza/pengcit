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

    public int[][] bezierTransform(int[][] pixels, int h, int w, int c) {
        int[][] newPixels = new int[h][w];
        int[] pixelCount = new int[256];

        for(int i = 0; i < 256; i++) {
            pixelCount[i] = 0;
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                pixelCount[pixels[i][j]] += 1;
            }
        }

        for (int i = 1; i < 256; i++) {
            pixelCount[i] += pixelCount[i - 1];
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                newPixels[i][j] = c * pixelCount[pixels[i][j]] * 255 / (w*h);
            }
        }

        return newPixels;
    }

    public int[][] histogramSpecification(int[][] pixels, int h, int w, double[] c) {
        int cLength = c.length;
        int[] axis = new int[cLength];
        double[] gradient = new double[cLength - 1];
        double[] yIntercept = new double[cLength - 1];

        // create segments
        int segment = 256 / (cLength - 1);
        axis[0] = 0;
        for (int i = 1; i < cLength - 1; i++) {
            axis[i] = i * segment;
        }
        axis[cLength - 1] = 255;

        // create lines
        for (int i = 0; i < cLength - 1; i++) {
            gradient[i] = (c[i+1] - c[i]) / (axis[i+1] - axis[i]);
            yIntercept[i] = c[i] - (gradient[i] * axis[i]);
        }


        // count pixel
        double[] pixelCount = new double[256];

        // for every segment and for every color in segment
        for (int i = 0; i < cLength - 1; i++) {
            for(int j = axis[i]; j <= axis[i+1]; j++) {
                pixelCount[j] = gradient[i] * axis[i] + yIntercept[i];
            }
        }

        // cumulative count
        double[] pixelCountCumulative = new double[256];

        pixelCountCumulative[0] = pixelCount[0];
        for (int i = 1; i < 256; i++) {
            pixelCountCumulative[i] = pixelCount[i] + pixelCountCumulative[i-1];
        }

        // copy to newPixels
        int[][] newPixels = new int[h][w];
        for (int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                double result = 255 * pixelCountCumulative[pixels[i][j]] / pixelCountCumulative[255];
                newPixels[i][j] = (int) Math.round(result);
            }
        }

        return newPixels;
    }
}
