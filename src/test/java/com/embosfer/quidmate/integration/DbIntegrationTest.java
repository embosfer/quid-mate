package com.embosfer.quidmate.integration;

import com.embosfer.quidmate.core.model.*;
import com.embosfer.quidmate.db.translator.LabelPatternTranslator;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static com.embosfer.quidmate.core.model.TransactionType.CARD_PAYMENT;
import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by embosfer on 28/05/2017.
 */
public class DbIntegrationTest {

    private final DbConnectionTestSupport dbConnection = new DbConnectionTestSupport(new LabelPatternTranslator());

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
    public void retrievesAllAvailableLabels() {

//        List<Label> allLabels = getAllLabels();

    }

}
