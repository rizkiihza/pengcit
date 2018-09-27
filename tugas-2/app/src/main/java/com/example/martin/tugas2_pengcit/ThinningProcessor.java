package com.example.martin.tugas2_pengcit;

import java.util.LinkedList;
import java.util.List;


public class ThinningProcessor {
    public class Point {
        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public int[][] thinning(final int[][] givenImage, int w, int h) {
        int[][] binaryImage = new int[h][w];
        int[][] resultImage = new int[h][w];

        for(int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (givenImage[i][j] == 255) {
                    binaryImage[i][j] = 0;
                }
                else if (givenImage[i][j] == 0) {
                    binaryImage[i][j] = 1;
                }
            }
        }

        int a, b;
        List<Point> pointsToChange = new LinkedList();
        boolean hasChange;
        do {
            // step 1
            hasChange = false;
            for (int y = 1; y + 1 < binaryImage.length; y++) {
                for (int x = 1; x + 1 < binaryImage[y].length; x++) {
                    a = getA(binaryImage, y, x);
                    b = getB(binaryImage, y, x);

                    boolean c1, c2, c3, c4, c5;
                    c1 = binaryImage[y][x] == 1;
                    c2 = 2 <= b && b <= 6;
                    c3 = a == 1;
                    c4 = binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y + 1][x] == 0;
                    c5 = (binaryImage[y][x + 1] * binaryImage[y + 1][x] * binaryImage[y][x - 1] == 0);
                    if ( c1 && c2 && c3 && c4 && c5) {
                        pointsToChange.add(new Point(x, y));
                        hasChange = true;
                    }
                }
            }
            for (Point point : pointsToChange) {
                binaryImage[point.y][point.x] = 0;
            }
            pointsToChange.clear();

            // step 2
            for (int y = 1; y + 1 < binaryImage.length; y++) {
                for (int x = 1; x + 1 < binaryImage[y].length; x++) {
                    a = getA(binaryImage, y, x);
                    b = getB(binaryImage, y, x);

                    boolean c1, c2, c3, c4, c5;
                    c1 = binaryImage[y][x] == 1;
                    c2 = 2 <= b && b <= 6;
                    c3 = a == 1;
                    c4 = (binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y][x - 1] == 0);
                    c5 = (binaryImage[y - 1][x] * binaryImage[y + 1][x] * binaryImage[y][x - 1] == 0);
                    if ( c1 && c2 && c3 && c4 && c5) {
                        pointsToChange.add(new Point(x, y));
                        hasChange = true;
                    }
                }
            }
            for (Point point : pointsToChange) {
                binaryImage[point.y][point.x] = 0;
            }
            pointsToChange.clear();
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


