package com.embosfer.quidmate.core.model;

import java.time.LocalDate;

/**
 * Created by embosfer on 28/05/2017.
 */
public class Transaction {

    public final LocalDate date;
    public final TransactionType type;
    public final Description description;
    public final DebitCredit debitCredit;
    public final Balance balance;

    public Transaction(LocalDate localDate, TransactionType type, Description description, DebitCredit debitCredit, Balance balance) {
        this.date = localDate;
        this.type = type;
        this.description = description;
        this.debitCredit = debitCredit;
        this.balance = balance;
    }

    public static Transaction of(LocalDate localDate, TransactionType type, Description description, DebitCredit debitCredit, Balance balance) {
        return new Transaction(localDate, type, description, debitCredit, balance);
    }

    @Override
    public String toString() {
        return "[" + date + ", " + type + ", " + description + ", " + debitCredit + ", " + balance + "]";
    }
}
