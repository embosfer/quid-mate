package com.embosfer.quidmate.acceptance;

import com.embosfer.quidmate.core.model.Balance;
import com.embosfer.quidmate.core.model.DebitCredit;
import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.doubles.FakeDbConnection;
import com.embosfer.quidmate.support.QuidMateRunner;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.sql.SQLException;
import java.time.LocalDate;

import static com.embosfer.quidmate.core.model.TransactionType.CARD_PAYMENT;
import static com.embosfer.quidmate.core.model.TransactionType.CASH_WDL;
import static com.embosfer.quidmate.support.MidataSupport.file;

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
    public void uploadingMidataFileShowsTransactionsOnGUIAndTotalExpenses() throws SQLException {

        Transaction[] transactions = {
                Transaction.of(LocalDate.of(2017, 5, 28), CARD_PAYMENT, Description.of("CARD PAYMENT TO X"), DebitCredit.of(-2.00), Balance.of(998.00)),
                Transaction.of(LocalDate.of(2017, 5, 27), CASH_WDL, Description.of("CASH WITHDRAWAL AT Y"), DebitCredit.of(-30.00), Balance.of(1000))
        };

        gui.loadsMidataFile(file("test_transactions.csv")
                .withHeader("Date;Type;Merchant/Description;Debit/Credit;Balance;")
                .withTransactions(transactions));

        db.contains(transactions);

        gui.showsTransactionsWereLoaded(2);
        gui.showsTotalExpenses(3.00); // TODO review decimal points
        gui.shows(transactions);
    }

    // TODO: test with a non expected file pops an error message

    @Ignore
    @Test
    public void uploadingMidataFileSupportsAllTypesOfTransactions() {

    }

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
        db.stop();
    }

}