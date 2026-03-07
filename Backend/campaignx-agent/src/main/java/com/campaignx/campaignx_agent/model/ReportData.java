package com.campaignx.campaignx_agent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReportData {

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("body")
    private String body;

    @JsonProperty("EO")
    private String EO;

    @JsonProperty("EC")
    private String EC;
}