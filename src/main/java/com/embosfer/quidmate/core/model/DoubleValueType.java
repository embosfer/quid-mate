package com.embosfer.quidmate.core.model;

/**
 * Created by embosfer on 23/07/2017.
 */
public class DoubleValueType {

    public final double value;

    public DoubleValueType(double value) {
        this.value = value;
    }

    public static double fromCSV(String csvValue) {
        return Double.parseDouble(csvValue.replace("£", ""));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DoubleValueType)) return false;
        DoubleValueType other = (DoubleValueType) obj;
        return this.value == other.value;
    }

    @Override
    public String toString() {
        return value >= 0 ? "+£" + String.format("%.2f", value) : "-£" + String.format("%.2f", value).substring(1);
    }
}
