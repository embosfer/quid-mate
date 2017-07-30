package com.embosfer.quidmate.core.model;

import org.junit.Test;

import java.time.LocalDate;

import static com.embosfer.quidmate.core.model.TransactionType.CARD_PAYMENT;
import static com.embosfer.quidmate.core.model.TransactionType.CASH_WDL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by embosfer on 30/07/2017.
 */
public class TransactionTest {

    @Test
    public void sameInstancesAreEqual() {
        Transaction transaction = Transaction.of(
                LocalDate.of(2017, 5, 28),
                CARD_PAYMENT,
                Description.of("CARD PAYMENT TO X"),
                DebitCredit.of(-2.00),
                Balance.of(998.00));
        assertThat(transaction, equalTo(transaction));
    }

    @Test
    public void sameValuesButDifferentInstancesAreEqual() {
        Transaction transaction = Transaction.of(
                LocalDate.of(2017, 5, 28),
                CARD_PAYMENT,
                Description.of("CARD PAYMENT TO X"),
                DebitCredit.of(-2.00),
                Balance.of(998.00));
        Transaction sameTransactionButDifferentObject = Transaction.of(
                LocalDate.of(2017, 5, 28),
                CARD_PAYMENT,
                Description.of("CARD PAYMENT TO X"),
                DebitCredit.of(-2.00),
                Balance.of(998.00));
        assertThat(transaction, equalTo(sameTransactionButDifferentObject));
    }

    @Test
    public void differentValuesAreNotEqual() {
        Transaction transaction = Transaction.of(
                LocalDate.of(2017, 5, 28),
                CARD_PAYMENT,
                Description.of("CARD PAYMENT TO X"),
                DebitCredit.of(-2.00),
                Balance.of(998.00));
        Transaction differentTransaction = Transaction.of(
                LocalDate.of(2017, 5, 27),
                CASH_WDL,
                Description.of("CASH WITHDRAWAL AT Y"),
                DebitCredit.of(-30.00),
                Balance.of(1000));
        assertThat(transaction, not(equalTo(differentTransaction)));
    }

}