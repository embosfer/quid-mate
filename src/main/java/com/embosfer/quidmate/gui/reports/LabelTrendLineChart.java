package com.embosfer.quidmate.gui.reports;

import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.db.DbConnection;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

public class LabelTrendLineChart extends LineChart<Number, Number> {

    LabelTrendLineChart(NumberAxis xAxis, NumberAxis yAxis, DbConnection dbConnection, Description labelDescription) {

        super(xAxis, yAxis);
        setTitle(labelDescription.value);

        XYChart.Series series = new XYChart.Series();
        series.setName("Expenses trend");

        LocalDate fromDate = LocalDate.of(2017, 1, 1);
        LocalDate toDate = LocalDate.of(2018, 1, 1);
        List<Transaction> transactionsWithinDate = dbConnection.getTransactionsMatching(labelDescription.value, fromDate, toDate);

        Map<Month, Double> amountsPerMonth = transactionsWithinDate.stream()
                .collect(groupingBy(transaction -> transaction.date.getMonth(), summingDouble(Transaction::getDebitCreditAsDouble)));

        ObservableList seriesData = series.getData();

        populate(seriesData, amountsPerMonth, Month.JANUARY);
        populate(seriesData, amountsPerMonth, Month.FEBRUARY);
        populate(seriesData, amountsPerMonth, Month.MARCH);
        populate(seriesData, amountsPerMonth, Month.APRIL);
        populate(seriesData, amountsPerMonth, Month.MAY);
        populate(seriesData, amountsPerMonth, Month.JUNE);
        populate(seriesData, amountsPerMonth, Month.JULY);
        populate(seriesData, amountsPerMonth, Month.AUGUST);
        populate(seriesData, amountsPerMonth, Month.SEPTEMBER);
        populate(seriesData, amountsPerMonth, Month.OCTOBER);
        populate(seriesData, amountsPerMonth, Month.NOVEMBER);
        populate(seriesData, amountsPerMonth, Month.DECEMBER);

        getData().add(series);
    }

    private void populate(ObservableList seriesData, Map<Month, Double> amountsPerMonth, Month month) {
        Double amount = amountsPerMonth.get(month);
        seriesData.add(new XYChart.Data(month.ordinal() + 1, amount == null ? 0 : Math.abs(amount)));
    }
}
