package com.embosfer.quidmate.integration;

import com.embosfer.quidmate.core.DbConfig;
import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.db.DefaultDbConnection;
import com.embosfer.quidmate.db.translator.LabelPatternTranslator;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.embosfer.quidmate.jooq.quidmate.Tables.*;

/**
 * Created by embosfer on 27/07/2017.
 */
public class DbConnectionTestSupport extends DefaultDbConnection {

    private final DbConfig dbConfig;
    private DSLContext execute;
    private Connection checkerConnection;
    private List<LabeledTransaction> recordedTransactions;
    private List<Label> recordedLabels;

    public DbConnectionTestSupport(DbConfig dbConfig, LabelPatternTranslator labelPatternTranslator) {
        super(dbConfig, labelPatternTranslator);
        this.dbConfig = dbConfig;
    }

    public void setUp() throws SQLException {
        openDbConnections();
        recordedTransactions = new ArrayList<>();
        recordedLabels = new ArrayList<>();
    }

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
            System.out.println(noDeletedTransactions + " transactions (with associated labels) deleted from DB.");
        }
        for (Label record : recordedLabels) {
            int noDeletedTransactions = this.execute.delete(TRANSACTIONLABEL)
                    .where(TRANSACTIONLABEL.ID.eq(record.id))
                    .execute();
            System.out.println(noDeletedTransactions + " labels deleted from DB.");
        }
    }

    private void openDbConnections() throws SQLException {
        this.open();

        checkerConnection = DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password);
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

        Optional<Label> currentLabel = lt.leafLabel;
        while (currentLabel.isPresent()) {
            System.out.println("Checking for existence of label " + currentLabel);
            Result<Record> labelResult = execute.select()
                    .from(TRANSACTIONLABELLIST)
                    .where(TRANSACTIONLABELLIST.TRANSACTION_ID.eq(transactionRecord.get(TRANSACTION.ID)))
                    .and(TRANSACTIONLABELLIST.LABEL_ID.eq(currentLabel.get().id))
                    .fetch();
            if (labelResult == null) throw new IllegalStateException("Couldn't find Label " + currentLabel.get());
            currentLabel = currentLabel.get().parentLabel;
        }
    }

    public void has(Label... labels) {
        for (Label label : labels) {
            super.store(label);
            this.recordedLabels.add(label);
        }
    }
}
