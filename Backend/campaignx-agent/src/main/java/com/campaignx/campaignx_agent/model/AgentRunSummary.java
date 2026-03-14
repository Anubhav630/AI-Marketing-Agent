package com.campaignx.campaignx_agent.model;

import lombok.Data;

@Data
public class AgentRunSummary {

    private String firstCampaignId;
    private double firstOpenRate;
    private double firstClickRate;

    private boolean optimized;

    private String secondCampaignId;
    private double secondOpenRate;
    private double secondClickRate;

    private String improvedSubject;

    // getters setters
}