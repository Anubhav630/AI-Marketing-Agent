//package com.campaignx.campaignx_agent.service;
//
//import com.campaignx.campaignx_agent.model.*;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class AgentService {
//
//    private final CampaignService campaignService;
//    private final LLMService llmService;
//
//    public AgentService(CampaignService campaignService, LLMService llmService) {
//        this.campaignService = campaignService;
//        this.llmService = llmService;
//    }
//
//    // Generate campaign plan
//    public CampaignPlan generateCampaignPlan(String campaignBrief) {
//
//        Strategy strategy = llmService.interpretBrief(campaignBrief);
//
//        CustomerCohortResponse cohort = campaignService.getCustomers();
//
//        List<String> targetCustomers =
//                segmentCustomers(cohort.getData(), strategy);
//
//        EmailContent email =
//                llmService.generateEmail(campaignBrief, strategy);
//
//        CampaignPlan plan = new CampaignPlan();
//        plan.setSubject(email.getSubject());
//        plan.setBody(email.getBody());
//        plan.setCustomerIds(targetCustomers);
//
//        return plan;
//    }
//
//    // Segment customers
//    private List<String> segmentCustomers(List<Customer> customers,
//                                          Strategy strategy) {
//
//        List<String> selected = new ArrayList<>();
//
//        for (Customer c : customers) {
//
//            boolean match = true;
//
//            if(strategy.getCity() != null &&
//                    !strategy.getCity().equalsIgnoreCase(c.getCity()))
//                match = false;
//
//            if(strategy.getMinCreditScore() != null &&
//                    c.getCreditScore() < strategy.getMinCreditScore())
//                match = false;
//
//            if(strategy.getMinIncome() != null &&
//                    c.getMonthlyIncome() < strategy.getMinIncome())
//                match = false;
//
//            if(strategy.getGender() != null &&
//                    !strategy.getGender().equalsIgnoreCase(c.getGender()))
//                match = false;
//
//            if(match)
//                selected.add(c.getCustomer_id());
//
//        }
//
//        return selected;
//    }
//
//    // Launch campaign
//    public String launchCampaign(CampaignPlan plan) {
//
//        SendCampaignRequest request = new SendCampaignRequest();
//
//        request.setSubject(plan.getSubject());
//        request.setBody(plan.getBody());
//        request.setList_customer_ids(plan.getCustomerIds());
//
//        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(2);
//
//        DateTimeFormatter formatter =
//                DateTimeFormatter.ofPattern("dd:MM:yy HH:mm:ss");
//
//        request.setSend_time(futureTime.format(formatter));
//
//        return campaignService.sendCampaign(request);
//    }
//
//    // Fetch report
//    public CampaignReportResponse fetchCampaignReport(String campaignId) {
//
//        return campaignService.getCampaignReport(campaignId);
//    }
//
//    // Analyze report metrics
//    public CampaignMetrics analyzeReport(List<ReportData> reportData) {
//
//        int total = reportData.size();
//        int opened = 0;
//        int clicked = 0;
//
//        for (ReportData r : reportData) {
//
//            if ("Y".equals(r.getEO()))
//                opened++;
//
//            if ("Y".equals(r.getEC()))
//                clicked++;
//        }
//
//        CampaignMetrics metrics = new CampaignMetrics();
//
//        metrics.setOpenRate((double) opened / total);
//        metrics.setClickRate((double) clicked / total);
//
//        return metrics;
//    }
//
//    // Generate improved email using LLM suggestions
//    public EmailContent generateImprovedEmail(CampaignMetrics metrics,
//                                              String originalSubject,
//                                              String originalBody) {
//
//        String suggestion = llmService.optimizeCampaign(metrics);
//
//        EmailContent email = new EmailContent();
//
//        email.setSubject(originalSubject + " | Limited Time Offer!");
//        email.setBody(
//                originalBody +
//                        "\n\nOptimized based on campaign performance:\n\n" +
//                        suggestion
//        );
//
//        return email;
//    }
//}

package com.campaignx.campaignx_agent.service;

import com.campaignx.campaignx_agent.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgentService {

    private final CampaignService campaignService;
    private final LLMService llmService;

    public AgentService(CampaignService campaignService, LLMService llmService) {
        this.campaignService = campaignService;
        this.llmService = llmService;
    }

    // Generate campaign plan
    public CampaignPlan generateCampaignPlan(String campaignBrief) {

        Strategy strategy = llmService.interpretBrief(campaignBrief);

        CustomerCohortResponse cohort = campaignService.getCustomers();

        List<String> targetCustomers =
                segmentCustomers(cohort.getData(), strategy);

        EmailContent email =
                llmService.generateEmail(campaignBrief, strategy);

        CampaignPlan plan = new CampaignPlan();
        plan.setSubject(email.getSubject());
        plan.setBody(email.getBody());
        plan.setCustomerIds(targetCustomers);

        return plan;
    }

    // Segment customers
    private List<String> segmentCustomers(List<Customer> customers,
                                          Strategy strategy) {

        List<String> selected = new ArrayList<>();

        // Check if no filters exist
        boolean noFilters =
                strategy.getCity() == null &&
                        strategy.getGender() == null &&
                        strategy.getMinIncome() == null &&
                        strategy.getMinCreditScore() == null;

        for (Customer c : customers) {

            // If no filters exist → just take first 20 customers
            if (noFilters) {
                selected.add(c.getCustomer_id());
                if (selected.size() >= 20)
                    break;
                continue;
            }

            boolean match = true;

            if (strategy.getCity() != null &&
                    c.getCity() != null &&
                    !strategy.getCity().equalsIgnoreCase(c.getCity()))
                match = false;

            if (strategy.getGender() != null &&
                    c.getGender() != null &&
                    !strategy.getGender().equalsIgnoreCase(c.getGender()))
                match = false;

            if (strategy.getMinIncome() != null &&
                    c.getMonthlyIncome() < strategy.getMinIncome())
                match = false;

            if (strategy.getMinCreditScore() != null &&
                    c.getCreditScore() < strategy.getMinCreditScore())
                match = false;

            if (match) {
                selected.add(c.getCustomer_id());
            }

            if (selected.size() >= 20)
                break;
        }

        return selected;
    }

    // Launch campaign
    public String launchCampaign(CampaignPlan plan) {

        SendCampaignRequest request = new SendCampaignRequest();

        request.setSubject(plan.getSubject());
        request.setBody(plan.getBody());
        request.setList_customer_ids(plan.getCustomerIds());

        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(2);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd:MM:yy HH:mm:ss");

        request.setSend_time(futureTime.format(formatter));

        return campaignService.sendCampaign(request);
    }

    // Fetch report
    public CampaignReportResponse fetchCampaignReport(String campaignId) {
        return campaignService.getCampaignReport(campaignId);
    }

    // Analyze report metrics
    public CampaignMetrics analyzeReport(List<ReportData> reportData) {

        int total = reportData.size();
        int opened = 0;
        int clicked = 0;

        for (ReportData r : reportData) {

            if ("Y".equals(r.getEO()))
                opened++;

            if ("Y".equals(r.getEC()))
                clicked++;
        }

        CampaignMetrics metrics = new CampaignMetrics();

        metrics.setOpenRate((double) opened / total);
        metrics.setClickRate((double) clicked / total);

        return metrics;
    }

    // Generate improved email using LLM suggestions
    public EmailContent generateImprovedEmail(CampaignMetrics metrics,
                                              String originalSubject,
                                              String originalBody) {

        String suggestion = llmService.optimizeCampaign(metrics);

        EmailContent email = new EmailContent();

        email.setSubject(originalSubject + " | Limited Time Offer!");
        email.setBody(
                originalBody +
                        "\n\nOptimized based on campaign performance:\n\n" +
                        suggestion
        );

        return email;
    }
}