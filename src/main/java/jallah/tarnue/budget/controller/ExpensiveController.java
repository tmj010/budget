package jallah.tarnue.budget.controller;

import jallah.tarnue.budget.model.Expense;
import jallah.tarnue.budget.util.BudgetUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Component
public class ExpensiveController {
    @FXML
    private AnchorPane root;
    @FXML
    private TextField expenseTxt;
    @FXML
    private TextField amountTxt;
    @FXML
    private DatePicker dueDateDp;
    @FXML
    private CheckBox completedCb;
    @FXML
    private Button addBtn;
    @FXML
    private Button cancelBtn;

    private Expense expense;
    private ObservableSet<Expense> expenses = FXCollections.observableSet();

    private static final String EXPENSE_ERROR_MSG = "Please enter valid information";

    public void addOrUpdateExpense(ActionEvent event) {
        if (isValidExpense()) {
            if (getAddBtn().getText().equals(BudgetUtil.UPDATE_TEXT)) {
                updateExpense();
            } else {
                addExpense();
            }
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, EXPENSE_ERROR_MSG);
        }
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public Button getAddBtn() {
        return addBtn;
    }

    public ObservableSet<Expense> getExpenses() {
        return expenses;
    }

    public void cancelExpense(ActionEvent event) {
        closeWindow();
    }

    public void editExpense(Expense expense) {
        expenseTxt.setText(expense.getExpense());
        amountTxt.setText(expense.getAmount().toString());
        dueDateDp.setValue(expense.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        completedCb.setSelected(expense.getCompleted());
    }

    private boolean isValidExpense() {
        String usageText = expenseTxt.getText();
        String amountTxt = this.amountTxt.getText();
        LocalDate dueDate = dueDateDp.getValue();
        return !StringUtils.isEmpty(usageText) && NumberUtils.isNumber(amountTxt)
                && null != dueDate;
    }

    private void updateExpense() {
        Expense expense = getExpense();
        if (null != expense) {
            addOrUpdateExpense(expense);
        }
    }

    private void addExpense() {
        addOrUpdateExpense(new Expense());
    }

    private void addOrUpdateExpense(Expense expense) {
        expense.setExpense(expenseTxt.getText());
        expense.setAmount(Double.valueOf(amountTxt.getText()));
        Date date = Date.from(dueDateDp.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        expense.setDate(date);
        expense.setCompleted(completedCb.isSelected());
        expenses.add(expense);
    }

    private Optional<ButtonType> showAlert(Alert.AlertType alertType, String contentTxt) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(contentTxt);
        return alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
