package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.db.DbConnection;
import javafx.scene.control.TreeItem;
import org.junit.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by embosfer on 14/08/2017.
 */
public class LabelsTreeTableViewTest {

    DbConnection dbConnection = mock(DbConnection.class);

    @Test
    public void labelsGetLoadedFromDbLazilyWhenTheLabelTabIsClicked() {
        LabelsTreeTableView labelsTreeTableView = new LabelsTreeTableView(dbConnection);
        verify(dbConnection, times(0)).getAllLabels(Optional.empty());

        labelsTreeTableView.onTabClicked();

        verify(dbConnection, times(1)).getAllLabels(Optional.empty());
    }

    @Test
    public void labelsGetLoadedFromDbOnlyOnce() {
        LabelsTreeTableView labelsTreeTableView = new LabelsTreeTableView(dbConnection);

        labelsTreeTableView.onTabClicked();
        labelsTreeTableView.onTabClicked();
        labelsTreeTableView.onTabClicked();

        verify(dbConnection, times(1)).getAllLabels(Optional.empty());
    }

    @Test
    public void treeStructureIsBuiltCorrectly() {
        LabelsTreeTableView labelsTreeTableView = new LabelsTreeTableView(dbConnection);
        Label parentLabel1 = Label.of(1, Description.of("Parent1"), null, "wp1", "wp2");
        Label childLabel1 = Label.of(2, Description.of("Child1"), parentLabel1, "wc1", "wc2");
        Label childOfChildLabel1 = Label.of(3, Description.of("ChildOfChild1"), childLabel1, "wcc1", "wcc2");
        Label parentLabel2 = Label.of(4, Description.of("Parent2"), null, new String[]{});
        when(dbConnection.getAllLabels(Optional.empty()))
            .thenReturn(asList(parentLabel1, childLabel1, childOfChildLabel1, parentLabel2));

        labelsTreeTableView.onTabClicked();

        // row1
        TreeItem<Label> treeItemLevel1 = labelsTreeTableView.getTreeItem(0);
        assertThat(treeItemLevel1.getValue(), equalTo(parentLabel1));
        assertThat(treeItemLevel1.getChildren().get(0).getValue(), equalTo(childLabel1));
        assertThat(treeItemLevel1.getChildren().get(0).getChildren().get(0).getValue(), equalTo(childOfChildLabel1));

        // row2
        TreeItem<Label> treeItemLevel2 = labelsTreeTableView.getTreeItem(1);
        assertThat(treeItemLevel2.getValue(), equalTo(parentLabel2));
    }

}