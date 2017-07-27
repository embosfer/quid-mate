package com.embosfer.quidmate.db;

import com.embosfer.quidmate.core.model.Transaction;

import java.util.List;

/**
 * Created by embosfer on 27/07/2017.
 */
public interface DbConnection {

    void store(List<Transaction> transactions);
}
