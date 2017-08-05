package com.embosfer.quidmate.core;

import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.db.DbConnection;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by embosfer on 01/08/2017.
 */
public class TransactionLabelerTest {

    DbConnection dbConnection = mock(DbConnection.class);
    TransactionLabeler labeler = new TransactionLabeler(dbConnection);
    Transaction transactionToBeLabeled;
    List<Transaction> someTransactions;

    @Before
    public void setUp() {
         transactionToBeLabeled = Transaction.of(null, null, Description.of("Something that doesn't match with any defined label"), null, null);
         someTransactions = asList(transactionToBeLabeled);
    }

    @Test
    public void labelsDbGetLoadedLazilyFromDbOnlyTheFirstTimeWeCallLabel() {

        labeler.label(someTransactions);

        verify(dbConnection, times(1)).getAllLabels();

        labeler.label(someTransactions);
        labeler.label(someTransactions);

        verifyNoMoreInteractions(dbConnection);
    }

    @Test
    public void transactionThatDoesntMatchWithAnyLabelGetsAnEmptyLabelList() {
        when(dbConnection.getAllLabels()).thenReturn(asList(Label.of(null, asList("ABC"), null)));

        List<LabeledTransaction> labeledTransactions = labeler.label(asList(transactionToBeLabeled));

        assertThat(labeledTransactions.size(), equalTo(1));

        LabeledTransaction labeledTransaction = labeledTransactions.get(0);
        assertTrue(labeledTransaction.transaction == transactionToBeLabeled);
        assertThat(labeledTransaction.labels, equalTo(emptyList()));

    }

}