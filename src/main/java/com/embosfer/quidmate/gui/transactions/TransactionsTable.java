package com.embosfer.quidmate.gui.transactions;

import com.embosfer.quidmate.core.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toCollection;


/**
 * Created by embosfer on 30/07/2017.
 */
public class TransactionsTable extends TableView<LabeledTransaction> {

    public TransactionsTable() {
        setId("loadedTransactions");
        getColumns().addAll(dateColumn(), typeColumn(), descriptionColumn(), debitCreditColumn(), balanceColumn(), labelsColumn());
    }

    private TableColumn<LabeledTransaction, LocalDate> dateColumn() {
        TableColumn<LabeledTransaction, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setPrefWidth(100);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        return dateColumn;
    }

    private TableColumn<LabeledTransaction, TransactionType> typeColumn() {
        TableColumn<LabeledTransaction, TransactionType> typeColumn = new TableColumn<>("Type");
        typeColumn.setPrefWidth(150);
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        return typeColumn;
    }

    private TableColumn<LabeledTransaction, Description> descriptionColumn() {
        TableColumn<LabeledTransaction, Description> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setPrefWidth(750);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        return descriptionColumn;
    }

    private TableColumn<LabeledTransaction, DebitCredit> debitCreditColumn() {
        TableColumn<LabeledTransaction, DebitCredit> debitCreditColumn = new TableColumn<>("Debit/Credit");
        debitCreditColumn.setPrefWidth(100);
        debitCreditColumn.setCellValueFactory(new PropertyValueFactory<>("debitCredit"));
        debitCreditColumn.setCellFactory(tableColumn ->
                new TableCell<LabeledTransaction, DebitCredit>() {
                    @Override
                    protected void updateItem(DebitCredit item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            if (item.value >= 0) {
                                this.setTextFill(Color.FORESTGREEN);
                            } else {
                                this.setTextFill(Color.DARKRED);
                            }
                            setText(item.toString());
                        }
                    }
                });
        return debitCreditColumn;
    }

    private TableColumn<LabeledTransaction, Balance> balanceColumn() {
        TableColumn<LabeledTransaction, Balance> balanceColumn = new TableColumn<>("Balance");
        balanceColumn.setPrefWidth(100);
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        return balanceColumn;
    }

    private TableColumn<LabeledTransaction, Balance> labelsColumn() {
        TableColumn<LabeledTransaction, Balance> labelsColumn = new TableColumn<>("Labels");
        labelsColumn.setPrefWidth(300);
        labelsColumn.setCellValueFactory(new PropertyValueFactory<>("labels"));
        return labelsColumn;
    }

    public void add(List<LabeledTransaction> transactions) {
        ObservableList<LabeledTransaction> items = transactions.stream().collect(toCollection(FXCollections::observableArrayList));
        setItems(items);
    }
}
