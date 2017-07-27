package com.embosfer.quidmate.doubles;

import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.db.DbConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by embosfer on 28/05/2017.
 */
public class FakeDbConnection implements DbConnection {

    List<Transaction> storedTransactions = new ArrayList<>();

    @Override
    public void store(List<Transaction> transactions) {
        storedTransactions.addAll(transactions);
    }

    public void contains(Transaction[] transactions) {
        if (!storedTransactions.containsAll(asList(transactions))) {
            throw new RuntimeException("Transactions were not loaded in DB. " +
                    "\nExpected: " + Arrays.toString(transactions) +
                    "\nActual:" + storedTransactions);
        }
    }

    public void stop() {

    }

    public void stops() throws SQLException {
    }

}
