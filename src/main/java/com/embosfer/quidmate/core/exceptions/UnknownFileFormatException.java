package com.embosfer.quidmate.core.exceptions;

/**
 * Created by embosfer on 26/07/2017.
 */
public class UnknownFileFormatException extends Exception {

    public UnknownFileFormatException(String midataFileName) {
        super("File " + midataFileName + " doesn't have the expected format");
    }
}
