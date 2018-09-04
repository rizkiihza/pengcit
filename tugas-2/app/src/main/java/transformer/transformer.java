package transformer;

public class transformer {

    public int[][] transform_cumulative(int[][] pixels) {
        int row = pixels.length;
        int col = pixels[0].length;

        int[] count_pixels = new int[256];
        int[][] new_pixels = new int[row][col];

        for (int i = 0; i < count_pixels.length; i += 1) {
            count_pixels[i] = 0;
        }

        for (int[] row_data : pixels) {
            for (int item : row_data) {
                count_pixels[item] += 1;
            }
        }

        for (int i = 1; i < count_pixels.length; i += 1) {
            count_pixels[i] += count_pixels[i-1];
        }

        for (int i = 0; i < row; i+= 1) {
            for (int j = 0; j < col; j += 1) {
                new_pixels[i][j] = (255*count_pixels[pixels[i][j]]) / (row*col);
            }
        }

        return new_pixels;
    }
}
