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
import java.util.*;

import static com.embosfer.quidmate.jooq.quidmate.Tables.*;
import static com.embosfer.quidmate.jooq.quidmate.tables.Transactiontype.TRANSACTIONTYPE;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.reverse;
import static java.util.Collections.reverseOrder;
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

            Optional<Label> nextLabel = lt.leafLabel; // TODO: test order of label storage
            while (nextLabel.isPresent()) {
                int noLabels = this.execute.insertInto(TRANSACTIONLABELLIST)
                        .set(TRANSACTIONLABELLIST.TRANSACTION_ID, transactionRecord.getId())
                        .set(TRANSACTIONLABELLIST.LABEL_ID, nextLabel.get().id)
                        .execute();
                log.debug("{} labels stored for Transaction ID {}", noLabels, transactionRecord.getId());
                nextLabel = nextLabel.get().parentLabel;
            }

        }
    }

    @Override
    public void store(Label label) {
        int noLabelsStored = this.execute.insertInto(TRANSACTIONLABEL)
                .set(TRANSACTIONLABEL.ID, label.id)
                .set(TRANSACTIONLABEL.PARENT_ID, label.parentLabel.map(parentLab -> parentLab.id).orElse(null)) // TODO: refactor this to be an Optional
                .set(TRANSACTIONLABEL.NAME, label.description.value)
                .set(TRANSACTIONLABEL.PATTERN, label.patternToFind.map(lab -> lab.toString()).orElse(null))
                .execute();
        if (noLabelsStored != 1) throw new IllegalStateException("Couldn't store label " + label);
    }

    @Override
    public List<Label> getAllLabels() {
        Result<Record> labelRecords = execute.select()
                .from(TRANSACTIONLABEL)
                .orderBy(TRANSACTIONLABEL.PARENT_ID.asc(), TRANSACTIONLABEL.ID.asc()) // this order guarantees that we can chain labels by parent properly
                .fetch();

        return buildLabelHierarchyFrom(labelRecords);
    }

    private List<Label> buildLabelHierarchyFrom(Result<Record> transactionLabelDbRecords) {
        Map<Integer, Label> labels = new HashMap<>();
        return transactionLabelDbRecords.stream() // TODO: not sure this bit still works, we store from leaf upwards, so the order might be broken here
                .map(labelDbRecord -> {
                            String wordsToFind = labelDbRecord.get(TRANSACTIONLABEL.PATTERN);

                            Label label = Label.of(labelDbRecord.get(TRANSACTIONLABEL.ID),
                                    Description.of(labelDbRecord.get(TRANSACTIONLABEL.NAME)),
                                    labels.get(labelDbRecord.get(TRANSACTIONLABEL.PARENT_ID)),
                                    wordsToFind == null ? "" : wordsToFind);

                            labels.put(label.id, label); // TODO can I do this in a more functional way?
                            return label;
                        }
                )
                .filter(label -> label.patternToFind.isPresent())
                .sorted(reverseOrder()) // TODO: check that this sort them by levels: the leaf labels first, which will contain the pointers to their parents...
                .collect(toImmutableList());
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

        for (Record tranDbRecord : transactionRecords) {

            Transaction transaction = Transaction.of(tranDbRecord.get(TRANSACTION.DATE).plusDays(1), // TODO LocalDate gets loaded from DB (jooq) with a day less!
                    TransactionType.fromDB(tranDbRecord.get(TRANSACTION.TYPE_ID)),
                    Description.of(tranDbRecord.get(TRANSACTION.DESCRIPTION)),
                    DebitCredit.of(tranDbRecord.get(TRANSACTION.DEBIT_CREDIT)),
                    Balance.of(tranDbRecord.get(TRANSACTION.BALANCE)));

            Result<Record> transactionLabelDbRecords = execute // TODO: I can probably create a cache of labels so I only select the ids, as opposed to everything...
                    .select(TRANSACTIONLABEL.fields())
                    .from(TRANSACTIONLABELLIST, TRANSACTIONLABEL)
                    .where(TRANSACTIONLABELLIST.TRANSACTION_ID.eq(tranDbRecord.get(TRANSACTION.ID)))
                    .and(TRANSACTIONLABELLIST.LABEL_ID.eq(TRANSACTIONLABEL.ID))
                    .fetch();

            List<Label> labelList = buildLabelHierarchyFrom(transactionLabelDbRecords);
            if (labelList.size() > 1)
                throw new IllegalStateException("A transaction cannot have more than 2 leaf labels!!! Got: " + labelList);

            labeledTransactions.add(LabeledTransaction.of(transaction, Optional.of(labelList.get(0))));
        }

        return labeledTransactions;
    }
}
