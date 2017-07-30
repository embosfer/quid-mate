package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.exceptions.UnknownFileFormatException;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DbConnection;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Created by embosfer on 23/07/2017.
 */
public class GuiMainPane extends StackPane {

    public GuiMainPane(MidataFileProvider midataFileProvider, MidataParser midataParser, DbConnection dbConnection) {
        Button button = new Button("Upload midata file");
        button.setId("uploadMidataFileBtn");

        button.setOnAction(event -> {
            Optional<File> optionalFile = midataFileProvider.getFile();
            optionalFile.ifPresent(file -> {
                try {
                    List<Transaction> transactions = midataParser.parse(file); // TODO: do this on a separate thread
                    System.out.println(transactions);
                    dbConnection.store(transactions);
                } catch (UnknownFileFormatException e) {
                    // TODO: show popup error
                }
            });
        });

        getChildren().add(button);
    }

}
