package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.PieChartDataCreator;
import com.embosfer.quidmate.core.TransactionLabeler;
import com.embosfer.quidmate.core.exceptions.UnknownFileFormatException;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DbConnection;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
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

    public TransactionsTab(PieChartDataCreator pieChartDataMuncher, TransactionsTable transactionsTable,
                           MidataFileProvider midataFileProvider, MidataParser midataParser,
                           TransactionLabeler transactionLabeler,
                           DbConnection dbConnection) {

        Button btnUploadMidataFile = new Button("Upload midata file");
        btnUploadMidataFile.setId("btnUploadMidataFile");
        Label lblNoTransactionsLoaded = new Label();
        lblNoTransactionsLoaded.setId("lblNoTransactionsLoaded");

        List<LabeledTransaction> lastTransactions = dbConnection.retrieveLastTransactions(20);// TODO separate thread...
        transactionsTable.add(lastTransactions);

        PieChart pieChart = new PieChart();
        pieChart.setLegendVisible(false);
        pieChart.setLabelLineLength(10);
        final javafx.scene.control.Label percentagesCaption = new javafx.scene.control.Label();
        percentagesCaption.setVisible(false);
        percentagesCaption.setTextFill(Color.DARKORANGE);
        percentagesCaption.setStyle("-fx-font: 24 arial;");

        btnUploadMidataFile.setOnAction(event ->
                midataFileProvider.getFile().ifPresent(file -> {
                    try {
                        List<Transaction> transactions = midataParser.parse(file); // TODO: do this on a separate thread
                        List<LabeledTransaction> labeledTransactions = transactionLabeler.label(transactions);
                        dbConnection.store(labeledTransactions);
                        transactionsTable.add(labeledTransactions);
                        lblNoTransactionsLoaded.setText(labeledTransactions.size() + " transactions were loaded.");

                        pieChart.setData(pieChartDataMuncher.createPieChartDataFor(labeledTransactions));
                        setPercentageCaptionOn(pieChart, percentagesCaption);

                    } catch (UnknownFileFormatException e) {
                        // TODO: show popup error
                    }
                })
        );

        HBox vBoxButtons = new HBox();
        vBoxButtons.setBorder(quidMateBorder());
        vBoxButtons.getChildren().addAll(btnUploadMidataFile);
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

    private void setPercentageCaptionOn(PieChart pieChart, Label percentagesCaption) {
        for (final PieChart.Data data : pieChart.getData()) {
            Node node = data.getNode();

            node.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, e -> {
                percentagesCaption.setText(data.getName() + ": " + data.getPieValue() + "%");
                percentagesCaption.setVisible(true);
            });

            node.addEventHandler(MouseEvent.MOUSE_EXITED, e -> percentagesCaption.setVisible(false));
        }

    }

}
