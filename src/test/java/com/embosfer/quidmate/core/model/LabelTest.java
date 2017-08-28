package com.embosfer.quidmate.core.model;

import org.junit.Test;

import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class LabelTest {

    @Test
    public void emptyWordsLabelIsAnEmptyPatternToFind() {
        Label label = Label.of(1, null, null, "");

        assertThat(label.patternToFind, equalTo(Optional.empty()));
        assertThat(label.wordsToFind, equalTo(emptyList()));
    }

    @Test
    public void emptyArrayOfWordsLabelIsAnEmptyPatternToFind() {
        Label label = Label.of(1, null, null, new String[]{});

        assertThat(label.patternToFind, equalTo(Optional.empty()));
        assertThat(label.wordsToFind, equalTo(emptyList()));
    }

}