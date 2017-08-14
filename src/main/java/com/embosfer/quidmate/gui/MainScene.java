package com.embosfer.quidmate.gui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

/**
 * Created by embosfer on 14/08/2017.
 */
public class MainScene extends Scene {

    public MainScene(Group root, Tab... tabs) {
        super(root,1700, 1000, Color.WHITE);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tabs);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(tabPane);
        mainPane.prefHeightProperty().bind(this.heightProperty());
        mainPane.prefWidthProperty().bind(this.widthProperty());

        root.getChildren().add(mainPane);
    }
}
