package com.campaignx.campaignx_agent.service;

import com.campaignx.campaignx_agent.model.*;
import com.campaignx.campaignx_agent.util.CsvCustomerLoader;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CampaignService {

    // ⭐ OFFLINE CUSTOMER SOURCE
    public CustomerCohortResponse getCustomers() {

        System.out.println("📂 Loading customers from CSV dataset");
        return CsvCustomerLoader.loadCustomers();
    }

    // ⭐ FULLY MOCK CAMPAIGN SEND
    public String sendCampaign(SendCampaignRequest request) {

        String campaignId = "MOCK_CAMPAIGN_" + UUID.randomUUID();

        System.out.println("✅ Mock Campaign Launched → " + campaignId);
        System.out.println("📊 Target Customers Count → "
                + request.getList_customer_ids().size());

        return campaignId;
    }

    // ⭐ NO REAL REPORT API NOW
    public CampaignReportResponse getCampaignReport(String campaignId) {

        System.out.println("⚡ Offline Mode → Report handled by Metrics Engine");
        return null;
    }
}