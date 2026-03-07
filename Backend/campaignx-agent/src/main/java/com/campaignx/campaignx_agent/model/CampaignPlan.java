package com.campaignx.campaignx_agent.model;

import lombok.Data;

import java.util.List;

@Data
public class CampaignPlan {

    private String subject;
    private String body;
    private List<String> customerIds;

}
