package com.embosfer.quidmate.support;

import com.embosfer.quidmate.Main;
import com.embosfer.quidmate.core.model.Transaction;
import com.embosfer.quidmate.core.parser.MidataParser;
import com.embosfer.quidmate.gui.GuiMainPane;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Created by embosfer on 28/05/2017.
 */
public class QuidMateRunner extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        GuiMainPane guiMainPane = new GuiMainPane(stage, new MidataParser());

        stage.setScene(new Scene(guiMainPane, 500, 500));
        stage.show();
    }

    public void loadsMidataFile(MidataSupport.MidataFile file) {
        // TODO for now file not used => should we use the name to be sure we get the right file?
        clickOn(".button").press(KeyCode.DOWN).press(KeyCode.ENTER);
    }

    public void shows(Transaction[] transactions) {
        for (Transaction transaction : transactions) {
//            driver.shows(transaction);


        }
    }

    public void showsTransactionsWereLoaded(int noTransactions) {
    }

    public void showsTotalExpenses(double totalExpenses) {
    }

    public void starts() {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
//        driver = new QuidMateJFrameDriver(1000);
    }

    public void stop() {
//        driver.dispose();
    }
}