package com.embosfer.quidmate.core;

import com.embosfer.quidmate.core.model.*;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by embosfer on 17/08/2017.
 */
public class PieChartDataCreatorTest {

    PieChartDataCreator creator = new PieChartDataCreator();

    @Test
    public void calculatesExpensePercentagesCorrectlyUsingDebitCreditValues() {
        Label billsLabel = Label.of(0, Description.of("Bills"), null, null);
        Label electricity = Label.of(0, Description.of("Electricity"), billsLabel, null);
        Label travelLabel = Label.of(0, Description.of("Travel"), null, null);

        LabeledTransaction t1 = LabeledTransaction.of(
                Transaction.of(null, null, null, DebitCredit.of(-50.0), null),
                asList(billsLabel, electricity));
        LabeledTransaction t2 = LabeledTransaction.of(
                Transaction.of(null, null, null, DebitCredit.of(-350.0), null),
                asList(travelLabel));
        LabeledTransaction t3 = LabeledTransaction.of(
                Transaction.of(null, null, null, DebitCredit.of(-100.0), null),
                emptyList());
        LabeledTransaction t4 = LabeledTransaction.of(
                Transaction.of(null, null, null, DebitCredit.of(1000.0), null),
                emptyList());

        List<LabeledTransaction> transactions = asList(t1, t2, t3, t4);
        ObservableList<PieChart.Data> pieChartData = creator.createExpensesPieChartDataFor(transactions);
        System.out.println(pieChartData);

        assertThat(pieChartData.size(), equalTo(3));
        assertThat(pieChartData.get(0).getName(), equalTo("Travel"));
        assertThat(pieChartData.get(0).getPieValue(), equalTo(70.00));
        assertThat(pieChartData.get(1).getName(), equalTo("Unknown"));
        assertThat(pieChartData.get(1).getPieValue(), equalTo(20.00));
        assertThat(pieChartData.get(2).getName(), equalTo("Bills"));
        assertThat(pieChartData.get(2).getPieValue(), equalTo(10.00));
    }

}