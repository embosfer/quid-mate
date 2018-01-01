package com.embosfer.quidmate.core.parser;

import com.embosfer.quidmate.core.exceptions.UnknownFileFormatException;
import com.embosfer.quidmate.core.model.*;
import com.embosfer.quidmate.db.DbConnection;
import com.embosfer.quidmate.support.MidataSupport.MidataFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static com.embosfer.quidmate.core.model.TransactionType.*;
import static com.embosfer.quidmate.support.MidataSupport.file;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by embosfer on 26/07/2017.
 */
public class MidataParserTest {

    DbConnection dbConnection = mock(DbConnection.class);
    MidataParser parser = new MidataParser(dbConnection);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private String midataFilename = "test_transactions.csv";

    @Test
    public void emptyFileThrowsUnknownFileFormatException() throws UnknownFileFormatException {
        expectedException.expect(UnknownFileFormatException.class);
        expectedException.expectMessage("File " + midataFilename + " doesn't have the expected format");

        File midataFile = file(midataFilename).outputFile;
        parser.parse(midataFile);
    }

    @Test
    public void unknownFileThrowsUnknownFileFormatException() throws UnknownFileFormatException {
        String unknownFile = "unknown_file.csv";
        expectedException.expect(UnknownFileFormatException.class);
        expectedException.expectMessage("File " + unknownFile + " doesn't have the expected format");

        parser.parse(new File(unknownFile));
    }

    @Test
    public void supportsAllTypesOfTransactions() throws UnknownFileFormatException {
        LocalDate now = now();
        Description someDescription = Description.of("A");
        DebitCredit someValue = DebitCredit.of(1);
        Balance otherValue = Balance.of(1);
        Transaction[] transactions = {
                Transaction.of(now, CARD_PAYMENT, someDescription, someValue, otherValue),
                Transaction.of(now, CASH_AVAILABILITY_PCAS, someDescription, someValue, otherValue),
                Transaction.of(now, CASH_WDL, someDescription, someValue, otherValue),
                Transaction.of(now, CASH_WDL_REV, someDescription, someValue, otherValue),
                Transaction.of(now, CASHBACK, someDescription, someValue, otherValue),
                Transaction.of(now, CHEQUE_PAID_IN, someDescription, someValue, otherValue),
                Transaction.of(now, CLEARED_CHEQUE, someDescription, someValue, otherValue),
                Transaction.of(now, CREDIT_IN, someDescription, someValue, otherValue),
                Transaction.of(now, DEPOSIT, someDescription, someValue, otherValue),
                Transaction.of(now, DD, someDescription, someValue, otherValue),
                Transaction.of(now, FAST_PAYMENT, someDescription, someValue, otherValue),
                Transaction.of(now, FEES, someDescription, someValue, otherValue),
                Transaction.of(now, INTEREST, someDescription, someValue, otherValue),
                Transaction.of(now, MONTHLY_ACCOUNT_FEE, someDescription, someValue, otherValue),
                Transaction.of(now, NONSTERLING_PURCHASE_FEE, someDescription, someValue, otherValue),
                Transaction.of(now, PAYMENTS, someDescription, someValue, otherValue),
                Transaction.of(now, PURCHASE_IN_BRANCHES_PCAS, someDescription, someValue, otherValue)
        };

        MidataFile midataFile = file(midataFilename)
                .withHeader("Date;Type;Merchant/Description;Debit/Credit;Balance;")
                .withTransactions(transactions);

        List<Transaction> actualTransactions = parser.parse(midataFile.outputFile);
        TransactionType[] actualTypes = actualTransactions.stream().map(transaction -> transaction.type).collect(toList()).toArray(new TransactionType[TransactionType.values().length]);

        assertThat(actualTypes, equalTo(TransactionType.values()));
    }

    @Test
    public void supportsNegativeAndPositiveAmounts() throws UnknownFileFormatException {
        Transaction[] transactions = {
                Transaction.of(now(), DD, Description.of("A"), DebitCredit.of(-1), Balance.of(-1)),
                Transaction.of(now(), DD, Description.of("A"), DebitCredit.of(1), Balance.of(0))
        };

        MidataFile midataFile = file(midataFilename)
                .withHeader("Date;Type;Merchant/Description;Debit/Credit;Balance;")
                .withTransactions(transactions);

        List<Transaction> actualTransactions = parser.parse(midataFile.outputFile);

        Transaction tran1 = actualTransactions.get(0);
        assertThat(tran1.debitCredit.value, is(-1.0));
        assertThat(tran1.balance.value, is(-1.0));

        Transaction tran2 = actualTransactions.get(1);
        assertThat(tran2.debitCredit.value, is(1.0));
        assertThat(tran2.balance.value, is(0.0));
    }

    @Test
    public void ignoresArrangedOverdraftLimit() throws UnknownFileFormatException {

        Transaction[] transactions = {
                Transaction.of(now(), DD, Description.of("A"), DebitCredit.of(1), Balance.of(0))
        };

        MidataFile midataFile = file(midataFilename)
                .withHeader("Date;Type;Merchant/Description;Debit/Credit;Balance;")
                .withTransactions(transactions)
                .withArrangedOverdraftLimitInfo();

        List<Transaction> actualTransactions = parser.parse(midataFile.outputFile);

        assertThat(actualTransactions.size(), is(1));
    }

}
