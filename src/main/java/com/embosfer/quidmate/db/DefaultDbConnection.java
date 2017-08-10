package com.embosfer.quidmate.db;

import com.embosfer.quidmate.core.DbConfig;
import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.core.model.LabeledTransaction;
import com.embosfer.quidmate.core.model.TransactionType;
import com.embosfer.quidmate.db.translator.LabelPatternTranslator;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.embosfer.quidmate.jooq.quidmate.Tables.*;
import static com.embosfer.quidmate.jooq.quidmate.tables.Transactiontype.TRANSACTIONTYPE;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Created by embosfer on 27/07/2017.
 */
public class DefaultDbConnection implements DbConnection {

    private static final Logger log = LoggerFactory.getLogger(DefaultDbConnection.class);

    private final LabelPatternTranslator labelPatternTranslator;

    private final DbConfig dbConfig;
    private Connection connection;
    private DSLContext execute;

    public DefaultDbConnection(DbConfig dbConfig, LabelPatternTranslator labelPatternTranslator) {
        this.dbConfig = dbConfig;
        this.labelPatternTranslator = labelPatternTranslator;
    }

    @Override
    public void open() throws SQLException {
        connection = DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password);
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
            TransactionRecord transactionRecord = execute.insertInto(TRANSACTION)
                    .set(TRANSACTION.DATE, lt.transaction.date)
                    .set(TRANSACTION.TYPE_ID, lt.transaction.type.id)
                    .set(TRANSACTION.DESCRIPTION, lt.transaction.description.value)
                    .set(TRANSACTION.DEBIT_CREDIT, lt.transaction.debitCredit.value)
                    .set(TRANSACTION.BALANCE, lt.transaction.balance.value)
                    .returning(TRANSACTION.ID)
                    .fetchOne();
            log.debug("Transaction ID {} stored.", transactionRecord.getId());

            for (Label label : lt.labels) {
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

        Result<Record> labelRecords = execute.select().from(TRANSACTIONLABEL).fetch();

        List<Label> dbLabels = new ArrayList<>();
        Map<Integer, Label> labelsById = new HashMap<>();

        for (Record labelRecord : labelRecords) {
            Label label = Label.of(labelRecord.get(TRANSACTIONLABEL.ID).intValue(),
                    Description.of(labelRecord.get(TRANSACTIONLABEL.NAME)),
                    labelsById.get(labelRecord.get(TRANSACTIONLABEL.PARENT_ID)),
                    labelPatternTranslator.translateFromDb(labelRecord.get(TRANSACTIONLABEL.PATTERN)));
            dbLabels.add(label);
            labelsById.put(label.id, label);
        }

        return (dbLabels.size() > 0 ? dbLabels : emptyList());
    }

    @Override
    public List<TransactionType> getAllTransactionTypes() {

        Result<Record> dbRecords = execute.select().from(TRANSACTIONTYPE).fetch();

        return dbRecords.stream()
                .map(record -> TransactionType.fromDB(record))
                .collect(toList());
    }
}
