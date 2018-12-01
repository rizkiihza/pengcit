package com.example.martin.tugas2_pengcit;

public class ComplexNumber {
    public double real;
    public double imaginary;

    public ComplexNumber() {
        real = imaginary = 0;
    }

    public ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public ComplexNumber(ComplexNumber c) {
        this.real = c.real;
        this.imaginary = c.imaginary;
    }

    public double getMagnitude() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }
}
