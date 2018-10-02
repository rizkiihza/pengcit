package com.example.martin.tugas2_pengcit;

import android.graphics.Bitmap;
import android.graphics.Color;
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
        int[][] binaryImage = givenImage;

        Queue<Point> blackPoints = new LinkedList<>();
        Queue<Point> temp = new LinkedList<>();

        for(int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (givenImage[i][j] > 0) {
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
                } else if (b > 0) {
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
                } else if (b > 0) {
                    blackPoints.add(p);
                }
            }
            while (!pointsToChange.isEmpty()) {
                p = pointsToChange.remove();
                binaryImage[p.x][p.y] = 0;
            }
        } while (hasChange);

        int[][] resultImage = new int[w][h];

        while (!blackPoints.isEmpty()) {
            p = blackPoints.remove();
            resultImage[p.y][p.x] = 1;
        }

        return resultImage;
    }

    private boolean[][] visited;


    public int[][] removeNoise(int[][] givenImage, int w, int h) {
        int[][] replicateGivenImage = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                replicateGivenImage[i][j] = givenImage[i][j];
            }
        }
        int startx = -1, starty = -1;
        int total = 0;
        visited = new boolean[w][h];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (replicateGivenImage[j][i] > 0) {
                    if (startx < 0) {
                        startx = j;
                        starty = i;
                    }
                    total += 1;
                }
                visited[j][i] = false;
            }
        }

        dfs(replicateGivenImage, total, w, h, startx, starty);
        return  replicateGivenImage;
    }

    private int dfs(int[][] givenImage, int total, int w, int h, int x, int y) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        int totalLen = 1, cnt = 0, mins = -1;
        ArrayList<Integer> arrLen = new ArrayList<>();
        visited[x][y] = true;
        for (int k = 0; k < dx.length; k++) {
            if (givenImage[x + dx[k]][y + dy[k]] > 0 && !visited[x + dx[k]][y + dy[k]]) {
                int len = dfs(givenImage, total, w, h, x+dx[k], y+dy[k]);
                if (len < mins || mins < 0) {
                    mins = len;
                }
                totalLen += len;
                arrLen.add(len);
                cnt += 1;
            }
        }
        if (cnt > 1) {
            int now = 0;
            for (int k = 0; k < dx.length; k++) {
                if (givenImage[x + dx[k]][y + dy[k]] > 0 && !visited[x + dx[k]][y + dy[k]]) {
                    int len = arrLen.get(now);
                    now += 1;
                    if (len == mins && len < 0.075*total) {
                        dfsRemove(givenImage, w, h, x+dx[k], y+dy[k]);
                        totalLen -= len;
                        break;
                    }
                }
            }
        }
        visited[x][y] = false;
        return totalLen;
    }

    private void dfsRemove(int[][] givenImage, int w, int h, int x, int y) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        visited[x][y] = true;
        givenImage[x][y] = 0;
        for (int k = 0; k < dx.length; k++) {
            if (givenImage[x + dx[k]][y + dy[k]] > 0 && !visited[x + dx[k]][y + dy[k]]) {
                dfsRemove(givenImage, w, h, x+dx[k], y+dy[k]);
            }
        }
        visited[x][y] = false;
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


