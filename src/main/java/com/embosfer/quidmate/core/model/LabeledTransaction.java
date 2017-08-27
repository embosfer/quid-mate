package com.embosfer.quidmate.core.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by embosfer on 31/07/2017.
 */
public class LabeledTransaction {

    public final Transaction transaction;
    public final Optional<Label> leafLabel;
    public final Optional<Label> rootLabel;

    private LabeledTransaction(Transaction transaction, Optional<Label> leafLabel) {
        this.transaction = transaction;
        this.leafLabel = leafLabel;
        this.rootLabel = getRootLabelFrom(this.leafLabel);
    }

    public static LabeledTransaction of(Transaction transaction, Optional<Label> leafLabel) {
        return new LabeledTransaction(transaction, leafLabel);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabeledTransaction)) return false;
        LabeledTransaction otherTran = (LabeledTransaction) obj;

        return this.transaction.equals(otherTran.transaction)
                && Objects.equals(leafLabel, otherTran.leafLabel)
                && Objects.equals(rootLabel, otherTran.rootLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction, leafLabel, rootLabel);
    }

    @Override
    public String toString() { // TODO refactor this toString with the Transaction one?
        return "[" + transaction.date + ", " + transaction.type + ", Description: " + transaction.description
                + ", Debit/Credit: " + transaction.debitCredit + ", Balance: " + transaction.balance + ", Labels: " + getLabels() + "]";
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
        return this.leafLabel.map(label -> label.description.value).orElse("");
    }

    private Optional<Label> getRootLabelFrom(Optional<Label> leafLabel) {
        Optional<Label> rootLabel = leafLabel;
        Optional<Label> currLabel = leafLabel;
        while (currLabel.isPresent()) {
            rootLabel = currLabel;
            currLabel = currLabel.get().parentLabel;
        }
        return rootLabel;
    }

}
