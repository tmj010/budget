package jallah.tarnue.budget.controller;

import jallah.tarnue.budget.SpringFXMLLoader;
import jallah.tarnue.budget.model.Expense;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MainController {
    @FXML
    private AnchorPane root;
    @FXML
    private DatePicker payPeriod;
    @FXML
    private TextField payAmountTxt;
    @FXML
    private Button addExpenseBtn;
    @FXML
    private TableView<Expense> expenseTable;
    @FXML
    private TableColumn<Expense, String> expenseCol;
    @FXML
    private TableColumn<Expense, Double> amountCol;
    @FXML
    private TableColumn<Expense, Date> dateCol;
    @FXML
    private TableColumn<Expense, Boolean> completedCol;
    @FXML
    private TextField totalUsageTxt;
    @FXML
    private TextField amountLeftTxt;
    @FXML
    private TextField spendMoneyTxt;
    @FXML
    private TextField primarySavingTxt;
    @FXML
    private TextField secondarySavingTxt;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    private SpringFXMLLoader fxmlLoader;
    private ExpensiveController expensiveController;

    private static final Logger LOGGER = Logger.getGlobal();
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
    private static final String EXPENSE_FXML = "/fxml/expense.fxml";
    private static final String EXPENSE_TITLE = "Expense";

    @Autowired
    public MainController(ExpensiveController expensiveController, SpringFXMLLoader fxmlLoader) {
        this.expensiveController = expensiveController;
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    private void initialize() {
        tableInitialization();
    }

    private void tableInitialization() {
        expenseCol.setCellValueFactory(new PropertyValueFactory<>("expense"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(TextFieldTableCell.forTableColumn(new DateColDisplay()));
        completedCol.setCellValueFactory(new PropertyValueFactory<>("completed"));
        completedCol.setCellFactory(CheckBoxTableCell.forTableColumn(completedCol));
        expensiveController.getExpenses().addListener((ListChangeListener<Expense>) c -> {
            if (c.next()) {
                expenseTable.getItems().addAll(c.getAddedSubList());
            }
        });
        expenseTable.setRowFactory(tableView -> {
            final TableRow<Expense> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem editExpense = new MenuItem("Edit");
            editExpense.setOnAction(event -> {
                try {
                    expensiveController.editExpense(row.getItem());
                    addExpense(event);
                } catch (IOException e) {
                }
            });

            MenuItem deleteExpense = new MenuItem("Delete");
            deleteExpense.setOnAction(event -> tableView.getItems().remove(row.getItem()));

            rowMenu.getItems().addAll(editExpense, deleteExpense);
            row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                            .then(rowMenu)
                            .otherwise((ContextMenu) null));

            return row;
        });
    }

    public void addExpense(ActionEvent event) throws IOException {
        Parent expenseNode = fxmlLoader.load(EXPENSE_FXML);
        Stage expenseStage = new Stage(StageStyle.DECORATED);
        expenseStage.initModality(Modality.WINDOW_MODAL);
        expenseStage.initOwner(root.getScene().getWindow());
        expenseStage.setScene(new Scene(expenseNode));
        expenseStage.setTitle(EXPENSE_TITLE);
        expenseStage.show();
    }

    private class DateColDisplay extends StringConverter<Date> {
        @Override
        public String toString(Date date) {
            if (null == date) {
                return "";
            }
            return dateFormatter.format(date);
        }

        @Override
        public Date fromString(String dateStr) {
            if (null != dateStr) {
                try {
                    return dateFormatter.parse(dateStr);
                } catch (ParseException e) {
                    LOGGER.log(Level.SEVERE, "Error in date");
                }
            }
            return null;
        }
    }

}
