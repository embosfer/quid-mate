package com.embosfer.quidmate.core.model;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

/**
 * Created by embosfer on 31/07/2017.
 */
public class Label {

    public final Description description;
    public final List<String> wordsToFind;
    public final Pattern patternToFind;
    public final Optional<Label> parentLabel;

    private Label(Description description, List<String> wordsToFind, Label parentLabel) {
        this.description = description;
        this.wordsToFind = wordsToFind; // TODO Guava immutable
        this.patternToFind = Pattern.compile(wordsToFind.stream().collect(joining("|")));
        this.parentLabel = Optional.ofNullable(parentLabel);
    }

    public static Label of(Description description, List<String> wordsToFind, Label parentLabel) {
        return new Label(description, wordsToFind, parentLabel);
    }
}
