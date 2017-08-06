package com.embosfer.quidmate.core.model;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * Created by embosfer on 31/07/2017.
 */
public class Label {

    public final Description description;
    public final String[] wordsToFind;
    public final Pattern patternToFind;
    public final Optional<Label> parentLabel;
    public final int id;

    private Label(int id, Description description, Label parentLabel, String... wordsToFind) {
        this.id = id;
        this.description = description;
        this.wordsToFind = wordsToFind; // TODO Guava immutable
        this.patternToFind = Pattern.compile(Stream.of(wordsToFind).collect(joining("|")));
        this.parentLabel = Optional.ofNullable(parentLabel);
    }

    public static Label of(int id, Description description, Label parentLabel, String... wordsToFind) {
        return new Label(id, description, parentLabel, wordsToFind);
    }
}
