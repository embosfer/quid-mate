package com.embosfer.quidmate.core.model;

/**
 * Created by embosfer on 03/06/2017.
 */
public class DebitCredit extends DoubleValueType {

    private DebitCredit(double value) {
        super(value);
    }

    public static DebitCredit of(double value) {
        return new DebitCredit(value);
    }

    @Override
    public String toString() {
        return "Debit/Credit: " + super.toString();
    }
}
