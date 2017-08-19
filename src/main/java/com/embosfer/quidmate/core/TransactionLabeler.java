package com.embosfer.quidmate.core;

import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.db.DbConnection;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

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
            labels = dbConnection.getAllLabels();
        }

        List<LabeledTransaction> labeledTransactions = new ArrayList<>(transactions.size());
        for (Transaction transaction : transactions) {
            List<Label> matchedLabels = getLabelsThatMatchedFor(transaction);
            labeledTransactions.add(LabeledTransaction.of(transaction, matchedLabels));
        }

        return labeledTransactions;
    }

    private List<Label> getLabelsThatMatchedFor(Transaction transaction) {
        List<Label> matchedLabels = new ArrayList<>();
        for (Label label : labels) {
            label.patternToFind.ifPresent(pattern -> {
                if (pattern.matcher(transaction.description.value).find()) {
                    matchedLabels.add(label);
                    label.parentLabel.ifPresent(parentLabel -> matchedLabels.add(parentLabel));
                }
            });

        }
        return (matchedLabels.size() > 0 ? matchedLabels : emptyList());
    }

}
