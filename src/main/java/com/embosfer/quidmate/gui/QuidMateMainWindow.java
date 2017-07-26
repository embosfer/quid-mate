package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.parser.MidataParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by embosfer on 23/07/2017.
 */
public class QuidMateMainWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GuiMainPane guiMainPane = new GuiMainPane(primaryStage, new MidataParser());

        Scene myScene = new Scene(guiMainPane);
        primaryStage.setScene(myScene);
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
