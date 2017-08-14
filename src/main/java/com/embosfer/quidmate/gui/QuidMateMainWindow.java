package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.DbConfig;
import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.TransactionLabeler;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DefaultDbConnection;
import com.embosfer.quidmate.db.translator.LabelPatternTranslator;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by embosfer on 23/07/2017.
 */
public class QuidMateMainWindow extends Application {

    private DefaultDbConnection dbConnection;

    @Override
    public void start(Stage primaryStage) throws Exception {
        dbConnection = new DefaultDbConnection(new DbConfig(), new LabelPatternTranslator());
        dbConnection.open();

        BorderPane mainPane = new BorderPane();
        TabPane tabPane = new TabPane();
        mainPane.setCenter(tabPane);

        TransactionsTab transactionsTab = new TransactionsTab(new TransactionsTable(),
                new MidataFileProvider(primaryStage), new MidataParser(),
                new TransactionLabeler(dbConnection), dbConnection);
        LabelsTab labelsTab = new LabelsTab(dbConnection);
        tabPane.getTabs().addAll(transactionsTab, labelsTab);

        Group root = new Group();
        Scene scene = new Scene(root, 1700, 1000, Color.WHITE);
        mainPane.prefHeightProperty().bind(scene.heightProperty());
        mainPane.prefWidthProperty().bind(scene.widthProperty());
        root.getChildren().add(mainPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        dbConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
