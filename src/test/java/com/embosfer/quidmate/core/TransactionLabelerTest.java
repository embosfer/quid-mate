package com.embosfer.quidmate.core;

import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.db.DbConnection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static com.embosfer.quidmate.core.model.TransactionType.CASH_WDL;
import static java.util.Arrays.asList;
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
        when(dbConnection.getAllLabels()).thenReturn(asList(Label.of(1, null, null, "THIS_WONT_MATCH")));

        List<LabeledTransaction> labeledTransactions = labeler.label(asList(transactionToBeLabeled));

        assertThat(labeledTransactions.size(), equalTo(1));

        LabeledTransaction labeledTransaction = labeledTransactions.get(0);
        assertTrue(labeledTransaction.transaction == transactionToBeLabeled);
        assertThat(labeledTransaction.leafLabel, equalTo(Optional.empty()));
    }

    @Test
    public void labelsCorrectlyAParentLabelContainingSeveralMatchingWords() {
        Label parentLabel = Label.of(1, Description.of("Parent label"), null, "WORD1", "WORD2");
        when(dbConnection.getAllLabels()).thenReturn(asList(parentLabel));

        Transaction t1 = Transaction.of(null, null, Description.of("blah WORD1 blah"), null, null);
        Transaction t2 = Transaction.of(null, null, Description.of("blah WORD2 blah"), null, null);
        List<LabeledTransaction> labeledTransactions = labeler.label(asList(t1, t2));

        assertThat(labeledTransactions.size(), equalTo(2));
        // TODO LocalDate gets loaded from DB (jooq) with a day less! a
        assertThat(labeledTransactions.get(0).leafLabel.get(), equalTo(parentLabel));
        assertThat(labeledTransactions.get(1).leafLabel.get(), equalTo(parentLabel));
    }

    @Test
    public void labelsCorrectlyAChildLabelContainingSeveralMatchingWords() {
        Label parentLabel = Label.of(1, Description.of("Parent label"), null, null);
        Label childLabel = Label.of(2, Description.of("Child label with multiple words to find"), parentLabel, "WORD1", "WORD2");
        when(dbConnection.getAllLabels()).thenReturn(asList(childLabel));

        Transaction t1 = Transaction.of(null, null, Description.of("blah WORD1 blah"), null, null);
        Transaction t2 = Transaction.of(null, null, Description.of("blah WORD2 blah"), null, null);
        List<LabeledTransaction> labeledTransactions = labeler.label(asList(t1, t2));

        assertThat(labeledTransactions.size(), equalTo(2));
        assertThat(labeledTransactions.get(0).leafLabel.get(), equalTo(childLabel));
        assertThat(labeledTransactions.get(1).leafLabel.get(), equalTo(childLabel));
    }

    @Ignore
    @Test
    public void cashWithdrawalsHaveTheirOwnExclusiveLabel() {
        Label someLabel = Label.of(1, Description.of("Some label"), null, "WORD1");
        when(dbConnection.getAllLabels()).thenReturn(asList(someLabel));

        Transaction t1 = Transaction.of(null, CASH_WDL, Description.of("blah WORD1 blah"), null, null);
        List<LabeledTransaction> labeledTransactions = labeler.label(asList(t1));

        assertThat(labeledTransactions.size(), equalTo(1));
//        assertThat(labeledTransactions.get(0).labels, equalTo(asList(withDrawalLabel)));
    }

}