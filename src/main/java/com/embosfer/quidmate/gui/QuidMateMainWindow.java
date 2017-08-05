package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.TransactionLabeler;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DefaultDbConnection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by embosfer on 23/07/2017.
 */
public class QuidMateMainWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DefaultDbConnection dbConnection = new DefaultDbConnection();
        GuiMainPane guiMainPane = new GuiMainPane(new TransactionsTable(),
                new MidataFileProvider(primaryStage), new MidataParser(),
                new TransactionLabeler(dbConnection), dbConnection);

        Scene myScene = new Scene(guiMainPane);
        primaryStage.setScene(myScene);
        primaryStage.setWidth(1300);
        primaryStage.setHeight(1000);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
