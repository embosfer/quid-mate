package com.embosfer.quidmate.core;

import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
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
    public void calculatesPercentagesCorrectly() {
        Label billsLabel = Label.of(0, Description.of("Bills"), null, null);
        Label electricity = Label.of(0, Description.of("Electricity"), billsLabel, null);
        Label travelLabel = Label.of(0, Description.of("Travel"), null, null);

        LabeledTransaction t1 = LabeledTransaction.of(null, asList(billsLabel, electricity));
        LabeledTransaction t2 = LabeledTransaction.of(null, asList(travelLabel));
        LabeledTransaction t3 = LabeledTransaction.of(null, emptyList());

        List<LabeledTransaction> transactions = asList(t1, t2, t3);
        ObservableList<PieChart.Data> pieChartData = creator.createPieChartDataFor(transactions);

        assertThat(pieChartData.size(), equalTo(3));
        assertThat(pieChartData.get(0).getName(), equalTo("Travel"));
        assertThat(pieChartData.get(0).getPieValue(), equalTo(33.33));
        assertThat(pieChartData.get(1).getName(), equalTo("Unknown"));
        assertThat(pieChartData.get(1).getPieValue(), equalTo(33.33));
        assertThat(pieChartData.get(2).getName(), equalTo("Bills"));
        assertThat(pieChartData.get(2).getPieValue(), equalTo(33.33));
    }

}