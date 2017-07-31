package com.embosfer.quidmate.core.model;

/**
 * Created by embosfer on 31/07/2017.
 */
public class StringValueType {

    public final String value;

    protected StringValueType(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StringValueType)) return false;
        StringValueType other = (StringValueType) obj;
        return this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
