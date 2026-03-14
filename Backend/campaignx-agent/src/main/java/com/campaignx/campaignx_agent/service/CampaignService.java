package com.campaignx.campaignx_agent.service;

import com.campaignx.campaignx_agent.model.*;
import com.campaignx.campaignx_agent.util.MockReportGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.campaignx.campaignx_agent.util.MockCustomerGenerator;

import java.util.UUID;

@Service
public class CampaignService {

    private final RestTemplate restTemplate;

    @Value("${campaignx.api.base-url}")
    private String baseUrl;

    @Value("${campaignx.api.key}")
    private String apiKey;

    public CampaignService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ================= GET CUSTOMERS =================
    public CustomerCohortResponse getCustomers() {

        try {

            String url = baseUrl + "/get_customer_cohort";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-Key", apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<CustomerCohortResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            CustomerCohortResponse.class
                    );

            System.out.println("✅ Customer API success");

            return response.getBody();

        } catch (Exception ex) {

            System.out.println("⚠ Customer API FAILED → using MOCK customers");

            // ⭐ CORRECT mock fallback
            return MockCustomerGenerator.generate();
        }
    }

    // ================= SEND CAMPAIGN =================
    public String sendCampaign(SendCampaignRequest request) {

        try {

            String url = baseUrl + "/send_campaign";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-Key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SendCampaignRequest> entity =
                    new HttpEntity<>(request, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, entity, String.class);

            System.out.println("✅ Campaign sent successfully");

            return response.getBody();

        } catch (Exception ex) {

            System.out.println("⚠ Campaign API FAILED → simulating campaignId");

            // ⭐ simulate campaign id for LOOP demo
            return "SIM_CAMPAIGN_" + UUID.randomUUID();
        }
    }

    // ================= GET REPORT =================
    public CampaignReportResponse getCampaignReport(String campaignId) {

        try {

            String url = baseUrl + "/get_report?campaign_id=" + campaignId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-Key", apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<CampaignReportResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            CampaignReportResponse.class
                    );

            System.out.println("✅ Report API success");

            return response.getBody();

        } catch (Exception ex) {

            System.out.println("⚠ Report API FAILED → generating MOCK report");

            // ⭐ mock report so LOOP optimization works
            return MockReportGenerator.generateMockReport();
        }
    }
}
