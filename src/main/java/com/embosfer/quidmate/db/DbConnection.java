package com.embosfer.quidmate.db;

import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.model.TransactionType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by embosfer on 27/07/2017.
 */
public interface DbConnection {

    void open() throws SQLException;

    void close() throws SQLException;

    void store(List<LabeledTransaction> transactions);

    void store(Label label);

    List<Label> getAllLabels(Optional<Predicate<Label>> optionalFilter);

    List<TransactionType> getAllTransactionTypes();

    List<LabeledTransaction> retrieveLastTransactions(int noTransactions);

    List<Transaction> getTransactionsMatching(String label, LocalDate fromDate, LocalDate toDate);
}
