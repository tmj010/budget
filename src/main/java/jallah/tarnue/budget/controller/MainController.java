package jallah.tarnue.budget.controller;

import jallah.tarnue.budget.SpringFXMLLoader;
import jallah.tarnue.budget.model.Budget;
import jallah.tarnue.budget.model.Expense;
import jallah.tarnue.budget.repository.BudgetRepository;
import jallah.tarnue.budget.repository.ExpenseRepository;
import jallah.tarnue.budget.util.BudgetUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
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
import org.apache.commons.lang3.math.NumberUtils;
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

    private DoubleProperty totalExpensesProperty;
    private DoubleProperty amountLeftOverProperty;
    private DoubleProperty payAmountProperty;

    private SpringFXMLLoader fxmlLoader;
    private ExpensiveController expensiveController;
    private BudgetRepository budgetRepository;
    private ExpenseRepository expenseRepository;

    private static final Logger LOGGER = Logger.getGlobal();
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
    private static final String EXPENSE_FXML = "/fxml/expense.fxml";
    private static final String EXPENSE_TITLE = "Expense";

    private enum Operator {
        PLUS, MINUS
    }

    @Autowired
    public MainController(ExpensiveController expensiveController, BudgetRepository budgetRepository,
                          ExpenseRepository expenseRepository, SpringFXMLLoader fxmlLoader) {
        this.expensiveController = expensiveController;
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.fxmlLoader = fxmlLoader;
        this.totalExpensesProperty = new SimpleDoubleProperty();
        this.amountLeftOverProperty = new SimpleDoubleProperty();
        this.payAmountProperty = new SimpleDoubleProperty();
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

    public void saveExpensive(ActionEvent event) {
        Budget budget = new Budget();
        budget.setAmountLeft(amountLeftOverProperty.doubleValue());
        budget.setPayAmount(payAmountProperty.doubleValue());
        budget.setPayPeriod(BudgetUtil.localDateToDate(payPeriod.getValue()));
        budget.setSpendingMoney(BudgetUtil.getDoubleValue(spendMoneyTxt.getText()));
        budget.setPrimarySaving(BudgetUtil.getDoubleValue(primarySavingTxt.getText()));
        budget.setSecondarySaving(BudgetUtil.getDoubleValue(secondarySavingTxt.getText()));
        Budget savedBudget = budgetRepository.save(budget);
        expensiveController.getExpenses().stream().forEach(expense -> {
            expense.setBudget(savedBudget);
            expenseRepository.save(expense);
        });
    }

    @FXML
    private void initialize() {
        tableInitialization();
        payAmountTxt.textProperty().addListener((o, oldVal, newVal) -> {
            try {
                String strAmtVal = newVal.trim();
                if (NumberUtils.isDigits(strAmtVal)) {
                    Double amtValue = Double.valueOf(strAmtVal);
                    payAmountProperty.setValue(amtValue);
                    if (amtValue > 0) {
                        double textFieldValue = calculateTextField();
                        ObservableSet<Expense> expenses = expensiveController.getExpenses();
                        if (null != expenses && !expenses.isEmpty()) {
                            double totalExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();
                            amountLeftOverProperty.setValue((amtValue - totalExpense) - textFieldValue);
                        } else {
                            amountLeftOverProperty.setValue(amtValue - textFieldValue);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                LOGGER.severe("Error while casting to double");
            }
        });

        amountLeftOverProperty.addListener((o, oldVal, newVal) -> {
            if (null != newVal) {
                String amount = BudgetUtil.formatToUSCurrency(newVal.doubleValue());
                amountLeftLbl.setText(amount);
            }
        });

        totalExpensesProperty.addListener((o, oldVal, newVal) -> {
            if (null != newVal) {
                double totalExpensesValue = newVal.doubleValue();
                String amount = BudgetUtil.formatToUSCurrency(totalExpensesValue);
                totalExpensesLbl.setText(amount);

                // Calculate amount left over
                double payAmount = payAmountProperty.doubleValue();
                if (payAmount > 0) {
                    double textFieldsValue = calculateTextField();
                    double updateAmtLeftOverValue = (payAmount - totalExpensesValue) - textFieldsValue;
                    amountLeftOverProperty.setValue(updateAmtLeftOverValue);
                }
            }
        });

        spendMoneyTxt.focusedProperty().addListener(new TextFieldFocusListener(spendMoneyTxt));
        primarySavingTxt.focusedProperty().addListener(new TextFieldFocusListener(primarySavingTxt));
        secondarySavingTxt.focusedProperty().addListener(new TextFieldFocusListener(secondarySavingTxt));
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

                // Added a listener to update the total expenses label when this expense gets updated
                expenseAdded.amountProperty().addListener((o, oldVal, newVal) -> {
                    Double initialVal = totalExpensesProperty.doubleValue();
                    Double oldDoubleVal = (Double) oldVal;
                    Double newDoubleVal = (Double) newVal;
                    totalExpensesProperty.setValue(((initialVal - oldDoubleVal) + newDoubleVal));
                });

                expenseTable.getItems().add(expenseAdded);
                calculateExpenses(expenseAdded, totalExpensesProperty, Operator.PLUS);

            } else if (change.wasRemoved()) {
                Expense expenseRemoved = change.getElementRemoved();
                expenseTable.getItems().remove(expenseRemoved);
                calculateExpenses(expenseRemoved, totalExpensesProperty, Operator.MINUS);
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

    private Double calculateExpenses(Expense expense, DoubleProperty doubleProperty, Operator operator) {
        Double amount = expense.getAmount();
        double initialVal = doubleProperty.doubleValue();
        switch (operator) {
            case PLUS:
                Double addedValue = (initialVal + amount);
                doubleProperty.setValue(addedValue);
                return addedValue;
            case MINUS:
                Double minusValue = (initialVal - amount);
                doubleProperty.setValue(minusValue);
                return minusValue;
            default:
                return null;
        }
    }

    private double calculateTextField() {
        double totalValue = 0;
        totalValue += BudgetUtil.getDoubleValue(spendMoneyTxt.getText());
        totalValue += BudgetUtil.getDoubleValue(primarySavingTxt.getText());
        totalValue += BudgetUtil.getDoubleValue(secondarySavingTxt.getText());
        return totalValue;
    }

    private class TextFieldFocusListener implements ChangeListener<Boolean> {
        private TextField textField;

        TextFieldFocusListener(TextField textField) {
            this.textField = textField;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean outOfFocus, Boolean inFocus) {
            if (outOfFocus) {
                double calculatedTextField = 0;
                if (textField == spendMoneyTxt) {
                    calculatedTextField += BudgetUtil.getDoubleValue(primarySavingTxt.getText());
                    calculatedTextField += BudgetUtil.getDoubleValue(secondarySavingTxt.getText());
                } else if (textField == primarySavingTxt) {
                    calculatedTextField += BudgetUtil.getDoubleValue(spendMoneyTxt.getText());
                    calculatedTextField += BudgetUtil.getDoubleValue(secondarySavingTxt.getText());
                } else if (textField == secondarySavingTxt) {
                    calculatedTextField += BudgetUtil.getDoubleValue(primarySavingTxt.getText());
                    calculatedTextField += BudgetUtil.getDoubleValue(spendMoneyTxt.getText());
                }
                double amountLeftOver = (payAmountProperty.doubleValue() - totalExpensesProperty.doubleValue()) - calculatedTextField;
                String strAmt = textField.getText();
                if (NumberUtils.isDigits(strAmt)) {
                    double amount = Double.parseDouble(strAmt);
                    if (amount <= amountLeftOver) {
                        amountLeftOverProperty.setValue(amountLeftOver - amount);
                    } else {
                        // TODO add an alert to let the user know that they can't have more then the amount left over
                    }
                } else {
                    amountLeftOverProperty.setValue(amountLeftOver);
                }
            }
        }
    }
}
