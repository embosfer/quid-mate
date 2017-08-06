package com.embosfer.quidmate.db.translator;

/**
 * Created by embosfer on 06/08/2017.
 */
public class LabelPatternTranslator {

    public String[] translateFromDbPatternToWords(String pattern) {

        return pattern.split("\\|");
    }
}
