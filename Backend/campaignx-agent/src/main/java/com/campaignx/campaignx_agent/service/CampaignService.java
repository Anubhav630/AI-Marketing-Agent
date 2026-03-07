package com.campaignx.campaignx_agent.service;

import com.campaignx.campaignx_agent.model.CampaignReportResponse;
import com.campaignx.campaignx_agent.model.CustomerCohortResponse;
import com.campaignx.campaignx_agent.model.SendCampaignRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public CustomerCohortResponse getCustomers() {

        String url = baseUrl + "/get_customer_cohort";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<CustomerCohortResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        CustomerCohortResponse.class);

        return response.getBody();
    }

    public String sendCampaign(SendCampaignRequest request) {

        String url = baseUrl + "/send_campaign";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SendCampaignRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        return response.getBody();
    }

    public CampaignReportResponse getCampaignReport(String campaignId) {

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

        return response.getBody();
    }
}
