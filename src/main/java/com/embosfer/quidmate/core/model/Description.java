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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Description)) return false;
        Description other = (Description) obj;
        return this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
