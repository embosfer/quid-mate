package com.embosfer.quidmate.db.translator;

/**
 * Created by embosfer on 06/08/2017.
 */
public class LabelPatternTranslator {

    public String[] translateFromDb(String pattern) {

        return pattern.split("\\|");
    }
}
