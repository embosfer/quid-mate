package com.embosfer.quidmate.support;

import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DbConnection;
import com.embosfer.quidmate.gui.GuiMainPane;
import com.embosfer.quidmate.gui.TransactionsTable;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.containsRow;

/**
 * Created by embosfer on 28/05/2017.
 */
public class QuidMateRunner extends ApplicationTest {

    private final DbConnection db;
    private final MidataFileProvider midataFileProvider = mock(MidataFileProvider.class);

    public QuidMateRunner(DbConnection db) {
        this.db = db;
    }

    @Override
    public void start(Stage stage) throws Exception {
        GuiMainPane guiMainPane = new GuiMainPane(new TransactionsTable(), midataFileProvider, new MidataParser(), db);

        stage.setScene(new Scene(guiMainPane, 500, 500));
        stage.show();
    }


    public void loadsMidataFile(MidataSupport.MidataFile file) {
        when(this.midataFileProvider.getFile()).thenReturn(Optional.of(file.outputFile));
        clickOn("#uploadMidataFileBtn").press(KeyCode.DOWN).press(KeyCode.ENTER);
    }

    public void shows(Transaction[] transactions) {
        for (Transaction transaction : transactions) {
            verifyThat("#LoadedTransactions", containsRow(transaction.date, transaction.type, transaction.description, transaction.debitCredit, transaction.balance));
        }
    }

    public void showsTransactionsWereLoaded(int noTransactions) {
        verifyThat("#numberOfTransactionsLoaded", LabeledMatchers.hasText(noTransactions + " transactions were loaded."));
    }



    public void showsTotalExpenses(double totalExpenses) {
    }

    public void stop() {
//        driver.dispose();
    }
}
