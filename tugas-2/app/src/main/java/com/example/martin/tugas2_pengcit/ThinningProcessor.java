package com.example.martin.tugas2_pengcit;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class ThinningProcessor {
    public class Point {
        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "{" + x + ", " + y + "}";
        }
    }

    public int[][] thinning(final int[][] givenImage, int w, int h) {
        int[][] binaryImage = new int[h][w];
        int[][] resultImage = new int[h][w];

        Queue<Point> blackPoints = new LinkedList<>();
        Queue<Point> temp = new LinkedList<>();

        for(int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (givenImage[i][j] == 255) {
                    binaryImage[i][j] = 0;
                }
                else if (givenImage[i][j] == 0) {
                    binaryImage[i][j] = 1;
                    if (i>0 && i+1<h && j>0 && j+1<w) {
                        blackPoints.add(new Point(i, j));
                    }
                }
            }
        }

        int a, b;
        Queue<Point> pointsToChange = new LinkedList<>();
        boolean hasChange;
        Point p;
        do {
            // step 1
            hasChange = false;
            while (!blackPoints.isEmpty()) {
                p = blackPoints.remove();
                a = getA(binaryImage, p.x, p.y);
                b = getB(binaryImage, p.x, p.y);

                boolean c2, c3, c4, c5;
                c2 = 2 <= b && b <= 6;
                c3 = a == 1;
                c4 = binaryImage[p.x - 1][p.y] * binaryImage[p.x][p.y + 1] * binaryImage[p.x + 1][p.y] == 0;
                c5 = (binaryImage[p.x][p.y + 1] * binaryImage[p.x + 1][p.y] * binaryImage[p.x][p.y - 1] == 0);
                if (c2 && c3 && c4 && c5) {
                    pointsToChange.add(p);
                    hasChange = true;
                } else {
                    temp.add(p);
                }
            }
            while (!pointsToChange.isEmpty()) {
                p = pointsToChange.remove();
                binaryImage[p.x][p.y] = 0;
            }

            // step 2
            while (!temp.isEmpty()) {
                p = temp.remove();
                a = getA(binaryImage, p.x, p.y);
                b = getB(binaryImage, p.x, p.y);

                boolean c2, c3, c4, c5;
                c2 = 2 <= b && b <= 6;
                c3 = a == 1;
                c4 = binaryImage[p.x - 1][p.y] * binaryImage[p.x][p.y + 1] * binaryImage[p.x][p.y - 1] == 0;
                c5 = (binaryImage[p.x - 1][p.y] * binaryImage[p.x + 1][p.y] * binaryImage[p.x][p.y - 1] == 0);
                if (c2 && c3 && c4 && c5) {
                    pointsToChange.add(p);
                    hasChange = true;
                } else {
                    blackPoints.add(p);
                }
            }
            while (!pointsToChange.isEmpty()) {
                p = pointsToChange.remove();
                binaryImage[p.x][p.y] = 0;
            }
        } while (hasChange);


        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (binaryImage[i][j] == 0) {
                    resultImage[i][j] = 255;
                } else if (binaryImage[i][j] == 1) {
                    resultImage[i][j] = 0;
                }
            }
        }

        return resultImage;
    }

    private int getA(int[][] binaryImage, int y, int x) {
        int count = 0;

        if (y - 1 >= 0 && x + 1 < binaryImage[y].length && binaryImage[y - 1][x] == 0 && binaryImage[y - 1][x + 1] == 1) {
            count++;
        }

        if (y - 1 >= 0 && x + 1 < binaryImage[y].length && binaryImage[y - 1][x + 1] == 0 && binaryImage[y][x + 1] == 1) {
            count++;
        }

        if (y + 1 < binaryImage.length && x + 1 < binaryImage[y].length && binaryImage[y][x + 1] == 0 && binaryImage[y + 1][x + 1] == 1) {
            count++;
        }

        if (y + 1 < binaryImage.length && x + 1 < binaryImage[y].length && binaryImage[y + 1][x + 1] == 0 && binaryImage[y + 1][x] == 1) {
            count++;
        }

        if (y + 1 < binaryImage.length && x - 1 >= 0 && binaryImage[y + 1][x] == 0 && binaryImage[y + 1][x - 1] == 1) {
            count++;
        }

        if (y + 1 < binaryImage.length && x - 1 >= 0 && binaryImage[y + 1][x - 1] == 0 && binaryImage[y][x - 1] == 1) {
            count++;
        }

        if (y - 1 >= 0 && x - 1 >= 0 && binaryImage[y][x - 1] == 0 && binaryImage[y - 1][x - 1] == 1) {
            count++;
        }

        if (y - 1 >= 0 && x - 1 >= 0 && binaryImage[y - 1][x - 1] == 0 && binaryImage[y - 1][x] == 1) {
            count++;
        }
        return count;
    }

    private int getB(int[][] binaryImage, int y, int x) {
        int count = 0;

        if (binaryImage[y - 1][x] == 1) {
            count++;
        }

        if (binaryImage[y - 1][x + 1] == 1) {
            count++;
        }

        if (binaryImage[y][x + 1] == 1) {
            count++;
        }

        if (binaryImage[y + 1][x + 1] == 1) {
            count++;
        }

        if (binaryImage[y + 1][x] == 1) {
            count++;
        }

        if (binaryImage[y + 1][x - 1] == 1) {
            count++;
        }

        if (binaryImage[y][x - 1] == 1) {
            count++;
        }

        if (binaryImage[y - 1][x - 1] == 1) {
            count++;
        }

        return count;
    }
}


