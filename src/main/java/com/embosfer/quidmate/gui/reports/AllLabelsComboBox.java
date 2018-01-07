package com.embosfer.quidmate.gui.reports;

import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.db.DbConnection;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import java.util.Optional;

import static java.util.stream.Collectors.toCollection;

public class AllLabelsComboBox extends ComboBox<Description>{

    public AllLabelsComboBox(DbConnection dbConnection) {

        super(dbConnection.getAllLabels(Optional.empty()).stream()
                .map(label -> label.description)
                .collect(toCollection(FXCollections::observableArrayList)));
    }
}
