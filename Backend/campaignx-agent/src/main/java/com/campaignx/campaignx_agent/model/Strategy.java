package com.campaignx.campaignx_agent.model;

import lombok.Data;

@Data
public class Strategy {

    private String targetSegment;
    private String goal;
    private String tone;

    private String city;
    private Integer minCreditScore;
    private Integer minIncome;
    private String Gender;

}
