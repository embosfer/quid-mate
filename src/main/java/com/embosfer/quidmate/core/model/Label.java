package com.embosfer.quidmate.core.model;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

/**
 * Created by embosfer on 31/07/2017.
 */
public class Label {

    public final Description description;
    public final List<String> wordsToFind;
    public final Optional<Pattern> patternToFind;
    public final Optional<Label> parentLabel;
    public final int id;

    private Label(int id, Description description, Label parentLabel, String... wordsToFind) {
        this.id = id;
        this.description = description;
        this.wordsToFind = wordsToFind == null || wordsToFind.length == 0 ? emptyList() : ImmutableList.copyOf(wordsToFind);
        this.patternToFind = Optional.ofNullable(wordsToFind == null ? null : Pattern.compile(this.wordsToFind.stream().collect(joining("|"))));
        this.parentLabel = Optional.ofNullable(parentLabel);
    }

    public static Label of(int id, Description description, Label parentLabel, String... wordsToFind) {
        return new Label(id, description, parentLabel, wordsToFind);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Label)) return false;
        Label otherLabel = (Label) obj;

        return this.id == otherLabel.id
                && Objects.equals(this.description, otherLabel.description)
                && Objects.equals(this.wordsToFind, otherLabel.wordsToFind)
                && Objects.equals(this.patternToFind, otherLabel.parentLabel)
                && Objects.equals(this.parentLabel, otherLabel.parentLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, wordsToFind, patternToFind, parentLabel);
    }

    @Override
    public String toString() {
        return "[Id: " + id + ", Description: " + description + ", WordsToFind: " + wordsToFind + ", pattern: " + patternToFind + ", parentLabel: " + parentLabel + "]";
    }

    // For Java FX
    public Description getDescription() {
        return description;
    }

    public List<String> getWordsToFind() {
        return wordsToFind;
    }

    public Description getParentLabelDescription() {
        return parentLabel.map(parentLabel -> parentLabel.description).orElse(null);
    }
}
