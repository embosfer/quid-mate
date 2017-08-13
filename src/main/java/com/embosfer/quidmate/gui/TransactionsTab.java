package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.TransactionLabeler;
import com.embosfer.quidmate.core.exceptions.UnknownFileFormatException;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DbConnection;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Created by embosfer on 23/07/2017.
 */
public class TransactionsTab extends Tab {

    public TransactionsTab(TransactionsTable transactionsTable,
                           MidataFileProvider midataFileProvider, MidataParser midataParser,
                           TransactionLabeler transactionLabeler,
                           DbConnection dbConnection) {

        Button btnUploadMidataFile = new Button("Upload midata file");
        btnUploadMidataFile.setId("btnUploadMidataFile");
        Label lblNoTransactionsLoaded = new Label();
        lblNoTransactionsLoaded.setId("lblNoTransactionsLoaded");

        List<LabeledTransaction> lastTransactions = dbConnection.retrieveLastTransactions(20);// TODO separate thread...
        transactionsTable.add(lastTransactions);

        btnUploadMidataFile.setOnAction(event -> {
            Optional<File> optionalFile = midataFileProvider.getFile();
            optionalFile.ifPresent(file -> {
                try {
                    List<Transaction> transactions = midataParser.parse(file); // TODO: do this on a separate thread
                    List<LabeledTransaction> labeledTransactions = transactionLabeler.label(transactions);
                    dbConnection.store(labeledTransactions);
                    transactionsTable.add(labeledTransactions);
                    lblNoTransactionsLoaded.setText(labeledTransactions.size() + " transactions were loaded.");
                } catch (UnknownFileFormatException e) {
                    // TODO: show popup error
                }
            });
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(btnUploadMidataFile, lblNoTransactionsLoaded, transactionsTable);
        setContent(vBox);
        setText("Transactions");
    }

}
