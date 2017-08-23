package com.embosfer.quidmate.db;

import com.embosfer.quidmate.core.DbConfig;
import com.embosfer.quidmate.core.model.*;
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
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;
import static java.util.Collections.reverse;
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

        reverse(transactions); // we want to store them in date ascending order

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
        // TODO: order them by levels: the leaf labels first, which will contain the pointers to their parents...
        // TODO: this is so TransactionLabeler can label more efficiently
        Result<Record> labelRecords = execute.select().from(TRANSACTIONLABEL).fetch();

        List<Label> dbLabels = new ArrayList<>();
        Map<Integer, Label> labelsById = new HashMap<>();

        for (Record labelRecord : labelRecords) {
            String pattern = labelRecord.get(TRANSACTIONLABEL.PATTERN);
            Label label = Label.of(labelRecord.get(TRANSACTIONLABEL.ID),
                    Description.of(labelRecord.get(TRANSACTIONLABEL.NAME)),
                    labelsById.get(labelRecord.get(TRANSACTIONLABEL.PARENT_ID)),
                    pattern == null ? null : labelPatternTranslator.translateFromDb(pattern));
            dbLabels.add(label);
            labelsById.put(label.id, label);
        }

        return (dbLabels.size() > 0 ? dbLabels : emptyList());
    }

    @Override
    public List<TransactionType> getAllTransactionTypes() {

        Result<Record> dbRecords = execute.select().from(TRANSACTIONTYPE).fetch();

        return dbRecords.stream()
                .map(record -> TransactionType.fromDB(record.get(TRANSACTIONTYPE.ID)))
                .collect(toList());
    }

    @Override
    public List<LabeledTransaction> retrieveLastTransactions(int noTransactions) {
        Result<Record> transactionRecords = execute
                .select()
                .from(TRANSACTION)
                .orderBy(TRANSACTION.ID.desc())
                .limit(noTransactions)
                .fetch();

        List<LabeledTransaction> labeledTransactions = new ArrayList<>(transactionRecords.size());

        Map<Integer, Label> labels = new HashMap<>();
        for (Record tranDbRecord : transactionRecords) {

            Result<Record> transactionLabelRecords = execute
                    .select(TRANSACTIONLABEL.fields())
                    .from(TRANSACTIONLABELLIST, TRANSACTIONLABEL)
                    .where(TRANSACTIONLABELLIST.TRANSACTION_ID.eq(tranDbRecord.get(TRANSACTION.ID)))
                    .and(TRANSACTIONLABELLIST.LABEL_ID.eq(TRANSACTIONLABEL.ID))
                    .fetch();

            Transaction transaction = Transaction.of(tranDbRecord.get(TRANSACTION.DATE),
                    TransactionType.fromDB(tranDbRecord.get(TRANSACTION.TYPE_ID)),
                    Description.of(tranDbRecord.get(TRANSACTION.DESCRIPTION)),
                    DebitCredit.of(tranDbRecord.get(TRANSACTION.DEBIT_CREDIT)),
                    Balance.of(tranDbRecord.get(TRANSACTION.BALANCE)));

            List<Label> labelList = transactionLabelRecords.stream()
                    .map(labDbRecord -> {
                                String wordsToFind = labDbRecord.get(TRANSACTIONLABEL.PATTERN);

                                return Label.of(labDbRecord.get(TRANSACTIONLABEL.ID),
                                        Description.of(labDbRecord.get(TRANSACTIONLABEL.NAME)),
                                        labels.get(labDbRecord.get(TRANSACTIONLABEL.PARENT_ID)),
                                        wordsToFind == null ? "" : wordsToFind);
                            }
                    )
                    .collect(toImmutableList());

            labeledTransactions.add(LabeledTransaction.of(transaction, labelList));
        }

        return labeledTransactions;
    }
}
