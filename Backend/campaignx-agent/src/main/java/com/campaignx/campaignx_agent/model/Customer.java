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

    // ===== GETTERS =====

    public String getCustomer_id() {
        return customer_id;
    }

    public String getCity() {
        return city;
    }

    public String getGender() {
        return gender;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public Integer getMonthlyIncome() {
        return monthlyIncome;
    }

    public Integer getAge() {
        return age;
    }

    // ===== SETTERS =====

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public void setMonthlyIncome(Integer monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
