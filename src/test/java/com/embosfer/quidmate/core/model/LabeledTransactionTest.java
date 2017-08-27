package com.embosfer.quidmate.core.model;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class LabeledTransactionTest {

    @Test
    public void rootAndLeafLabelLabelPropertiesAreRetrievedCorrectlyForOneLevelLabels() {
        Label oneLevelLabel = Label.of(0, null, null, new String[]{});

        LabeledTransaction transaction = LabeledTransaction.of(null, Optional.of(oneLevelLabel));

        assertThat(transaction.rootLabel.get(), equalTo(oneLevelLabel));
        assertThat(transaction.leafLabel.get(), equalTo(oneLevelLabel));
    }

    @Test
    public void rootAndLeafLabelPropertiesAreRetrievedCorrectlyForMultipleLevelLabels() {
        Label parentLabel = Label.of(0, null, null, new String[]{});
        Label firstLevelLabel = Label.of(1, null, parentLabel, new String[]{});

        LabeledTransaction transaction = LabeledTransaction.of(null, Optional.of(firstLevelLabel));

        assertThat(transaction.rootLabel.get(), equalTo(parentLabel));
        assertThat(transaction.leafLabel.get(), equalTo(firstLevelLabel));
    }

}