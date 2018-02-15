package jallah.tarnue.budget.controller;

import jallah.tarnue.budget.SpringFXMLLoader;
import jallah.tarnue.budget.model.Expense;
import jallah.tarnue.budget.util.BudgetUtil;
import javafx.beans.binding.Bindings;
import javafx.collections.SetChangeListener;
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
    private Label totalExpensesLbl;
    @FXML
    private Label amountLeftLbl;
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

    public void addExpense(ActionEvent event) throws IOException {
        Parent expenseNode = fxmlLoader.load(EXPENSE_FXML);
        Stage expenseStage = new Stage(StageStyle.DECORATED);
        expenseStage.initModality(Modality.WINDOW_MODAL);
        expenseStage.initOwner(root.getScene().getWindow());
        expenseStage.setScene(new Scene(expenseNode));
        expenseStage.setTitle(EXPENSE_TITLE);
        expenseStage.show();
    }

    @FXML
    private void initialize() {
        tableInitialization();
    }

    private void tableInitialization() {
        expenseCol.setCellValueFactory(new PropertyValueFactory<>("expense"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Date>() {
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
                        LOGGER.severe("Error in date");
                    }
                }
                return null;
            }
        }));

        completedCol.setCellValueFactory(new PropertyValueFactory<>("completed"));
        completedCol.setCellFactory(CheckBoxTableCell.forTableColumn(completedCol));
        expensiveController.getExpenses().addListener((SetChangeListener<Expense>) change -> {
            if (change.wasAdded()) {
                Expense expenseAdded = change.getElementAdded();
                expenseTable.getItems().add(expenseAdded);
                Double amount = expenseAdded.getAmount();
                Double initialVal = Double.valueOf(totalExpensesLbl.getText());
                totalExpensesLbl.setText((initialVal + amount) + "");
            } else if (change.wasRemoved()) {
                Expense expenseRemoved = change.getElementRemoved();
                expenseTable.getItems().remove(expenseRemoved);
                Double amount = expenseRemoved.getAmount();
                Double initialVal = Double.valueOf(totalExpensesLbl.getText());
                totalExpensesLbl.setText((initialVal - amount) + "");
                String currentAmount = totalExpensesLbl.getText();
                if (!currentAmount.isEmpty() && currentAmount.equals("0.0")) {
                    totalExpensesLbl.setText("0.00");
                }
            }
        });

        expenseTable.setRowFactory(tableView -> {
            final TableRow<Expense> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem editExpense = new MenuItem("Edit");
            editExpense.setOnAction(event -> {
                try {
                    addExpense(event);
                    Expense expense = row.getItem();
                    expensiveController.setExpense(expense);
                    expensiveController.editExpense(expense);
                    expensiveController.getAddBtn().setText(BudgetUtil.UPDATE_TEXT);
                } catch (IOException e) {
                    LOGGER.severe("Error");
                }
            });

            MenuItem deleteExpense = new MenuItem("Delete");
            deleteExpense.setOnAction(event -> {
                expensiveController.getExpenses().remove(row.getItem());
                tableView.getItems().remove(row.getItem());
            });

            rowMenu.getItems().addAll(editExpense, deleteExpense);
            row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                            .then(rowMenu)
                            .otherwise((ContextMenu) null));

            return row;
        });
    }

    private void labelDisplay() {

    }
}
