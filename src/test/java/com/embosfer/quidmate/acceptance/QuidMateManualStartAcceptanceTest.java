package com.embosfer.quidmate.acceptance;

import com.embosfer.quidmate.core.model.Balance;
import com.embosfer.quidmate.core.model.DebitCredit;
import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.doubles.FakeDbConnection;
import com.embosfer.quidmate.support.QuidMateRunner;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.time.LocalDate;

import static com.embosfer.quidmate.core.model.TransactionType.DD;
import static com.embosfer.quidmate.core.model.TransactionType.INTEREST;

/**
 * Created by embosfer on 21/05/2017.
 */
public class QuidMateManualStartAcceptanceTest extends ApplicationTest {

    FakeDbConnection db = new FakeDbConnection();
    QuidMateRunner gui = new QuidMateRunner(db);
    private Transaction transaction1;
    private Transaction transaction2;

    @Override
    public void init() throws Exception {

        LocalDate now = LocalDate.now();
        transaction1 = Transaction.of(now, DD, Description.of("Desc1"), DebitCredit.of(-50.00), Balance.of(950.00));
        transaction2 = Transaction.of(now, INTEREST, Description.of("Desc2"), DebitCredit.of(20.00), Balance.of(1000));

        db.has(transaction1, "label1", "label2");
        db.has(transaction2);
    }

    @Override
    public void start(Stage stage) throws Exception {
        gui.start(stage); // delegate
    }

    @Test
    public void reloadsTransactionsUponStart() {
        gui.showsLabeledTransaction(transaction1, "label1 label2");
        gui.showsLabeledTransaction(transaction2, "");
    }

    @After
    public void cleanUp() {
        gui.stop(); //TODO not sure yet if I need this
    }

}