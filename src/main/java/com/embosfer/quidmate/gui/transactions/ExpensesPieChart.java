package com.embosfer.quidmate.gui.transactions;

import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

class ExpensesPieChart extends PieChart {

    private final Label percentagesCaption;

    ExpensesPieChart(Label percentagesCaption) {
        this.percentagesCaption = percentagesCaption;
        setLegendVisible(false);
        setLabelLineLength(30);
    }

    public void setPercentageCaption() {
        for (final PieChart.Data data : getData()) {
            Node node = data.getNode();

            node.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, e -> {
                percentagesCaption.setText(data.getName() + ": " + data.getPieValue() + "%");
                percentagesCaption.setVisible(true);
            });

            node.addEventHandler(MouseEvent.MOUSE_EXITED, e -> percentagesCaption.setVisible(false));
        }

    }
}
