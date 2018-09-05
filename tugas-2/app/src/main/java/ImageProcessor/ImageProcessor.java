package ImageProcessor;

public class ImageProcessor {

    public int[][] transformCumulative(int[][] pixels, int h, int w) {
        int[] count_pixels = new int[256];
        int[][] new_pixels = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                new_pixels[i][j] = pixels[i][j];
            }
        }

        for (int i = 0; i < count_pixels.length; i ++) {
            count_pixels[i] = 0;
        }

        for (int[] row_data : pixels) {
            for (int item : row_data) {
                count_pixels[item] += 1;
            }
        }

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
