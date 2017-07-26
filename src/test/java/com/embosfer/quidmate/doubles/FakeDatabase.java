package com.embosfer.quidmate.doubles;

import com.embosfer.quidmate.core.model.Transaction;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.embosfer.quidmate.db.quidmate.tables.Transaction.TRANSACTION;
import static org.jooq.impl.DSL.count;

/**
 * Created by embosfer on 28/05/2017.
 */
public class FakeDatabase {


    private Connection connection;
    private DSLContext dslContext;

    public boolean contains(Transaction[] transactions) {
        for (Transaction transaction: transactions) {

            Result<Record> result = dslContext.select()
                    .from(TRANSACTION)
                    .where(TRANSACTION.DATE.eq(transaction.date))
                    .and(TRANSACTION.DESCRIPTION.eq(transaction.description.value))
                    .and(TRANSACTION.BALANCE.eq(transaction.balance.value))
                    .and(TRANSACTION.DEBIT_CREDIT.eq(transaction.debitCredit.value))
                    .fetch();
            if (result.size() != 1) throw new IllegalStateException("Blah"); //TODO: create a matcher instead
        }
        return true;
    }

    public void stop() {

    }

    public void starts() throws SQLException {
        String userName = "root";
        String password = "m@ur0$M@t3";
        String url = "jdbc:mysql://localhost:3306/QuidMate";
        connection = DriverManager.getConnection(url, userName, password);
        dslContext = DSL.using(connection);
    }


    public void stops() throws SQLException {
        connection.close();
        dslContext.close();
    }
}
