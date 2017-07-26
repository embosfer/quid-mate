package com.embosfer.quidmate.core.model;

/**
 * Created by embosfer on 28/05/2017.
 */
public class Balance extends DoubleValueType {

    private Balance(double value) {
        super(value);
    }

    public static Balance of(double value) {
        return new Balance(value);
    }

}
