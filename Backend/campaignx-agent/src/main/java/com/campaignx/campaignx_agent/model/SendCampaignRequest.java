package com.campaignx.campaignx_agent.model;

import lombok.Data;
import java.util.List;

@Data
public class SendCampaignRequest {

    private String subject;
    private String body;
    private List<String> list_customer_ids;
    private String send_time;
}