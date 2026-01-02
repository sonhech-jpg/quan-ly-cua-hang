package model;

import java.sql.Timestamp;
import java.util.List;

public class Invoice {
    private int id;
    private int employeeId;
    private int customerId;
    private Timestamp date;
    private double total;

    public Invoice() {}

    public Invoice(int id, int employeeId, int customerId, Timestamp date, double total) {
        this.id = id;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.date = date;
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
