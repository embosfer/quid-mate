package com.embosfer.quidmate.integration;

import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.db.DefaultDbConnection;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.embosfer.quidmate.jooq.quidmate.Tables.TRANSACTION;
import static com.embosfer.quidmate.jooq.quidmate.Tables.TRANSACTIONLABELLIST;

/**
 * Created by embosfer on 27/07/2017.
 */
// TODO rename this to DbConnectionTestSupport?
public class DbTestSupport extends DefaultDbConnection {

    private DSLContext execute;
    private Connection checkerConnection;
    private List<LabeledTransaction> recordedTransactions;

    @Before
    public void setUp() throws SQLException {
        openDbConnections();
        recordedTransactions = new ArrayList<>();
    }

    @After
    public void tearDown() throws SQLException {
        removeTestRecordsFromDb();
        closeDbConnections();
        recordedTransactions.clear();
    }

    @Override
    public void store(List<LabeledTransaction> transactions) {
        super.store(transactions);
        this.recordedTransactions.addAll(transactions);
    }

    private void removeTestRecordsFromDb() {
        for (LabeledTransaction record : recordedTransactions) {
            int noDeletedTransactions = this.execute.delete(TRANSACTION)
                    .where(TRANSACTION.DATE.eq(record.transaction.date))
                    .and(TRANSACTION.DESCRIPTION.eq(record.transaction.description.value))
                    .and(TRANSACTION.BALANCE.eq(record.transaction.balance.value))
                    .and(TRANSACTION.DEBIT_CREDIT.eq(record.transaction.debitCredit.value))
                    .execute();
            System.out.println(noDeletedTransactions + " transactions deleted from DB.");
        }
    }

    private void openDbConnections() throws SQLException {
        this.open();

        // TODO refactor the below
        String user = "root";
        String password = "xxxxx";
        String url = "jdbc:mysql://localhost:3306/QuidMate";
        checkerConnection = DriverManager.getConnection(url, user, password);
        execute = DSL.using(checkerConnection);
    }

    private void closeDbConnections() throws SQLException {
        this.close();

        checkerConnection.close();
        execute.close();
    }

    protected void dbContains(LabeledTransaction lt) {
        Record transactionRecord = execute.select()
                .from(TRANSACTION)
                .where(TRANSACTION.DATE.eq(lt.transaction.date))
                .and(TRANSACTION.DESCRIPTION.eq(lt.transaction.description.value))
                .and(TRANSACTION.BALANCE.eq(lt.transaction.balance.value))
                .and(TRANSACTION.DEBIT_CREDIT.eq(lt.transaction.debitCredit.value))
                .fetchOne();
        if (transactionRecord == null) throw new IllegalStateException("Couldn't find LabeledTransaction " + lt);

        for (Label label : lt.labels) {
            Result<Record> labelResult = execute.select()
                    .from(TRANSACTIONLABELLIST)
                    .where(TRANSACTIONLABELLIST.TRANSACTION_ID.eq(transactionRecord.get(TRANSACTION.ID)))
                    .and(TRANSACTIONLABELLIST.LABEL_ID.eq(label.id))
                    .fetch();
            if (labelResult == null) throw new IllegalStateException("Couldn't find Label " + label);
        }
    }
}
