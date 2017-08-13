package com.embosfer.quidmate.support;

import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.TransactionLabeler;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DbConnection;
import com.embosfer.quidmate.gui.TransactionsTab;
import com.embosfer.quidmate.gui.TransactionsTable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
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
        // TODO refactor this so we can test tabs individually
        BorderPane mainPane = new BorderPane();
        TabPane tabPane = new TabPane();
        mainPane.setCenter(tabPane);

        TransactionsTab transactionsTab = new TransactionsTab(new TransactionsTable(), midataFileProvider, new MidataParser(), new TransactionLabeler(db), db);
        tabPane.getTabs().add(transactionsTab);

        Group root = new Group();
        Scene scene = new Scene(root, 1300, 1000, Color.WHITE);
        mainPane.prefHeightProperty().bind(scene.heightProperty());
        mainPane.prefWidthProperty().bind(scene.widthProperty());
        root.getChildren().add(mainPane);

        stage.setScene(scene);
        stage.show();
    }


    public void loadsMidataFile(MidataSupport.MidataFile file) {
        when(this.midataFileProvider.getFile()).thenReturn(Optional.of(file.outputFile));
        clickOn("#btnUploadMidataFile").press(KeyCode.DOWN).press(KeyCode.ENTER);
    }

    public void showsLabeledTransaction(Transaction transaction, String labels) {
        verifyThat("#LoadedTransactions", containsRow(transaction.date, transaction.type, transaction.description, transaction.debitCredit, transaction.balance, labels));
    }

    public void showsTransactionsWereLoaded(int noTransactions) {
        verifyThat("#lblNoTransactionsLoaded", LabeledMatchers.hasText(noTransactions + " transactions were loaded."));
    }

    public void showsTotalExpenses(double totalExpenses) {
    }

    public void stop() {
//        driver.dispose();
    }

}
