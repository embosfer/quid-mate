package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.DbConfig;
import com.embosfer.quidmate.core.MidataFileProvider;
import com.embosfer.quidmate.core.PieChartDataCreator;
import com.embosfer.quidmate.core.TransactionLabeler;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.db.DefaultDbConnection;
import com.embosfer.quidmate.db.translator.LabelPatternTranslator;
import com.embosfer.quidmate.gui.reports.AllLabelsComboBox;
import com.embosfer.quidmate.gui.reports.PeriodTrendComboBox;
import com.embosfer.quidmate.gui.reports.ReportsTab;
import javafx.application.Application;
import javafx.scene.Group;
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

        TransactionsTab transactionsTab = new TransactionsTab(new PieChartDataCreator(), new TransactionsTable(),
                new MidataFileProvider(primaryStage), new MidataParser(dbConnection),
                new TransactionLabeler(dbConnection), dbConnection);
        LabelsTab labelsTab = new LabelsTab(dbConnection);
        ReportsTab reportsTab = new ReportsTab(dbConnection, new AllLabelsComboBox(dbConnection), new PeriodTrendComboBox());

        primaryStage.setScene(new MainScene(new Group(), transactionsTab, labelsTab, reportsTab));
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
