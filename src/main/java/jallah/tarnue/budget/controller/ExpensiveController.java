package jallah.tarnue.budget.controller;

import jallah.tarnue.budget.model.Expense;
import jallah.tarnue.budget.repository.ExpenseRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
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

    private ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private ExpenseRepository expenseRepository;

    private static final String expenseErrorMsg = "Please enter valid information";
    private static final String expenseAddedMsg = "Successfully added usage";

    @Autowired
    public ExpensiveController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public void addExpense(ActionEvent event) {
        if (isValidExpense()) {
            addExpense();
            showAlert(Alert.AlertType.INFORMATION, expenseAddedMsg).ifPresent(type -> closeWindow());
        } else {
            showAlert(Alert.AlertType.ERROR, expenseErrorMsg);
        }
    }

    public ObservableList<Expense> getExpenses() {
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
        return !StringUtils.isEmpty(usageText) && !StringUtils.isEmpty(amountTxt)
                && null != dueDate;
    }

    private void addExpense() {
        Expense expense = new Expense();
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
