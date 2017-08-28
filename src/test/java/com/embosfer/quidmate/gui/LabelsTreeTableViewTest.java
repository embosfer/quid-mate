package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.db.DbConnection;
import org.junit.Test;

import java.util.Optional;

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

}