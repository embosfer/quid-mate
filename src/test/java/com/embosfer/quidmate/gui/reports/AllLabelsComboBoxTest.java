package com.embosfer.quidmate.gui.reports;

import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.db.DbConnection;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AllLabelsComboBoxTest {

    Label label1 = Label.of(1, Description.of("Bills"), null);
    Label label2 = Label.of(2, Description.of("Shopping"), null);
    DbConnection dbConnection = mock(DbConnection.class);

    @Test
    public void populatesComboBoxWithAllLabelsAvailable() {

        when(dbConnection.getAllLabels(Optional.empty())).thenReturn(asList(label1, label2));

        AllLabelsComboBox allLabelsComboBox = new AllLabelsComboBox(dbConnection);
        ObservableList<Description> items = allLabelsComboBox.getItems();
        Assert.assertThat(items, hasItems(label1.description, label2.description));

    }

}