package com.embosfer.quidmate.core.model;

/**
 * Created by embosfer on 23/07/2017.
 */
public class DoubleValueType extends ValueType {

    public final double value;

    public DoubleValueType(double value) {
        this.value = value;
    }

    public static double fromCSV(String csvValue) {
        return Double.valueOf(csvValue.replace("£", ""));
    }

    @Override
    public String toString() {
        return value >= 0 ? "+£" + String.format("%.2f", value) : "-£" + String.format("%.2f", value).substring(1);
    }
}
