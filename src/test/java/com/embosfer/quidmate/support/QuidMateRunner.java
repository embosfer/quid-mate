package com.embosfer.quidmate.support;

import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.PieChartDataCreator;
import com.embosfer.quidmate.core.TransactionLabeler;
import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DbConnection;
import com.embosfer.quidmate.gui.LabelsTab;
import com.embosfer.quidmate.gui.MainScene;
import com.embosfer.quidmate.gui.TransactionsTab;
import com.embosfer.quidmate.gui.TransactionsTable;
import javafx.scene.Group;
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
    public void start(Stage primaryStage) throws Exception {
        TransactionsTab transactionsTab = new TransactionsTab(new PieChartDataCreator(), new TransactionsTable(), midataFileProvider, new MidataParser(db), new TransactionLabeler(db), db);
        LabelsTab labelsTab = new LabelsTab(db);

        primaryStage.setScene(new MainScene(new Group(), transactionsTab, labelsTab));
        primaryStage.show();
    }


    public void loadsMidataFile(MidataSupport.MidataFile file) {
        when(this.midataFileProvider.getFile()).thenReturn(Optional.of(file.outputFile));
        clickOn("#btnUploadMidataFile").press(KeyCode.DOWN).press(KeyCode.ENTER);
    }

    public void showsLabeledTransaction(Transaction transaction, String leafLabel) {
        verifyThat("#loadedTransactions", containsRow(transaction.date, transaction.type, transaction.description, transaction.debitCredit, transaction.balance, leafLabel));
    }

    public void showsTransactionsWereLoaded(int noTransactions) {
        verifyThat("#lblNoTransactionsLoaded", LabeledMatchers.hasText(noTransactions + " transactions were loaded."));
    }

    public void showsPopupMessage(String message) {
//        verifyThat(fromAll()., typeSafeMatcher(Dialog.class, "", node -> node.getTitle().equals("")));
    }


    public void showsLabel(Label label) {
        verifyThat("#loadedLabels", containsRow(label.description, label.wordsToFind, label.parentLabel.map(lab -> lab.description).orElse(null)));
    }

    public void stop() {
//        driver.dispose();
    }

    public void goesToLabelTab() {
        clickOn("#labelsTab");
    }
}
