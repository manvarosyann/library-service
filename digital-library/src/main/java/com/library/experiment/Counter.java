package com.library.experiment;

import org.springframework.stereotype.Component;

@Component
public class Counter {
    private int counter = 0;

    public void increment() {
        counter++;
    }

    public int getValue() {
        return counter;
    }

    public void reset() {
        counter = 0;
    }
}
