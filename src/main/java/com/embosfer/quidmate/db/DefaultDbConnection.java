package com.embosfer.quidmate.db;

import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.TransactionType;
import com.embosfer.quidmate.jooq.quidmate.Tables;
import com.embosfer.quidmate.jooq.quidmate.tables.records.TransactionRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.embosfer.quidmate.jooq.quidmate.Tables.TRANSACTIONLABELLIST;
import static com.embosfer.quidmate.jooq.quidmate.tables.Transactiontype.TRANSACTIONTYPE;
import static java.util.stream.Collectors.toList;

/**
 * Created by embosfer on 27/07/2017.
 */
public class DefaultDbConnection implements DbConnection {

    private static final Logger log = LoggerFactory.getLogger(DefaultDbConnection.class);

    private Connection connection;
    private DSLContext execute;

    // TODO make it configurable
    @Override
    public void open() throws SQLException {
        String userName = "root";
        String password = "xxxxx";
        String url = "jdbc:mysql://localhost:3306/QuidMate";
        connection = DriverManager.getConnection(url, userName, password);
        execute = DSL.using(connection);
    }

    @Override
    public void close() throws SQLException {
        connection.close();
        execute.close();
    }

    @Override
    public void store(List<LabeledTransaction> transactions) {

        for (LabeledTransaction lt : transactions) {
            TransactionRecord transactionRecord = execute.insertInto(Tables.TRANSACTION)
                    .set(Tables.TRANSACTION.DATE, lt.transaction.date)
                    .set(Tables.TRANSACTION.TYPE_ID, lt.transaction.type.id)
                    .set(Tables.TRANSACTION.DESCRIPTION, lt.transaction.description.value)
                    .set(Tables.TRANSACTION.DEBIT_CREDIT, lt.transaction.debitCredit.value)
                    .set(Tables.TRANSACTION.BALANCE, lt.transaction.balance.value)
                    .returning(Tables.TRANSACTION.ID)
                    .fetchOne();
            log.debug("Transaction ID {} stored.", transactionRecord.getId());

            for (Label label: lt.labels) {
                int noLabels = this.execute.insertInto(TRANSACTIONLABELLIST)
                        .set(TRANSACTIONLABELLIST.TRANSACTION_ID, transactionRecord.getId())
                        .set(TRANSACTIONLABELLIST.LABEL_ID, label.id)
                        .execute();
                log.debug("{} labels stored for Transaction ID {}", noLabels, transactionRecord.getId());
            }

        }
    }

    @Override
    public List<Label> getAllLabels() {
        throw new RuntimeException("Forgot to impl.");
    }

    @Override
    public List<TransactionType> getAllTransactionTypes() {

        Result<Record> dbRecords = execute.select().from(TRANSACTIONTYPE).fetch();

        return dbRecords.stream()
                .map(record -> TransactionType.fromDB(record))
                .collect(toList());
    }
}
