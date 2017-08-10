package com.embosfer.quidmate.core.model;

import com.google.common.collect.ImmutableList;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * Created by embosfer on 31/07/2017.
 */
public class LabeledTransaction {

    public final Transaction transaction;
    public final List<Label> labels;

    private LabeledTransaction(Transaction transaction, List<Label> labels) {
        this.transaction = transaction;
        this.labels = ImmutableList.copyOf(labels);
    }

    public static LabeledTransaction of(Transaction transaction, List<Label> labels) {
        return new LabeledTransaction(transaction, labels);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabeledTransaction)) return false;
        LabeledTransaction otherTran = (LabeledTransaction) obj;

        return this.transaction.equals(otherTran.transaction) && Objects.equals(labels, otherTran.labels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction, labels); // TODO look at refactoring this
    }

    @Override
    public String toString() {
        return "[" + transaction.date + ", " + transaction.type + ", Description: " + transaction.description
                + ", Debit/Credit: " + transaction.debitCredit + ", Balance: " + transaction.balance + ", Labels: " + labels + "]"; // TODO refactor this?
    }

    // These getters are created so JavaFX columns can be retrieved via reflection...
    public LocalDate getDate() {
        return transaction.date;
    }

    public TransactionType getType() {
        return transaction.type;
    }

    public Description getDescription() {
        return transaction.description;
    }

    public DebitCredit getDebitCredit() {
        return transaction.debitCredit;
    }

    public Balance getBalance() {
        return transaction.balance;
    }

    public String getLabels() {
        return labels.stream()
                .map(label -> label.description.value)
                .collect(joining(" "));
    }

}
