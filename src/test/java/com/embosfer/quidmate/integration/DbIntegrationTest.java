package com.embosfer.quidmate.integration;

import com.embosfer.quidmate.core.model.TransactionType;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.Test;

import static com.embosfer.quidmate.db.quidmate.tables.Transactiontype.TRANSACTIONTYPE;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by embosfer on 28/05/2017.
 */
public class DbIntegrationTest extends DbTestSupport {

    @Test
    public void containsAllRequiredTransactionTypes() {
        Result<Record> dbRecords = execute.select().from(TRANSACTIONTYPE).fetch();

        //TODO: create a matcher instead
        TransactionType[] actualTransactionTypes = dbRecords.stream()
                .map(record -> TransactionType.fromDB(record))
                .collect(toList()).toArray(new TransactionType[TransactionType.values().length]);

        assertThat(actualTransactionTypes, equalTo(TransactionType.values()));
    }

}
