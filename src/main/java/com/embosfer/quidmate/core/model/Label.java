package com.embosfer.quidmate.core.model;

/**
 * Created by embosfer on 31/07/2017.
 */
public class Label extends StringValueType {

    private Label(String value) {
        super(value);
    }

    public static Label of(String value) {
        return new Label(value);
    }
}
