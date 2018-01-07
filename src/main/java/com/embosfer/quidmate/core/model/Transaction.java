package com.embosfer.quidmate.core.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by embosfer on 28/05/2017.
 */
public class Transaction {

    public final LocalDate date;
    public final TransactionType type;
    public final Description description;
    public final DebitCredit debitCredit;
    public final Balance balance;

    private Transaction(LocalDate localDate, TransactionType type, Description description, DebitCredit debitCredit, Balance balance) {
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Transaction)) return false;
        Transaction otherTran = (Transaction) obj;

        return Objects.equals(date, otherTran.date)
                && Objects.equals(type, otherTran.type)
                && Objects.equals(description, otherTran.description)
                && Objects.equals(debitCredit, otherTran.debitCredit)
                && Objects.equals(balance, otherTran.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, type, description, debitCredit, balance);
    }

    @Override
    public String toString() {
        return "[" + date + ", " + type + ", Description: " + description + ", Debit/Credit: " + debitCredit + ", Balance: " + balance + "]";
    }

    public double getDebitCreditAsDouble() {
        return debitCredit.value;
    }
}
