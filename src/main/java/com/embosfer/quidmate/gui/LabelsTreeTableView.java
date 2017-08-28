package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.model.Description;
import com.embosfer.quidmate.core.model.Label;
import com.embosfer.quidmate.db.DbConnection;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by embosfer on 13/08/2017.
 */
public class LabelsTreeTableView extends TreeTableView<Label> {

    private final DbConnection dbConnection;
    private boolean initialised = false;

    public LabelsTreeTableView(DbConnection dbConnection) {
        this.dbConnection = dbConnection;

        setId("loadedLabels");
        setShowRoot(false);

        getColumns().addAll(descriptionColumn(), wordsToFindColumn(), parentLabelDescriptionColumn());
    }

    public void onTabClicked() {
        if (!initialised) { // TODO separate thread...
            buildLabelTreeFrom(dbConnection.getAllLabels(Optional.empty()));
            initialised = true;
        }
    }

    private void buildLabelTreeFrom(List<Label> labels) {
        TreeItem<Label> root = new TreeItem<>();
        setRoot(root);
        Map<Integer, TreeItem> labelsById = new HashMap<>();
        for (Label label : labels) {
            TreeItem<Label> treeItem = new TreeItem<>(label);
            if (!label.parentLabel.isPresent()) {
                root.getChildren().add(treeItem);
            } else {
                labelsById.get(label.parentLabel.get().id).getChildren().add(treeItem);
            }
            labelsById.put(label.id, treeItem);
        }
    }

    private TreeTableColumn<Label, Description> descriptionColumn() {
        TreeTableColumn<Label, Description> descriptionColumn = new TreeTableColumn<>("Description");
        descriptionColumn.setPrefWidth(250);
        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        return descriptionColumn;
    }

    private TreeTableColumn<Label, List<String>> wordsToFindColumn() {
        TreeTableColumn<Label, List<String>> descriptionColumn = new TreeTableColumn<>("Words to find");
        descriptionColumn.setPrefWidth(750);
        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("wordsToFind"));
        return descriptionColumn;
    }

    private TreeTableColumn<Label, Description> parentLabelDescriptionColumn() {
        TreeTableColumn<Label, Description> descriptionColumn = new TreeTableColumn<>("Parent label");
        descriptionColumn.setPrefWidth(250);
        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("parentLabelDescription"));
        return descriptionColumn;
    }

}
