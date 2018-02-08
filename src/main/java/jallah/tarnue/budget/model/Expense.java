package jallah.tarnue.budget.model;

import javafx.beans.property.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Expense")
public class Expense {
    private Date date;
    private Budget budget;
    private IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private StringProperty expense = new SimpleStringProperty(this, "expense");
    private DoubleProperty amount = new SimpleDoubleProperty(this, "amount");
    private BooleanProperty completed = new SimpleBooleanProperty(this, "completed");

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id.get();
    }

    public void setId(Integer id) {
        this.id.setValue(id);
    }

    @Column(name = "expense")
    public String getExpense() {
        return expense.getValue();
    }

    public void setExpense(String expense) {
        this.expense.setValue(expense);
    }

    @Column(name = "amount")
    public Double getAmount() {
        return amount.doubleValue();
    }

    public void setAmount(Double amount) {
        this.amount.setValue(amount);
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "budget_id")
    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    @Column(name = "completed")
    public Boolean getCompleted() {
        return completed.getValue();
    }

    public void setCompleted(Boolean completed) {
        this.completed.setValue(completed);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    public Date getDate() {
        return date;
    }

    public BooleanProperty completedProperty() {
        return completed;
    }

    public StringProperty expenseProperty() {
        return expense;
    }

    public DoubleProperty amountProperty() {
        return amount;
    }
}
