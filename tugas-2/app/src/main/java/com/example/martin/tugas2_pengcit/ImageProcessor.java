package com.example.martin.tugas2_pengcit;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ImageProcessor {

    public int editDistance(ArrayList<Integer> a, ArrayList<Integer> b) {
        int length_a = a.size();
        int length_b = b.size();
        int[][] dp = new int[length_a+1][length_b+1];

        for (int i = 0; i <= length_a; i++) {
            for (int j = 0; j <= length_b; j ++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = max(i, j);
                    continue;
                }

                int insert = 1 + dp[i][j-1];
                int delete = 1 + dp[i-1][j];
                int replace = 1 + dp[i-1][j-1];

                if (a.get(i-1) == b.get(j-1)) {
                    --replace;
                }

                dp[i][j] = min(min(insert, delete), replace);
            }
        }

        return dp[length_a][length_b];
    }

    public double errorSum(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < 8; i++) {
            result += (a[i]-b[i])*(a[i]-b[i]);
        }
        return result;
    }

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

    public int[][] convertToBW(int[][] red, int[][] green, int[][] blue, int w, int h) {
        int result[][] = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int gray = (red[i][j] + green[i][j] + blue[i][j]) / 3;
                if (gray >= 128) {
                    result[i][j] = 1;
                } else {
                    result[i][j] = 0;
                }
            }
        }

        return result;
    }

    public ArrayList<Integer> getChainCode(int[][] pixels, int w, int h) {
        ArrayList<Integer> result = new ArrayList<>();

        int startx = -1, starty = -1;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (pixels[i][j] == 0) {
                    startx = i;
                    starty = j;
                    break;
                }
            }
            if (startx >= 0) {
                break;
            }
        }

        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        int nowx = startx, nowy = starty;
        int dir = 0;
        boolean start = false;
        while (nowx != startx || nowy != starty || !start) {
            boolean done = false;
            int firstDir = (dir + 5) % 8;
            for (int i = firstDir; i != firstDir || !done; i = (i + 1) % 8) {
                if (nowx + dx[i] < w && nowx + dx[i] >= 0 && nowy + dy[i] < h && nowy + dy[i] >= 0) {
                    if (pixels[nowx + dx[i]][nowy + dy[i]] == 0) {
                        result.add(i);
                        nowx += dx[i];
                        nowy += dy[i];
                        dir = i;
                        break;
                    }
                }
                done = true;
            }
            start = true;
        }

        return result;
    }

    public double[] getChainFrequency(int[][] pixels, int w, int h) {
        double[] result = new double[8];

        int startx = -1, starty = -1;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (pixels[i][j] == 0) {
                    startx = i;
                    starty = j;
                    break;
                }
            }
            if (startx >= 0) {
                break;
            }
        }

        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        int nowx = startx, nowy = starty;
        int dir = 0, sum = 0;
        boolean start = false;
        while (nowx != startx || nowy != starty || !start) {
            boolean done = false;
            int firstDir = (dir + 5) % 8;
            for (int i = firstDir; i != firstDir || !done; i = (i + 1) % 8) {
                if (nowx + dx[i] < w && nowx + dx[i] >= 0 && nowy + dy[i] < h && nowy + dy[i] >= 0) {
                    if (pixels[nowx + dx[i]][nowy + dy[i]] == 0) {
                        result[i] += 1;
                        nowx += dx[i];
                        nowy += dy[i];
                        dir = i;
                        sum += i;
                        break;
                    }
                }
                done = true;
            }
            start = true;
        }

        for (int i = 0; i < 8; i++) {
            result[i] /= sum;
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
            axis[i] = i * segment - 1;
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
                pixelCount[j] = gradient[i] * j + yIntercept[i];
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

    public int[][] histogramSpecificationSort(int[][] pixels, int h, int w, double[] c) {
        int cLength = c.length;
        int[] axis = new int[cLength];
        double[] gradient = new double[cLength - 1];
        double[] yIntercept = new double[cLength - 1];

        // create segments
        int segment = 256 / (cLength - 1);
        axis[0] = 0;
        for (int i = 1; i < cLength - 1; i++) {
            axis[i] = i * segment - 1;
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
                pixelCount[j] = gradient[i] * j + yIntercept[i];
            }
        }

        // cumulative count
        double[] pixelCountCumulative = new double[256];

        pixelCountCumulative[0] = pixelCount[0];
        for (int i = 1; i < 256; i++) {
            pixelCountCumulative[i] = pixelCount[i] + pixelCountCumulative[i-1];
        }

        for (int i = 0; i < 256; i++) {
            pixelCountCumulative[i] = pixelCountCumulative[i] / pixelCountCumulative[255];
        }

        int[] tmp = countPixels(pixels);
        double[] count = new double[256];
        for (int i=0;i<256;++i) {
            count[i] = (double)tmp[i]/(h*w);
        }

        // cumulative count
        double[] countCumulative = new double[256];

        countCumulative[0] = count[0];
        for (int i = 1; i < 256; i++) {
            countCumulative[i] = count[i] + countCumulative[i-1];
        }

        int[] map = new int[256];
        int index = 0;
        for (int i=0;i<256;++i) {
            while (index<255 && pixelCountCumulative[index]<countCumulative[i]) {
                ++index;
            }
            if (index==0) map[i] = 0;
            else if (index==255) map[i] = 255;
            else if (pixelCountCumulative[index]-countCumulative[i]<=countCumulative[i]-pixelCountCumulative[index-1]) map[i] = index;
            else map[i] = index-1;
        }

        int[][] new_pixels = new int[h][w];
        for (int i=0;i<h;++i) {
            for (int j=0;j<w;++j) {
                new_pixels[i][j] = map[pixels[i][j]];
            }
        }

        return new_pixels;
    }
}

