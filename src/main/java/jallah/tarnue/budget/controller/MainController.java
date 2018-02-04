package jallah.tarnue.budget.controller;

import jallah.tarnue.budget.model.Usage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class MainController {
    @FXML
    private DatePicker payPeriod;
    @FXML
    private TextField payAmountTxt;
    @FXML
    private Button usageBtn;
    @FXML
    private TableView<Usage> usageTable;
    @FXML
    private TableColumn<Usage, String> usageCol;
    @FXML
    private TableColumn<Usage, Double> amountCol;
    @FXML
    private TableColumn<Usage, Date> dateCol;
    @FXML
    private TableColumn<Usage, Boolean> completedCol;
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

}
