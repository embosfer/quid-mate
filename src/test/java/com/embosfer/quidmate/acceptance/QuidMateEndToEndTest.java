package com.embosfer.quidmate.acceptance;

import com.embosfer.quidmate.core.model.*;
import com.embosfer.quidmate.doubles.FakeDbConnection;
import com.embosfer.quidmate.support.QuidMateRunner;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.time.LocalDate;

import static com.embosfer.quidmate.core.model.TransactionType.CARD_PAYMENT;
import static com.embosfer.quidmate.core.model.TransactionType.DD;
import static com.embosfer.quidmate.support.MidataSupport.file;
import static java.util.Arrays.asList;

/**
 * Created by embosfer on 21/05/2017.
 */
public class QuidMateEndToEndTest extends ApplicationTest {


    FakeDbConnection db = new FakeDbConnection();
    QuidMateRunner gui = new QuidMateRunner(db);

    @Override
    public void start(Stage stage) throws Exception {
        gui.start(stage); // delegate
    }

    @Test
    public void uploadingMidataFileShowsLabeledTransactionsOnGUI() throws Exception {
        Label billsParentLabel = Label.of(1, Description.of("Bills"),null, "EDF", "E\\.ON");
        Label electricityLabel = Label.of(2, Description.of("Electricity"), billsParentLabel, "EDF");
        Label gasLabel = Label.of(3, Description.of("Gas"), billsParentLabel,"E.ON");
        db.has(billsParentLabel);
        db.has(electricityLabel);
        db.has(gasLabel);

        Transaction electricityTransaction =
                Transaction.of(LocalDate.of(2017, 5, 28), DD, Description.of("DIRECT DEBIT PAYMENT TO EDF ENERGY REF"), DebitCredit.of(-50.00), Balance.of(950.00));
        Transaction gasTransaction =
                Transaction.of(LocalDate.of(2017, 5, 27), CARD_PAYMENT, Description.of("CARD PAYMENT TO E.ON ENERGY SOLUTIONS"), DebitCredit.of(-30.00), Balance.of(1000));

        gui.loadsMidataFile(file("test_transactions.csv")
                .withHeader("Date;Type;Merchant/Description;Debit/Credit;Balance;")
                .withTransactions(electricityTransaction, gasTransaction));

        db.contains(asList(
                LabeledTransaction.of(electricityTransaction, asList(billsParentLabel, electricityLabel)),
                LabeledTransaction.of(gasTransaction, asList(billsParentLabel, gasLabel))));

        gui.showsLabeledTransaction(electricityTransaction, "Bills Electricity");
        gui.showsLabeledTransaction(gasTransaction, "Bills Gas");
        gui.showsTransactionsWereLoaded(2);
    }

    // TODO: test with a non expected file pops an error message

    // TODO: probably better to have a unit test for this
    @Ignore
    @Test
    public void mergesMidataTransactionsCorrectlyWithoutDuplicatingAnyOfThem() {

    }

    @Ignore
    @Test
    public void distinguishesCardPaymentsFromCashWithdrawals() {

    }

    @Ignore
    @Test
    public void canFilterTransactionsByMonth() {

    }

    @Ignore
    @Test
    public void canFilterTransactionsByYear() {

    }

    @Ignore
    @Test
    public void summarisesTransactionsByCategory() {

    }

    @After
    public void cleanUp() {
        gui.stop(); //TODO not sure yet if I need this
    }

}