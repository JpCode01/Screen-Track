package com.jpcode.screentrack.review;

public enum Rating {
    HALF(0.5),
    ONE(1.0),
    ONE_HALF(1.5),
    TWO(2.0),
    TWO_HALF(2.5),
    THREE(3.0),
    THREE_HALF(3.5),
    FOUR(4.0),
    FOUR_HALF(4.5),
    FIVE(5.0);

    private final double value;

    Rating(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
