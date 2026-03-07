package com.campaignx.campaignx_agent.model;

import lombok.Data;
import java.util.List;

@Data
public class CampaignReportResponse {

    private List<ReportData> data;
    private int total_rows;
    private int response_code;
    private String message;
    private String campaign_id;
}