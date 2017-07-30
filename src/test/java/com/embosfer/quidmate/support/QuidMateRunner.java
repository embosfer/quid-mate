package com.embosfer.quidmate.support;

import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DbConnection;
import com.embosfer.quidmate.gui.GuiMainPane;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        GuiMainPane guiMainPane = new GuiMainPane(midataFileProvider, new MidataParser(), db);

        stage.setScene(new Scene(guiMainPane, 500, 500));
        stage.show();
    }


    public void loadsMidataFile(MidataSupport.MidataFile file) {
        when(this.midataFileProvider.getFile()).thenReturn(Optional.of(file.outputFile));
        clickOn("#uploadMidataFileBtn").press(KeyCode.DOWN).press(KeyCode.ENTER);
    }

    public void shows(Transaction[] transactions) {
        for (Transaction transaction : transactions) {
//            driver.shows(transaction);


        }
    }

    public void showsTransactionsWereLoaded(int noTransactions) {
    }

    public void showsTotalExpenses(double totalExpenses) {
    }

    public void stop() {
//        driver.dispose();
    }
}
