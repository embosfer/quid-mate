package com.embosfer.quidmate.core;

import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.db.DbConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by embosfer on 31/07/2017.
 */
public class TransactionLabeler {

    private final DbConnection dbConnection;
    private List<Label> labels; // TODO volatile?

    public TransactionLabeler(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public List<LabeledTransaction> label(List<Transaction> transactions) {
        if (labels == null) {
            labels = dbConnection.getAllLabels(); // condition: this returns only labels with patterns
        }

        List<LabeledTransaction> labeledTransactions = new ArrayList<>(transactions.size());
        for (Transaction transaction : transactions) {
            Optional<Label> leafLabel = getLabelThatMatchedFor(transaction);
            labeledTransactions.add(LabeledTransaction.of(transaction, leafLabel));
        }

        return labeledTransactions;
    }

    private Optional<Label> getLabelThatMatchedFor(Transaction transaction) {
        for (Label label : labels) {
            if (label.patternToFind.get().matcher(transaction.description.value).find()) {
                return Optional.of(label);
            }
        }
        return Optional.empty();
    }
}