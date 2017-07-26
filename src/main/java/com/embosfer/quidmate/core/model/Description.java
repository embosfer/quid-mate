package com.embosfer.quidmate.core.model;

/**
 * Created by embosfer on 28/05/2017.
 */
public class Description {

    public final String value;

    private Description(String value) {
        this.value = value;
    }

    public static Description of(String description) {
        return new Description(description);
    }

    @Override
    public String toString() {
        return value;
    }
}
