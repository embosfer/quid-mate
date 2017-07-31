package com.embosfer.quidmate.core.model;

/**
 * Created by embosfer on 28/05/2017.
 */
public class Description extends StringValueType {

    private Description(String value) {
        super(value);
    }

    public static Description of(String description) {
        return new Description(description);
    }

}
