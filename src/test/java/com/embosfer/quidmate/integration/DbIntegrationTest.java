package com.embosfer.quidmate.integration;

import com.embosfer.quidmate.core.DbConfig;
import com.embosfer.quidmate.core.model.*;
import com.embosfer.quidmate.db.translator.LabelPatternTranslator;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.embosfer.quidmate.core.model.TransactionType.*;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by embosfer on 28/05/2017.
 */
public class DbIntegrationTest {

    private final DbConnectionTestSupport dbConnection = new DbConnectionTestSupport(new DbConfig(), new LabelPatternTranslator());

    @Before
    public void setUp() throws SQLException {
        dbConnection.setUp();
    }

    @After
    public void tearDown() throws SQLException {
        dbConnection.tearDown();
    }

    @Test
    public void containsAllRequiredTransactionTypes() {

        TransactionType[] dbTransactionTypes = dbConnection.getAllTransactionTypes()
                                                    .toArray(new TransactionType[TransactionType.values().length]);
        assertThat(dbTransactionTypes, equalTo(TransactionType.values()));
    }

    @Test
    public void canStoreALabeledTransaction() {
        Transaction transaction = Transaction.of(LocalDate.of(2017, 7, 27),
                CARD_PAYMENT,
                Description.of("TEST CARD PAYMENT TO SKY"),
                DebitCredit.of(-30.00),
                Balance.of(970.00));

        Label billsLabel = Label.of(1, null, null, "SKY", "EDF");
        Label internetLabel = Label.of(2, null, billsLabel, "SKY");
        LabeledTransaction labeledTransaction = LabeledTransaction.of(transaction,
                                                                        asList(billsLabel, internetLabel));

        dbConnection.store(asList(labeledTransaction));

        dbConnection.dbContains(labeledTransaction);
    }

    @Ignore
    @Test
    public void canRetrieveLastTransactions() {
        // TODO make sure required Labels are created by DbConnectionTestSupport

        LocalDate now = now();
        Transaction transaction1 = Transaction.of(now, CARD_PAYMENT, Description.of("t1"), DebitCredit.of(-1), Balance.of(1));
        Label labelT1 = Label.of(1, null, null, "lt1");
        LabeledTransaction labTransaction1 = LabeledTransaction.of(transaction1, asList(labelT1));

        Transaction transaction2 = Transaction.of(now.minusDays(1), PAYMENTS, Description.of("t2"), DebitCredit.of(2), Balance.of(3));
        Label labelT2 = Label.of(2, null, null, "lt2");
        LabeledTransaction labTransaction2 = LabeledTransaction.of(transaction2, asList(labelT2));

        Transaction transaction3 = Transaction.of(now.minusDays(2), FAST_PAYMENT, Description.of("t3"), DebitCredit.of(3), Balance.of(6));
        Label labelT3 = Label.of(3, null, null, "lt3");
        LabeledTransaction labTransaction3 = LabeledTransaction.of(transaction3, asList(labelT3));

        dbConnection.store(asList(labTransaction1, labTransaction2, labTransaction3));

        List<LabeledTransaction> lastTransactions = dbConnection.retrieveLastTransactions(2);
        assertThat(lastTransactions.size(), equalTo(2));
        assertThat(lastTransactions.get(0), equalTo(labTransaction1));
        assertThat(lastTransactions.get(1), equalTo(labTransaction2));
    }

    @Ignore
    @Test
    public void retrievesAllAvailableLabels() {
        List<Label> allLabels = dbConnection.getAllLabels();

    }

}
