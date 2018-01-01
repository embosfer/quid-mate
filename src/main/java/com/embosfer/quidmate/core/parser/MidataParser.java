package com.embosfer.quidmate.core.parser;

import com.embosfer.quidmate.core.exceptions.UnknownFileFormatException;
import com.embosfer.quidmate.core.model.*;
import com.embosfer.quidmate.db.DbConnection;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toList;

/**
 * Created by embosfer on 26/07/2017.
 */
public class MidataParser {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("dd/MM/yyyy");
    private static final Charset CHARSET = Charset.forName("ISO-8859-1");
    private final DbConnection dbConnection;

    public MidataParser(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    private LocalDate getLastProcessedTransactionDate() {
        List<LabeledTransaction> lastProcessedTransaction = dbConnection.retrieveLastTransactions(1);
        if (lastProcessedTransaction.size() > 0) {
            return lastProcessedTransaction.get(0).transaction.date;
        } else {
            return null;
        }
    }

    public List<Transaction> parse(File midataFile) throws UnknownFileFormatException {
        if (midataFile.length() == 0) throw new UnknownFileFormatException(midataFile.getName());

        LocalDate lastProcessedTransactionDate = getLastProcessedTransactionDate();

        try (Stream<String> lines = Files.lines(Paths.get(midataFile.getAbsolutePath()), CHARSET)) {
            return lines
                    .skip(1) // skip header
                    .filter(processableLines())
                    .filter(notYetProcessedTransaction(lastProcessedTransactionDate))
                    .map(line -> {
                        String[] fields = line.split(";");
                        int i = 0;
                        Transaction transaction = Transaction.of(LocalDate.parse(fields[i++], DATE_TIME_FORMATTER),
                                TransactionType.fromCSV(fields[i++]),
                                Description.of(fields[i++]),
                                DebitCredit.of(DoubleValueType.fromCSV(fields[i++])),
                                Balance.of(DoubleValueType.fromCSV(fields[i++])));
                        return transaction;
                    })
                    .collect(toList());

        } catch (IOException e) {
            throw new UnknownFileFormatException(midataFile.getName());
        }
    }

    private Predicate<String> processableLines() {
        return line -> !line.isEmpty() && !line.startsWith("Arranged overdraft");
    }

    private Predicate<String> notYetProcessedTransaction(LocalDate lastProcessedTransactionDate) {
        return line -> lastProcessedTransactionDate == null || dateFrom(line).isAfter(lastProcessedTransactionDate);
    }

    private LocalDate dateFrom(String line) {
        return LocalDate.of(parseInt(line.substring(6, 10)), parseInt(line.substring(3, 5)), parseInt(line.substring(0, 2)));
    }

}
