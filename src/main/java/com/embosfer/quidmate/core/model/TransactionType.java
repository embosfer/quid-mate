package com.embosfer.quidmate.core.model;

import org.jooq.Record;

import static com.embosfer.quidmate.db.quidmate.tables.Transactiontype.TRANSACTIONTYPE;

/**
 * Created by embosfer on 28/05/2017.
 */
public enum TransactionType {

    // "-" not supported in enums so we just get rid of them
    CARD_PAYMENT(1), CASH_AVAILABILITY_PCAS(2), CASH_WDL(3), CASH_WDL_REV(4),
    CASHBACK(5), CHEQUE_PAID_IN(6), CLEARED_CHEQUE(7), CREDIT_IN(8), DEPOSIT(9),
    DD(10), FAST_PAYMENT(11), FEES(12), INTEREST(13), MONTHLY_ACCOUNT_FEE(14),
    NONSTERLING_PURCHASE_FEE(15), PAYMENTS(16);

    public final int id;

    TransactionType(int id) {
        this.id = id;
    }

    public static TransactionType fromCSV(String csvValue) {
        return TransactionType.valueOf(csvValue.replaceAll("\\s", "_").replaceAll("-", ""));
    }

    public static TransactionType fromDB(Record record) {
        TransactionType result = fromCSV(record.get(TRANSACTIONTYPE.TYPE));
        if (record.get(TRANSACTIONTYPE.ID) != result.id) {
            throw new IllegalStateException("TransactionType Id mismatch. Got ID=" + record.get(TRANSACTIONTYPE.ID) + " from DB. Expected ID=" + result.id);
        }
        return result;
    }

    @Override
    public String toString() {
        return name().replaceAll("_", " ");
    }

}
