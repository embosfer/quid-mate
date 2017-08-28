package com.embosfer.quidmate.doubles;

import com.embosfer.quidmate.core.model.*;
import com.embosfer.quidmate.db.DbConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.lang.Thread.sleep;
import static java.util.Collections.emptyList;

/**
 * Created by embosfer on 28/05/2017.
 */
public class FakeDbConnection implements DbConnection {

    List<LabeledTransaction> storedTransactions = new ArrayList<>();
    List<LabeledTransaction> loadedTransactions = new ArrayList<>();
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
    public void store(Label label) {
        throw new RuntimeException("Forgot to impl.");
    }

    @Override
    public List<Label> getAllLabels(Optional<Predicate<Label>> optionalFilter) {
        return labelsInDb;
    }

    @Override
    public List<TransactionType> getAllTransactionTypes() {
        // not needed
        return emptyList();
    }

    @Override
    public List<LabeledTransaction> retrieveLastTransactions(int noTransactions) {
        return loadedTransactions;
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

    public void hasLoaded(Label label) {
        this.labelsInDb.add(label);
    }

    public void hasLoaded(Transaction transaction, String leafLabel) {
        Label label = Label.of(0, Description.of(leafLabel), null, leafLabel != null ? new String[]{leafLabel} : null);
        loadedTransactions.add(LabeledTransaction.of(transaction, Optional.of(label)));
    }
}
