package com.embosfer.quidmate.db.translator;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by embosfer on 06/08/2017.
 */
public class LabelPatternTranslatorTest {

    LabelPatternTranslator labelPatternTranslator = new LabelPatternTranslator();

    @Test
    public void translatesOrCondition() {

        String[] keyWords = labelPatternTranslator.translateFromDb("A|B|C");

        assertThat(keyWords, equalTo(new String[] {"A", "B", "C"}));
    }
}