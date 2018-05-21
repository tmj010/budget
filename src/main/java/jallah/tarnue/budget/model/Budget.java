package jallah.tarnue.budget.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Budget")
@Access(AccessType.PROPERTY)
public class Budget {
    private IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private Date payPeriod;
    private DoubleProperty payAmount = new SimpleDoubleProperty(this, "payAmount");
    private DoubleProperty amountLeft = new SimpleDoubleProperty(this, "amountLeft");
    private DoubleProperty spendingMoney = new SimpleDoubleProperty(this, "spendingMoney");
    private DoubleProperty primarySaving = new SimpleDoubleProperty(this, "primarySaving");
    private DoubleProperty secondarySaving = new SimpleDoubleProperty(this, "secondarySaving");

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id.get();
    }

    public void setId(Integer id) {
        this.id.setValue(id);
    }

    @Column(name = "pay_amount")
    public Double getPayAmount() {
        return payAmount.doubleValue();
    }

    public void setPayAmount(Double payAmount) {
        this.payAmount.setValue(payAmount);
    }

    @Column(name = "amount_left")
    public Double getAmountLeft() {
        return amountLeft.doubleValue();
    }

    public void setAmountLeft(Double amountLeft) {
        this.payAmount.setValue(amountLeft);
    }

    @Column(name = "spending_money")
    public Double getSpendingMoney() {
        return spendingMoney.doubleValue();
    }

    public void setSpendingMoney(Double spendingMoney) {
        this.spendingMoney.setValue(spendingMoney);
    }

    @Column(name = "primary_saving")
    public Double getPrimarySaving() {
        return primarySaving.doubleValue();
    }

    public void setPrimarySaving(Double primarySaving) {
        this.primarySaving.setValue(primarySaving);
    }

    @Column(name = "secondary_saving")
    public Double getSecondarySaving() {
        return secondarySaving.doubleValue();
    }

    public void setSecondarySaving(Double secondarySaving) {
        this.secondarySaving.setValue(secondarySaving);
    }

    @Column(name = "pay_period")
    public Date getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(Date payPeriod) {
        this.payPeriod = payPeriod;
    }
}
