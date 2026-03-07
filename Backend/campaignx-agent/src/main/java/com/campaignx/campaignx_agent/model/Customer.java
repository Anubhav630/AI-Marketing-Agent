package com.campaignx.campaignx_agent.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {

    @JsonProperty("customer_id")
    private String customer_id;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("Credit score")
    private int creditScore;

    @JsonProperty("Monthly_Income")
    private int monthlyIncome;

    @JsonProperty("Age")
    private int age;

    // getters

    public String getCustomer_id() {
        return customer_id;
    }

    public String getCity() {
        return city;
    }

    public String getGender() {
        return gender;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public int getMonthlyIncome() {
        return monthlyIncome;
    }

    public int getAge() {
        return age;
    }
}