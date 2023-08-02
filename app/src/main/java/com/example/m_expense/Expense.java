package com.example.m_expense;

public class Expense {

    private final String expense_type;
    private final int expense_amount;
    private final String expense_time;
    private final String comments;
    private int id;
    private int expense_icon;



    public Expense(String expense_type, int expense_amount, String expense_time, String comments, int id, int expense_icon) {
        this.expense_type = expense_type;
        this.expense_amount = expense_amount;
        this.expense_time = expense_time;
        this.comments = comments;
        this.id = id;
        this.expense_icon = expense_icon;
    }
    public int getExpense_icon() {return expense_icon;}

    public void setExpense_icon(int expense_icon) {this.expense_icon = expense_icon;
    }
    public String getExpense_type() {
        return expense_type;
    }

    public int getExpense_amount() {
        return expense_amount;
    }


    public String getExpense_time() {
        return expense_time;
    }


    public String getComments() {
        return comments;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
