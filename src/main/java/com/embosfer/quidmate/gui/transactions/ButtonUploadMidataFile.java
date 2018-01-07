package com.embosfer.quidmate.gui.transactions;

import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.PieChartDataCreator;
import com.embosfer.quidmate.core.TransactionLabeler;
import com.embosfer.quidmate.core.exceptions.UnknownFileFormatException;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DbConnection;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.List;

class ButtonUploadMidataFile extends Button {

    ButtonUploadMidataFile(MidataFileProvider midataFileProvider, MidataParser midataParser, TransactionLabeler transactionLabeler, PieChartDataCreator pieChartDataMuncher, DbConnection dbConnection, TransactionsTable transactionsTable, Label lblNoTransactionsLoaded, ExpensesPieChart pieChart) {
        super("Upload midata file");
        setId("btnUploadMidataFile");

        setOnAction(event ->
                midataFileProvider.getFile().ifPresent(file -> {
                    try {
                        List<Transaction> transactions = midataParser.parse(file); // TODO: do this on a separate thread
                        if (transactions.size() == 0) {
                            Alert alert = createInfoPopUp("0 transactions loaded", "You might have loaded the same file again...");
                            alert.showAndWait();
                            return;
                        }

                        List<LabeledTransaction> labeledTransactions = transactionLabeler.label(transactions);
                        dbConnection.store(labeledTransactions);
                        transactionsTable.add(labeledTransactions);
                        lblNoTransactionsLoaded.setText(labeledTransactions.size() + " transactions were loaded.");

                        pieChart.setData(pieChartDataMuncher.createExpensesPieChartDataFor(labeledTransactions));
                        pieChart.setPercentageCaption();

                    } catch (UnknownFileFormatException e) {
                        // TODO: show popup error
                    }
                })
        );
    }

    private Alert createInfoPopUp(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Blah");
        alert.setContentText(contentText);
        return alert;
    }


}
