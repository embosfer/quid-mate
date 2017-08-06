package com.embosfer.quidmate.db;

import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.TransactionType;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by embosfer on 27/07/2017.
 */
public interface DbConnection {

    void open() throws SQLException;

    void close() throws SQLException;

    void store(List<LabeledTransaction> transactions); // TODO store labeled transactions instead

    List<Label> getAllLabels();

    List<TransactionType> getAllTransactionTypes();
}
