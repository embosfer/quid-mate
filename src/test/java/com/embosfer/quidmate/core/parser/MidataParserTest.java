package com.embosfer.quidmate.core.parser;

import com.embosfer.quidmate.core.exceptions.UnknownFileFormatException;
import com.embosfer.quidmate.core.model.*;
import com.embosfer.quidmate.support.MidataSupport.MidataFile;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.embosfer.quidmate.core.model.TransactionType.*;
import static com.embosfer.quidmate.support.MidataSupport.file;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by embosfer on 26/07/2017.
 */
public class MidataParserTest {

    MidataParser parser = new MidataParser();

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
        Transaction[] transactions = {
                Transaction.of(now(), CARD_PAYMENT, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), CASH_AVAILABILITY_PCAS, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), CASH_WDL, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), CASH_WDL_REV, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), CASHBACK, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), CHEQUE_PAID_IN, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), CLEARED_CHEQUE, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), CREDIT_IN, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), DD, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), FAST_PAYMENT, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), FEES, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), INTEREST, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), MONTHLY_ACCOUNT_FEE, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), NONSTERLING_PURCHASE_FEE, Description.of("A"), DebitCredit.of(1), Balance.of(1)),
                Transaction.of(now(), PAYMENTS, Description.of("A"), DebitCredit.of(1), Balance.of(1))
        };

        MidataFile midataFile = file(midataFilename)
                .withHeader("Date;Type;Merchant/Description;Debit/Credit;Balance;")
                .withTransactions(transactions);

        List<Transaction> actualTransactions = parser.parse(midataFile.outputFile);// TODO review this accessor (maybe add a getter instead)
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

}
