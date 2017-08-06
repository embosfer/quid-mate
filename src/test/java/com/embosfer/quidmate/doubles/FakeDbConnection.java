package com.embosfer.quidmate.doubles;

import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.TransactionType;
import com.embosfer.quidmate.db.DbConnection;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static java.util.Collections.emptyList;

/**
 * Created by embosfer on 28/05/2017.
 */
public class FakeDbConnection implements DbConnection {

    List<LabeledTransaction> storedTransactions = new ArrayList<>();
    List<Label> labelsInDb = new ArrayList<>();

    @Override
    public void open() {
        // not needed: do nothing
    }

    @Override
    public void close() {
        // not needed: do nothing
    }

    @Override
    public void store(List<LabeledTransaction> transactions) {
        storedTransactions.addAll(transactions);
    }

    @Override
    public List<Label> getAllLabels() {
        return labelsInDb;
    }

    @Override
    public List<TransactionType> getAllTransactionTypes() {
        // not needed
        return emptyList();
    }

    public void contains(List<LabeledTransaction> transactions) throws InterruptedException {
        int waitingTimeInMillis = 0;
        while (waitingTimeInMillis < 1000) {
            if (storedTransactions.isEmpty()) {
                sleep(100);
                waitingTimeInMillis += 100;
            } else break;
        }
        if (!storedTransactions.containsAll(transactions)) {
            throw new RuntimeException("Transactions were not loaded in DB. " +
                    "\nExpected: " + transactions +
                    "\nActual:" + storedTransactions);
        }
    }

    public void has(Label label) {
        this.labelsInDb.add(label);
    }
}
