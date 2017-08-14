package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.db.DefaultDbConnection;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static com.embosfer.quidmate.gui.GuiUtils.quidMateBorder;

/**
 * Created by embosfer on 13/08/2017.
 */
public class LabelsTab extends Tab {

    public LabelsTab(DefaultDbConnection dbConnection) {
        Button btnCreateLabel = new Button("Create Label");
        btnCreateLabel.setId("#btnCreateLabel");

        btnCreateLabel.setOnAction(event -> {

        });

        VBox vBoxButtons = new VBox();
        vBoxButtons.setBorder(quidMateBorder());
        vBoxButtons.getChildren().add(btnCreateLabel);

        BorderPane borderPaneLabelTree = new BorderPane();
        borderPaneLabelTree.setBorder(quidMateBorder());
        LabelsTreeTableView labelsTreeTableView = new LabelsTreeTableView(dbConnection.getAllLabels());
        borderPaneLabelTree.setCenter(labelsTreeTableView);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(vBoxButtons, borderPaneLabelTree);
        setContent(hBox);
        setText("Labels");
    }
}
