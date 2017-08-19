package com.embosfer.quidmate.core;

import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by embosfer on 16/08/2017.
 */
// TODO rename?
public class PieChartDataCreator {

    private final DecimalFormat df;

    public PieChartDataCreator() {
        df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
    }


    public ObservableList<PieChart.Data> createPieChartDataFor(List<LabeledTransaction> labeledTransactions) {

        Map<String, Double> countByLabel = new HashMap<>(); // TODO review if I can do this a bit more clean
        for (LabeledTransaction labeledTransaction : labeledTransactions) {
            if (labeledTransaction.labels.isEmpty()) {
                countByLabel.compute("Unknown", (k, v) -> v == null ? 1 : v + 1);
            } else {
                for (Label label : labeledTransaction.labels) {
                    if (!label.parentLabel.isPresent()) {
                        countByLabel.compute(label.description.value, (k, v) -> v == null ? 1 : v + 1);
                    }
                }
            }
        }

        return countByLabel.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey(), formatToTwoDecimalPlaces((entry.getValue() * 100) / labeledTransactions.size())))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private double formatToTwoDecimalPlaces(double value) {
        String val = String.format("%.2f", value);
        return Double.parseDouble(val);
    }
}
