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
import java.util.Optional;

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
    public void canStoreALabeledTransactionWithOneSingleLabelHierarchy() {
        Transaction transaction = Transaction.of(LocalDate.of(2017, 7, 27),
                CARD_PAYMENT,
                Description.of("SIMPLELABELTRANSACTION"),
                DebitCredit.of(3.00),
                Balance.of(100.00));

        Label oneLabel = Label.of(1, null, null, "SIMPLELABELTRANSACTION");
        LabeledTransaction labeledTransaction = LabeledTransaction.of(transaction, Optional.of(oneLabel));

        dbConnection.store(asList(labeledTransaction));

        dbConnection.dbContains(labeledTransaction);
    }

    @Test
    public void canStoreALabeledTransactionWithMultipleLabelHierarchy() {
        Transaction transaction = Transaction.of(LocalDate.of(2017, 7, 27),
                CARD_PAYMENT,
                Description.of("HIERARCHYLABELTRANSACTION"),
                DebitCredit.of(-30.00),
                Balance.of(970.00));

        Label billsLabel = Label.of(1, null, null, null);
        Label internetLabel = Label.of(2, null, billsLabel, "HIERARCHYLABELTRANSACTION");
        LabeledTransaction labeledTransaction = LabeledTransaction.of(transaction, Optional.of(internetLabel));

        dbConnection.store(asList(labeledTransaction));

        dbConnection.dbContains(labeledTransaction);
    }

    @Test
    public void canRetrieveLastTransactions() {
        Label labelWithNoChildren = Label.of(1001, Description.of("label1"), null, "w1");
        Label parentLabel = Label.of(1002, Description.of("label2"), null, new String[]{});
        Label leafLabel = Label.of(1003, Description.of("label3"), parentLabel, "w3");
        dbConnection.has(labelWithNoChildren, parentLabel, leafLabel);

        LocalDate now = now();
        Transaction transaction1 = Transaction.of(now, CARD_PAYMENT, Description.of("w1"), DebitCredit.of(-1), Balance.of(1));
        LabeledTransaction labTransaction1 = LabeledTransaction.of(transaction1, Optional.of(labelWithNoChildren));

        Transaction transaction2 = Transaction.of(now.minusDays(1), PAYMENTS, Description.of("not_labeled"), DebitCredit.of(2), Balance.of(3));
        LabeledTransaction labTransaction2 = LabeledTransaction.of(transaction2, Optional.empty());

        Transaction transaction3 = Transaction.of(now.minusDays(2), FAST_PAYMENT, Description.of("w3"), DebitCredit.of(3), Balance.of(6));
        LabeledTransaction labTransaction3 = LabeledTransaction.of(transaction3, Optional.of(leafLabel));

        Transaction transactionThatWillBeFiltered = Transaction.of(now.minusDays(3), FAST_PAYMENT, Description.of("Filtered transactino"), DebitCredit.of(4), Balance.of(12));
        LabeledTransaction labTransactionThatWillBeFiltered = LabeledTransaction.of(transactionThatWillBeFiltered, Optional.empty());

        dbConnection.store(asList(labTransaction1, labTransaction2, labTransaction3, labTransactionThatWillBeFiltered));

        List<LabeledTransaction> lastTransactions = dbConnection.retrieveLastTransactions(3);
        assertThat(lastTransactions.size(), equalTo(3));
        assertThat(lastTransactions.get(0), equalTo(labTransaction1));
        assertThat(lastTransactions.get(1), equalTo(labTransaction2));
        assertThat(lastTransactions.get(2), equalTo(labTransaction3));
    }

    @Ignore
    @Test
    public void retrievesAllAvailableLabels() {

    }

}
