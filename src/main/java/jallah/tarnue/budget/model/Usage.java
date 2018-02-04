package jallah.tarnue.budget.model;

import javafx.beans.property.*;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "Usage")
public class Usage {
    private Date date;
    private Budget budget;
    private IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private StringProperty usage = new SimpleStringProperty(this, "usage");
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

    @Column(name = "usage")
    public String getUsage() {
        return usage.getValue();
    }

    public void setUsage(String usage) {
        this.usage.setValue(usage);
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

    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
