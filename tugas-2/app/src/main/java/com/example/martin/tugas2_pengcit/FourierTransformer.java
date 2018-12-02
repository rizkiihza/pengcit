package com.example.martin.tugas2_pengcit;

public class FourierTransformer {
    public void transformRadix(double[] real, double[] imag) {
        int n = real.length;
        int levels = 31 - Integer.numberOfLeadingZeros(n);

        double[] cosTable = new double[n / 2];
        double[] sinTable = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            cosTable[i] = Math.cos(2 * Math.PI * i / n);
            sinTable[i] = Math.sin(2 * Math.PI * i / n);
        }

        // Bit-reversed addressing permutation
        for (int i = 0; i < n; i++) {
            int j = Integer.reverse(i) >>> (32 - levels);
            if (j > i) {
                double temp = real[i];
                real[i] = real[j];
                real[j] = temp;
                temp = imag[i];
                imag[i] = imag[j];
                imag[j] = temp;
            }
        }

        // Cooley-Tukey decimation-in-time radix-2 FFT
        for (int size = 2; size <= n; size *= 2) {
            int halfsize = size / 2;
            int tablestep = n / size;
            for (int i = 0; i < n; i += size) {
                for (int j = i, k = 0; j < i + halfsize; j++, k += tablestep) {
                    double tpre =  real[j+halfsize] * cosTable[k] + imag[j+halfsize] * sinTable[k];
                    double tpim = -real[j+halfsize] * sinTable[k] + imag[j+halfsize] * cosTable[k];
                    real[j + halfsize] = real[j] - tpre;
                    imag[j + halfsize] = imag[j] - tpim;
                    real[j] += tpre;
                    imag[j] += tpim;
                }
            }

            // Prevent overflow
            if (size == n) {
                break;
            }
        }
    }

    public void transformBluestein(double[] real, double[] imag) {
        int n = real.length;
        int m = Integer.highestOneBit(n * 2 + 1) << 1;

        // Trignometric tables
        double[] cosTable = new double[n];
        double[] sinTable = new double[n];
        for (int i = 0; i < n; i++) {
            int j = (int)((long)i * i % (n * 2));
            cosTable[i] = Math.cos(Math.PI * j / n);
            sinTable[i] = Math.sin(Math.PI * j / n);
        }

        // Temporary vectors and preprocessing
        double[] areal = new double[m];
        double[] aimag = new double[m];
        for (int i = 0; i < n; i++) {
            areal[i] =  real[i] * cosTable[i] + imag[i] * sinTable[i];
            aimag[i] = -real[i] * sinTable[i] + imag[i] * cosTable[i];
        }
        double[] breal = new double[m];
        double[] bimag = new double[m];

        breal[0] = cosTable[0];
        bimag[0] = sinTable[0];
        for (int i = 1; i < n; i++) {
            breal[i] = breal[m - i] = cosTable[i];
            bimag[i] = bimag[m - i] = sinTable[i];
        }

        // Convolution
        double[] creal = new double[m];
        double[] cimag = new double[m];
        convolve(areal, aimag, breal, bimag, creal, cimag);

        // Postprocessing
        for (int i = 0; i < n; i++) {
            real[i] =  creal[i] * cosTable[i] + cimag[i] * sinTable[i];
            imag[i] = -creal[i] * sinTable[i] + cimag[i] * cosTable[i];
        }
    }

    private void convolve(double[] xreal, double[] ximag, double[] yreal, double[] yimag,
                          double[] outreal, double[] outimag) {
        int n = xreal.length;

        fft(xreal, ximag);
        fft(yreal, yimag);
        for (int i = 0; i < n; i++) {
            double temp = xreal[i] * yreal[i] - ximag[i] * yimag[i];
            ximag[i] = ximag[i] * yreal[i] + xreal[i] * yimag[i];
            xreal[i] = temp;
        }
        fft(ximag, xreal);

        // Scaling (because this FFT implementation omits it)
        for (int i = 0; i < n; i++) {
            outreal[i] = xreal[i] / n;
            outimag[i] = ximag[i] / n;
        }
    }

    public void fft(double[] real, double[] imag) {
        int n = real.length;
        if (n == 0) {
            return;
        } else if ((n & (n - 1)) == 0)  // Is power of 2
            transformRadix(real, imag);
        else {
            transformBluestein(real, imag);
        }
    }

    public void fftForward(ComplexNumber[] data) {
        int n = data.length;
        ComplexNumber[] result = new ComplexNumber[n];
        double[] real = new double[n];
        double[] imag = new double[n];
        for (int i = 0; i < data.length; i++) {
            real[i] = data[i].real;
            imag[i] = data[i].imaginary;
        }

        fft(real,imag);
        for (int i = 0; i < n; i++) {
            data[i] = new ComplexNumber(real[i], imag[i]);
        }
    }

    public void fftBackward(ComplexNumber[] data) {
        int n = data.length;
        ComplexNumber[] result = new ComplexNumber[n];
        double[] real = new double[n];
        double[] imag = new double[n];
        for (int i = 0; i < data.length; i++) {
            real[i] = data[i].real;
            imag[i] = data[i].imaginary;
        }

        fft(imag, real);
        for (int i = 0; i < n; i++) {
            data[i] = new ComplexNumber(real[i] / n, imag[i] / n);
        }
    }

    public void fft2Forward(ComplexNumber[][] data){
        int n = data.length;
        int m = data[0].length;

        for (int i = 0; i < n; i++){
            // copy row
            ComplexNumber[] row = data[i];
            // transform it
            fftForward(row);
            // copy back
            for (int j = 0; j < m; j++) {
                data[i][j] = row[j];
            }
        }

        // process columns
        ComplexNumber[]	col = new ComplexNumber[n];

        for ( int j = 0; j < m; j++ ){
            // copy column
            for (int i = 0; i < n; i++) {
                col[i] = data[i][j];
            }
            // transform it
            fftForward(col);
            // copy back
            for (int i = 0; i < n; i++) {
                data[i][j] = col[i];
            }
        }
    }

    public void fft2Backward(ComplexNumber[][] data){
        int n = data.length;
        int m = data[0].length;

        for (int i = 0; i < n; i++){
            // copy row
            ComplexNumber[] row = data[i];
            // transform it
            fftBackward(row);
            // copy back
            for (int j = 0; j < m; j++) {
                data[i][j] = row[j];
            }
        }

        // process columns
        ComplexNumber[]	col = new ComplexNumber[n];

        for ( int j = 0; j < m; j++ ){
            // copy column
            for (int i = 0; i < n; i++) {
                col[i] = data[i][j];
            }
            // transform it
            fftBackward(col);
            // copy back
            for (int i = 0; i < n; i++) {
                data[i][j] = col[i];
            }
        }
    }

    public ComplexNumber[][] fftImageForward(int[][] gr, int w, int h) {
        ComplexNumber[][] data = new ComplexNumber[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                data[i][j] = new ComplexNumber(gr[i][j], 0);
                if (((i+j) & 0x1) != 0) {
                    data[i][j].real *= -1;
                    data[i][j].imaginary *= -1;
                }
            }
        }

        fft2Forward(data);
        return data;
    }

    public int[][] fftImageBackward(ComplexNumber[][] data, int w, int h) {
        ComplexNumber[][] result = new ComplexNumber[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                result[i][j] = data[i][j];
            }
        }

        fft2Backward(result);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (((i+j) & 0x1) != 0) {
                    result[i][j].real *= -1;
                    result[i][j].imaginary *= -1;
                }
            }
        }

        int[][] res = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int real = (int) result[i][j].real;
                if (real < 0) {
                    real = 0;
                } else if (real > 255) {
                    real = 255;
                }
                res[i][j] = real;
            }
        }
        return res;
    }

    public int[][] convert(ComplexNumber[][] data, int w, int h) {
        int[][] result = new int[w][h];
        double[][] mag = new double[w][h];
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                mag[i][j] = Math.log(data[i][j].getMagnitude() + 1);

                if (mag[i][j] < min) min = mag[i][j];
                if (mag[i][j] > max) max = mag[i][j];
            }
        }

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (min == max) {
                    result[i][j] = 0;
                } else {
                    result[i][j] = (int)(255 * (mag[i][j] - min) / (max - min));
                }
            }
        }
        return result;
    }

    public ComplexNumber[][] mask(ComplexNumber[][] data, int min, int maks) {
        int w = data.length, h = data[0].length;
        int hw = w / 2, hh = h / 2;

        ComplexNumber[][] result = new ComplexNumber[w][h];
        for (int i = 0; i < w; i++) {
            int y = i - hw;

            for (int j = 0; j < h; j++) {
                int x = j - hh;
                int d = (int) Math.sqrt(x * x + y * y);

                if (d < min || d > maks) {
                    result[i][j] = new ComplexNumber();
                } else {
                    result[i][j] = new ComplexNumber(data[i][j]);
                }
            }
        }

        return result;
    }

    public ComplexNumber[][] maskLPF(ComplexNumber[][] data, int r) {
        return mask(data, 0, r);
    }
}
