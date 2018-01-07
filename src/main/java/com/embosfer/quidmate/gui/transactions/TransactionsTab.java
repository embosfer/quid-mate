package com.embosfer.quidmate.gui.transactions;

import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.PieChartDataCreator;
import com.embosfer.quidmate.core.TransactionLabeler;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DbConnection;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

import static com.embosfer.quidmate.gui.GuiUtils.quidMateBorder;

/**
 * Created by embosfer on 23/07/2017.
 */
public class TransactionsTab extends Tab {

    public TransactionsTab(PieChartDataCreator pieChartDataCreator, TransactionsTable transactionsTable,
                           MidataFileProvider midataFileProvider, MidataParser midataParser,
                           TransactionLabeler transactionLabeler,
                           DbConnection dbConnection) {

        Label lblNoTransactionsLoaded = new Label();
        lblNoTransactionsLoaded.setId("lblNoTransactionsLoaded");

        List<LabeledTransaction> lastTransactions = dbConnection.retrieveLastTransactions(20);// TODO separate thread...
        transactionsTable.add(lastTransactions);

        final javafx.scene.control.Label percentagesCaption = new javafx.scene.control.Label();
        percentagesCaption.setVisible(false);
        percentagesCaption.setTextFill(Color.DARKORANGE);
        percentagesCaption.setStyle("-fx-font: 24 arial;");

        ExpensesPieChart pieChart = new ExpensesPieChart(percentagesCaption);

        HBox vBoxButtons = new HBox();
        vBoxButtons.setBorder(quidMateBorder());
        vBoxButtons.getChildren().addAll(new ButtonUploadMidataFile(
                midataFileProvider, midataParser, transactionLabeler, pieChartDataCreator, dbConnection, transactionsTable, lblNoTransactionsLoaded, pieChart));
        HBox.setHgrow(vBoxButtons, Priority.ALWAYS);

        BorderPane borderPaneTransactions = new BorderPane();
        borderPaneTransactions.setBorder(quidMateBorder());
        borderPaneTransactions.setTop(lblNoTransactionsLoaded);
        borderPaneTransactions.setCenter(transactionsTable);
        VBox.setVgrow(borderPaneTransactions, Priority.ALWAYS);

        Group pieChartGroup = new Group();
        VBox box = new VBox();
        pieChartGroup.getChildren().add(box);
        box.getChildren().addAll(pieChart, percentagesCaption);

        VBox vBoxPieAndTransactions = new VBox();
        vBoxPieAndTransactions.setBorder(quidMateBorder());
        vBoxPieAndTransactions.getChildren().addAll(pieChartGroup, borderPaneTransactions);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(vBoxButtons, vBoxPieAndTransactions);
        setContent(hBox);

        setText("Transactions");
    }

}
