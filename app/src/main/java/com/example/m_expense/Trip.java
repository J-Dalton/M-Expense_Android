package com.example.m_expense;

public class Trip {

    private String name;
    private final String destination;
    private final String date;
    private final boolean require_risk;
    private final String description;
    private final int personId;
    private final int flag;
    private final int budget;


    public Trip(String name, String destination, String date, boolean require_risk, String description, int personId, int flag, int budget) {
        this.name = name;
        this.destination = destination;
        this.date = date;
        this.require_risk = require_risk;
        this.description = description;
        this.personId = personId;
        this.flag = flag;
        this.budget = budget;

    }

    public int getFlag() {
        return flag;
    }

    public int getBudget() {
        return budget;
    }

    public int getPersonId() {
        return personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public boolean isRequire_risk() {
        return require_risk;
    }

    public String getDescription() {
        return description;
    }


}
