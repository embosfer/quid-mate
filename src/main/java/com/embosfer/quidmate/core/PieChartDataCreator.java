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

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toCollection;

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


    public ObservableList<PieChart.Data> createExpensesPieChartDataFor(List<LabeledTransaction> labeledTransactions) {

        Map<String, Double> totalAmountsByLabel = new HashMap<>(); // TODO review if I can do this via streams (group by label)
        for (LabeledTransaction labeledTransaction : labeledTransactions) {

            double debitCredit = labeledTransaction.getDebitCredit().value;
            if (debitCredit > 0) continue;

            String labelDesc = "Unknown";
            if (!labeledTransaction.labels.isEmpty()) {
                for (Label label : labeledTransaction.labels) {
                    if (!label.parentLabel.isPresent()) { // calculate only the first level labels
                        labelDesc = label.description.value;
                        break;
                    }
                }
            }
            totalAmountsByLabel.compute(labelDesc, (k, v) -> v == null ? debitCredit : v + debitCredit);
        }

        double totalAmount = labeledTransactions.stream()
                .filter(tran -> tran.getDebitCredit().value < 0)
                .mapToDouble(tran -> tran.getDebitCredit().value)
                .sum();

        return totalAmountsByLabel.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey(), abs(formatToTwoDecimalPlaces((entry.getValue() * 100) / totalAmount))))
                .collect(toCollection(FXCollections::observableArrayList));
    }

    private double formatToTwoDecimalPlaces(double value) {
        String val = String.format("%.2f", value);
        return Double.parseDouble(val);
    }
}
