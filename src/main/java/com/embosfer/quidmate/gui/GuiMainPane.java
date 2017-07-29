package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.exceptions.UnknownFileFormatException;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DbConnection;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Created by embosfer on 23/07/2017.
 */
public class GuiMainPane extends StackPane {

    public GuiMainPane(Stage primaryStage, MidataParser midataParser, DbConnection dbConnection) {
        Button button = new Button("Upload midata file");
        button.setId("uploadMidataFileBtn");

        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Upload midata file");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    List<Transaction> transactions = midataParser.parse(file); // TODO: do this on a separate thread
                    System.out.println(transactions);
                    dbConnection.store(transactions);
                } catch (UnknownFileFormatException e) {
                    // TODO: show popup error
                }
            }
        });

        getChildren().add(button);
    }

}
