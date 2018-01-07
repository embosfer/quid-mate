package com.embosfer.quidmate.gui.reports;

import com.embosfer.quidmate.db.DbConnection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.embosfer.quidmate.gui.GuiUtils.quidMateBorder;

public class ReportsTab extends Tab {

    public ReportsTab(DbConnection dbConnection, AllLabelsComboBox allLabelsComboBox, PeriodTrendComboBox durationComboBox) {

        HBox userControlsVBox = new HBox();
        userControlsVBox.getChildren().addAll(allLabelsComboBox, durationComboBox);

        BorderPane borderPaneLineChart = new BorderPane();
        borderPaneLineChart.setBorder(quidMateBorder());
        borderPaneLineChart.setTop(userControlsVBox);
        VBox.setVgrow(borderPaneLineChart, Priority.ALWAYS);

        setContent(borderPaneLineChart);

        allLabelsComboBox.setOnAction(drawLabelTrendLineChart(dbConnection, allLabelsComboBox, borderPaneLineChart));

        setText("Reports");
        setId("reportsTab");
    }

    private EventHandler<ActionEvent> drawLabelTrendLineChart(DbConnection dbConnection, AllLabelsComboBox allLabelsComboBox, BorderPane borderPaneLineChart) {
        return event -> {
            final NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Month");
            final NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Amount in £");
            borderPaneLineChart.setCenter(new LabelTrendLineChart(xAxis, yAxis,
                    dbConnection,
                    allLabelsComboBox.getSelectionModel().getSelectedItem()));
        };
    }
}
