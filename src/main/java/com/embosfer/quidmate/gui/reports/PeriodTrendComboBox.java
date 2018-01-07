package com.embosfer.quidmate.gui.reports;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import java.time.LocalDate;
import java.time.Period;

public class PeriodTrendComboBox extends ComboBox<Period> {

    public PeriodTrendComboBox() {

        super();

        LocalDate now = LocalDate.now();

        setItems(FXCollections.observableArrayList(
                Period.between(now, now.minusMonths(1)),
                Period.between(now, now.minusMonths(3)),
                Period.between(now, now.minusMonths(6)),
                Period.between(now, now.minusMonths(12))));
    }

}
