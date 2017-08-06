package com.embosfer.quidmate.db;

import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;

import java.util.List;

/**
 * Created by embosfer on 27/07/2017.
 */
public class DefaultDbConnection implements DbConnection {

    @Override
    public void store(List<LabeledTransaction> transactions) {
        throw new RuntimeException("Forgot to impl.");
    }

    @Override
    public List<Label> getAllLabels() {
        throw new RuntimeException("Forgot to impl.");
    }
}
