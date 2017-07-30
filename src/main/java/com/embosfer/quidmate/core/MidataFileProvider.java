package com.embosfer.quidmate.core;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

/**
 * Created by embosfer on 29/07/2017.
 */
public class MidataFileProvider {

    private final FileChooser fileChooser = new FileChooser();
    private final Stage stage;

    public MidataFileProvider(Stage stage) {
        this.stage = stage;
        this.fileChooser.setTitle("Upload midata file");
    }

    public Optional<File> getFile() {
        File file = fileChooser.showOpenDialog(stage);
        return Optional.ofNullable(file);
    }
}
