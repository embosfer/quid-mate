package com.embosfer.quidmate.gui;

import com.embosfer.quidmate.core.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toCollection;


/**
 * Created by embosfer on 30/07/2017.
 */
public class TransactionsTable extends TableView<Transaction> {

    public TransactionsTable() {
        setId("LoadedTransactions");
        getColumns().addAll(dateColumn(), typeColumn(), descriptionColumn(), debitCreditColumn(), balanceColumn());
    }
    private TableColumn<Transaction, LocalDate> dateColumn() {
        TableColumn<Transaction, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        return dateColumn;
    }

    private TableColumn<Transaction, TransactionType> typeColumn() {
        TableColumn<Transaction, TransactionType> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        return typeColumn;
    }

    private TableColumn<Transaction, Description> descriptionColumn() {
        TableColumn<Transaction, Description> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        return descriptionColumn;
    }

    private TableColumn<Transaction, DebitCredit> debitCreditColumn() {
        TableColumn<Transaction, DebitCredit> debitCreditColumn = new TableColumn<>("Debit/Credit");
        debitCreditColumn.setCellValueFactory(new PropertyValueFactory<>("debitCredit"));
        return debitCreditColumn;
    }

    private TableColumn<Transaction, Balance> balanceColumn() {
        TableColumn<Transaction, Balance> balanceColumn = new TableColumn<>("Balance");
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        return balanceColumn;
    }

    public void add(List<Transaction> transactions) {
        ObservableList<Transaction> items = transactions.stream().collect(toCollection(FXCollections::observableArrayList));
        setItems(items);
    }
}
