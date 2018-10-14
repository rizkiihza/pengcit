package com.example.martin.tugas2_pengcit;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import uk.co.senab.photoview.PhotoViewAttacher;


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

        for(int i = 1; i + 1 < w; i++) {
            for (int j = 1; j + 1 < h; j++) {
                if (resultImage[i][j] > 0) {
                    if (resultImage[i + 1][j] + resultImage[i][j - 1] == 2) {
                        if (resultImage[i + 1][j - 1] + resultImage[i - 1][j + 1] == 0) {
                            resultImage[i][j] = 0;
                            continue;
                        }
                    }
                    if (resultImage[i - 1][j] + resultImage[i][j - 1] == 2) {
                        if (resultImage[i - 1][j - 1] + resultImage[i + 1][j + 1] == 0) {
                            resultImage[i][j] = 0;
                            continue;
                        }
                    }
                    if (resultImage[i - 1][j] + resultImage[i][j + 1] == 2) {
                        if (resultImage[i - 1][j + 1] + resultImage[i + 1][j - 1] == 0) {
                            resultImage[i][j] = 0;
                            continue;
                        }
                    }
                    if (resultImage[i + 1][j] + resultImage[i][j + 1] == 2) {
                        if (resultImage[i + 1][j + 1] + resultImage[i - 1][j - 1] == 0) {
                            resultImage[i][j] = 0;
                        }
                    }
                }
            }
        }

        return resultImage;
    }

    private boolean[][] used;
    private boolean[][] visited;


    public int[][] removeNoise(int[][] givenImage, int w, int h) {
        int[][] replicateGivenImage = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                replicateGivenImage[i][j] = givenImage[i][j];
            }
        }

        int total = 0;
        used = new boolean[w][h];
        visited = new boolean[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (replicateGivenImage[i][j] > 0) {
                    total += 1;
                }
                used[i][j] = false;
                visited[i][j] = false;
            }
        }

        for(int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (replicateGivenImage[i][j] > 0 && !used[i][j]) {
                    dfs(replicateGivenImage, total, w, h, i, j);
                }
            }
        }
        return  replicateGivenImage;
    }

    private int dfs(int[][] givenImage, int total, int w, int h, int x, int y) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        int totalLen = 1, cnt = 0, mins = -1;
        ArrayList<Integer> arrLen = new ArrayList<>();
        visited[x][y] = true;
        used[x][y] = true;
        for (int k = 0; k < dx.length; k++) {
            if (givenImage[x + dx[k]][y + dy[k]] > 0) {
                if (!visited[x + dx[k]][y + dy[k]]) {
                    int len = dfs(givenImage, total, w, h, x+dx[k], y+dy[k]);
                    if (len < mins || mins < 0) {
                        mins = len;
                    }
                    totalLen += len;
                    arrLen.add(len);
                    cnt += 1;
                }
            }
        }
        if (cnt > 1) {
            int now = 0;
            for (int k = 0; k < dx.length; k++) {
                if (givenImage[x + dx[k]][y + dy[k]] > 0 && !visited[x + dx[k]][y + dy[k]]) {
                    int len = arrLen.get(now);
                    now += 1;
                    if (len == mins && len < 0.1*total) {
                        if (checkRemove(givenImage, w, h, x+dx[k], y+dy[k])) {
                            dfsRemove(givenImage, w, h, x+dx[k], y+dy[k]);
                        }
                        totalLen -= len;
                        break;
                    }
                }
            }
        }
        visited[x][y] = false;
        return totalLen;
    }

    private boolean checkRemove(int[][] givenImage, int w, int h, int x, int y) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        visited[x][y] = true;
        int cnt = 0;
        for (int k = 0; k < dx.length; k++) {
            if (givenImage[x + dx[k]][y + dy[k]] > 0) {
                if (!visited[x + dx[k]][y + dy[k]]) {
                    if (checkRemove(givenImage, w, h, x+dx[k], y+dy[k])) {
                        visited[x][y] = false;
                        return true;
                    }
                }
                cnt += 1;
            }
        }
        visited[x][y] = false;
        return (cnt == 1);
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
    }

    private Point getFirstBlack(int[][] givenImage, int w, int h) {
        int startx = -1, starty = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (givenImage[x][y] > 0) {
                    startx = x;
                    starty = y;
                    break;
                }
            }
        }
        return new Point(startx, starty);
    }

    private Point getLastBlack(int[][] givenImage, int w, int h) {
        int lastx = -1, lasty = -1;
        for (int y = h-1; y >= 0; y--) {
            for (int x = w-1; x >= 0; x--) {
                if (givenImage[x][y] > 0) {
                    lastx = x;
                    lasty = y;
                    break;
                }
            }
        }
        return new Point(lastx, lasty);
    }

    public ArrayList<Point> getEndpoint(int[][] givenImage, int w, int h) {


        used = new boolean[w][h];
        visited = new boolean[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                visited[i][j] = false;
                used[i][j] = false;
            }
        }

        ArrayList<Point> result = new ArrayList<>();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (givenImage[i][j] > 0 && !used[i][j]) {
                    ArrayList<Point> this_result = dfsEndpoint(givenImage, w, h, i, j);
                    for (Point p : this_result) {
                        result.add(p);
                    }
                }
            }
        }
        return result;
    }

    public int getDifferentPart(int[][] givenImage, int w, int h) {
        used = new boolean[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                used[i][j] = false;
            }
        }

        int total_part = 0;
        for(int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (givenImage[i][j] > 0 && !used[i][j]) {
                    total_part += 1;
                    dfsPart(givenImage, w, h, i, j);
                }
            }
        }
        return total_part;
    }

    private void dfsPart(int[][] givenImage, int w, int h, int x, int y) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        used[x][y] = true;
        for (int k = 0; k < dx.length; k++) {
            if (givenImage[x + dx[k]][y+dy[k]] > 0) {
                if (!visited[x+dx[k]][y+dy[k]]) {
                    dfsPart(givenImage, w, h, x+dx[k], y+dy[k]);
                }
            }
        }
    }

    private ArrayList<Point> dfsEndpoint(int[][] givenImage, int w, int h, int x, int y) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        visited[x][y] = true;
        used[x][y] = true;
        int count = 0;
        ArrayList<Point> result = new ArrayList();
        for (int k = 0; k < dx.length; k++) {
            if (givenImage[x + dx[k]][y+dy[k]] > 0) {
                count += 1;
                if (!visited[x+dx[k]][y+dy[k]]) {
                    ArrayList<Point> temp = dfsEndpoint(givenImage, w, h, x+dx[k], y+dy[k]);
                    for (Point p : temp) {
                        result.add(p);
                    }
                }
            }
        }

        if (count == 1) {
            result.add(new Point(x, y));
        }

        return result;
    }

    public int[] countNeighbors(int[][] givenImage, int w, int h) {
        Point start = getFirstBlack(givenImage, w, h);
        int startx = start.x, starty = start.y;

        int[] total_neighbor = new int[8];
        used = new boolean[w][h];
        visited = new boolean[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                visited[i][j] = false;
                used[i][j] = false;
            }
        }

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (givenImage[i][j] > 0 && !used[i][j]) {
                    int[] neighbor = dfsNeighbors(givenImage, w, h, startx, starty);
                    for (int n = 0; n < neighbor.length; n++) {
                        total_neighbor[n] += neighbor[n];
                    }
                }
            }
        }
        return total_neighbor;
    }

    private int[] dfsNeighbors(int[][] givenImage, int w, int h, int x, int y) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        visited[x][y] = true;
        used[x][y] = true;
        int count = 0;
        int[] total = new int[8];
        for (int k = 0; k < dx.length; k++) {
            if (givenImage[x + dx[k]][y+dy[k]] > 0) {
                count += 1;
                if (!visited[x+dx[k]][y+dy[k]]) {
                    int[] temp = dfsNeighbors(givenImage, w, h, x+dx[k], y+dy[k]);
                    for (int l = 0; l < total.length; l++) {
                        total[l] += temp[l];
                    }
                }
            }
        }

        total[count] += 1;

        return total;
    }

    public int countLoop(int[][] givenImage, int w, int h) {

        used = new boolean[w][h];
        visited = new boolean[w][h];

        int total_loop = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                visited[i][j] = false;
                used[i][j] = false;
            }
        }

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (givenImage[i][j] > 0 && !used[i][j]) {
                    total_loop += dfsLoop(givenImage, w, h, i, j);
                }
            }
        }
        return total_loop;
    }

    private int dfsLoop(int[][] givenImage, int w, int h, int x, int y) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        visited[x][y] = true;
        used[x][y] = true;
        int count = 0;
        int count_unvisited = 0;
        int total_loop = 0;
        for (int k = 0; k < dx.length; k++) {
            if (givenImage[x + dx[k]][y+dy[k]] > 0) {
                count += 1;
                if (!visited[x+dx[k]][y+dy[k]]) {
                    count_unvisited += 1;
                }
            }
        }

        // check if this point is a loop
        if (count >= 2 && count_unvisited == 0) {
            total_loop += 1;
        }

        for (int k = 0; k < dx.length; k++) {
            if (givenImage[x + dx[k]][y+dy[k]] > 0) {
                if (!visited[x+dx[k]][y+dy[k]]) {
                    total_loop += dfsLoop(givenImage, w, h, x+dx[k], y+dy[k]);
                }
            }
        }

        return total_loop;
    }

    public int predict(int[][] givenImage, int w, int h) {
        int loop = countLoop(givenImage, w, h);
        int[] neighbors = countNeighbors(givenImage, w, h);
        ArrayList<Point> endpoints = getEndpoint(givenImage, w, h);
        Log.d("hitung", "Loop: " + Integer.toString(loop));
        Log.d("hitung", "Neighbor: " + Arrays.toString(neighbors));
        Log.d("hitung", "Endpoint: " + Arrays.toString(endpoints.toArray()));
        int startx = -1, starty = -1;
        int lastx = -1, lasty = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (givenImage[x][y] > 0) {
                    if (startx < 0) {
                        startx = x;
                        starty = y;
                    }
                    lastx = x;
                    lasty = y;
                }
            }
        }
        Point start = new Point(startx, starty);
        Point last = new Point(lastx, lasty);

        int rightx = -1, righty = -1;
        int leftx = -1, lefty = -1;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (givenImage[x][y] > 0) {
                    if (leftx < 0) {
                        leftx = x;
                        lefty = y;
                    }
                    rightx = x;
                    righty = y;
                }
            }
        }
        Point left = new Point(leftx, lefty);
        Point right = new Point(rightx, righty);


        if (loop>=2) {
            return 8;
        } else if (loop==0) {
            // 1,2,3,5,7
            if (neighbors[1] >= 3) {
                if (neighbors[3] >= 1) {
                    // 1,3
                    Point p = new Point(0, 0);
                    for (Point q : endpoints) {
                        if (q.x > p.x) {
                            p = q;
                        }
                    }
                    if (Math.abs(p.x-last.x) <= 0.1*(last.x-start.x)) {
                        return 1;
                    } else {
                        return 3;
                    }
                } else {
                    return 7;
                }
            } else {
                // 2,5,7
                Point p = endpoints.get(0);
                Point q = endpoints.get(1);
                if (p.y > q.y) {
                    p = endpoints.get(1);
                    q = endpoints.get(0);
                }
                if (p.x > q.x) {
                    return 5;
                } else {
                    if (right.x == q.x) {
                        return 2;
                    } else {
                        return 7;
                    }
                }
            }
        } else {
            // 0,4,6,9
            if (neighbors[1] == 0) {
                return 0;
            } else if (neighbors[1] >= 2) {
                return 4;
            } else {
                // 4,6,9
                Point p = endpoints.get(0);
                if (last.x == p.x && last.y == p.y) {
                    return 4;
                } else if (last.y - p.y < p.y - start.y) {
                    return 9;
                } else {
                    return 6;
                }
            }
        }
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


