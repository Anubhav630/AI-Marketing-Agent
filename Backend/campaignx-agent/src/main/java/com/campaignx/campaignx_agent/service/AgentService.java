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
//        System.out.println("Agent Strategy → "
//                + strategy.getCity() + " | "
//                + strategy.getGender() + " | "
//                + strategy.getMinIncome() + " | "
//                + strategy.getMinCreditScore());
//
//        CustomerCohortResponse cohort = campaignService.getCustomers();
//
//        List<String> targetCustomers =
//                segmentCustomers(cohort.getData(), strategy);
//
//        if (targetCustomers == null || targetCustomers.isEmpty()) {
//
//            System.out.println("⚠ No customers → fallback first 50");
//
//            targetCustomers = new ArrayList<>();
//
//            for (Customer c : cohort.getData()) {
//                targetCustomers.add(c.getCustomer_id());
//                if (targetCustomers.size() >= 50)
//                    break;
//            }
//        }
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
//        // STEP 1 — strict filtering
//        for (Customer c : customers) {
//
//            boolean match = true;
//
//            if(strategy.getCity() != null &&
//                    !strategy.getCity().equalsIgnoreCase(c.getCity()))
//                match = false;
//
//            if(strategy.getGender() != null &&
//                    !strategy.getGender().equalsIgnoreCase(c.getGender()))
//                match = false;
//
//            if(strategy.getMinIncome() != null &&
//                    c.getMonthlyIncome() < strategy.getMinIncome())
//                match = false;
//
//            if(strategy.getMinCreditScore() != null &&
//                    c.getCreditScore() < strategy.getMinCreditScore())
//                match = false;
//
//            if(match)
//                selected.add(c.getCustomer_id());
//        }
//
//        // STEP 2 — if too few customers → relax credit score
//        if(selected.size() < 10 && strategy.getMinCreditScore() != null){
//
//            System.out.println("⚡ Relaxing credit score filter");
//
//            selected.clear();
//
//            for (Customer c : customers) {
//
//                boolean match = true;
//
//                if(strategy.getCity() != null &&
//                        !strategy.getCity().equalsIgnoreCase(c.getCity()))
//                    match = false;
//
//                if(strategy.getMinIncome() != null &&
//                        c.getMonthlyIncome() < strategy.getMinIncome())
//                    match = false;
//
//                if(match)
//                    selected.add(c.getCustomer_id());
//            }
//        }
//
//        // STEP 3 — if still few → relax income also
//        if(selected.size() < 10){
//
//            System.out.println("⚡ Relaxing income filter");
//
//            selected.clear();
//
//            for (Customer c : customers) {
//
//                if(strategy.getCity() == null ||
//                        strategy.getCity().equalsIgnoreCase(c.getCity()))
//                    selected.add(c.getCustomer_id());
//            }
//        }
//
//        // STEP 4 — final fallback
//        if(selected.isEmpty()){
//
//            System.out.println("⚠ Final fallback first 50 customers");
//
//            for(Customer c : customers){
//                selected.add(c.getCustomer_id());
//                if(selected.size() >= 50)
//                    break;
//            }
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
//        return campaignService.getCampaignReport(campaignId);
//    }
//
//    // Analyze report metrics
//    public CampaignMetrics analyzeReport(List<ReportData> reportData) {
//
//        CampaignMetrics metrics = new CampaignMetrics();
//
//        if (reportData == null || reportData.isEmpty()) {
//            System.out.println("⚠ Empty report → using mock metrics");
//
//            metrics.setOpenRate(0.18);
//            metrics.setClickRate(0.09);
//            return metrics;
//        }
//
//        int total = reportData.size();
//        int opened = 0;
//        int clicked = 0;
//
//        for (ReportData r : reportData) {
//
//            if ("Y".equalsIgnoreCase(r.getEO()))
//                opened++;
//
//            if ("Y".equalsIgnoreCase(r.getEC()))
//                clicked++;
//        }
//
//        metrics.setOpenRate((double) opened / total);
//        metrics.setClickRate((double) clicked / total);
//
//        return metrics;
//    }
//
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
//    public AgentRunSummary runOptimizedCampaign(String brief) {
//
//        System.out.println("🚀 AI AGENT LOOP STARTED");
//
//        AgentRunSummary summary = new AgentRunSummary();
//
//        CampaignPlan plan = generateCampaignPlan(brief);
//
//        System.out.println("📤 Launching first campaign");
//
//        String firstCampaignId = launchCampaign(plan);
//
//        try {
//            Thread.sleep(4000);
//        } catch (Exception e) {}
//
//        summary.setFirstCampaignId(firstCampaignId);
//
//        try {
//            Thread.sleep(10000); // ⭐ VERY IMPORTANT wait for campaign execution
//        } catch (Exception e) {}
//
//        CampaignReportResponse report =
//                fetchCampaignReport(firstCampaignId);
//
//        CampaignMetrics metrics =
//                analyzeReport(report.getData());
//
//        System.out.println("📊 First Campaign Metrics → Open: "
//                + metrics.getOpenRate()
//                + " Click: "
//                + metrics.getClickRate());
//
//        summary.setFirstOpenRate(metrics.getOpenRate());
//        summary.setFirstClickRate(metrics.getClickRate());
//
//        // ⭐ Optimization trigger condition
//        if(metrics.getClickRate() < 0.15){
//
//            System.out.println("⚡ Optimization triggered");
//
//            EmailContent improved =
//                    generateImprovedEmail(
//                            metrics,
//                            plan.getSubject(),
//                            plan.getBody()
//                    );
//
//            summary.setImprovedSubject(improved.getSubject());
//
//            CampaignPlan improvedPlan = new CampaignPlan();
//            improvedPlan.setSubject(improved.getSubject());
//            improvedPlan.setBody(improved.getBody());
//            improvedPlan.setCustomerIds(plan.getCustomerIds());
//
//            System.out.println("📤 Launching optimized campaign");
//
//            String secondCampaignId =
//                    launchCampaign(improvedPlan);
//
//            summary.setOptimized(true);
//            summary.setSecondCampaignId(secondCampaignId);
//
//            try {
//                Thread.sleep(10000); // ⭐ wait again
//            } catch (Exception e) {}
//
//            CampaignReportResponse report2 =
//                    fetchCampaignReport(secondCampaignId);
//
//            CampaignMetrics metrics2 =
//                    analyzeReport(report2.getData());
//
//            System.out.println("📊 Optimized Campaign Metrics → Open: "
//                    + metrics2.getOpenRate()
//                    + " Click: "
//                    + metrics2.getClickRate());
//
//            summary.setSecondOpenRate(metrics2.getOpenRate());
//            summary.setSecondClickRate(metrics2.getClickRate());
//
//        } else {
//
//            System.out.println("✅ Optimization not required");
//            summary.setOptimized(false);
//        }
//
//        System.out.println("🏁 AI AGENT LOOP FINISHED");
//
//        return summary;
//    }
//
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
        List<Customer> customers = cohort.getData();

        String briefLower = campaignBrief.toLowerCase();

        // ⭐ CITY intelligent fallback
        if(strategy.getCity() == null && customers != null){

            for(Customer c : customers){

                if(c.getCity() != null &&
                        briefLower.contains(c.getCity().toLowerCase())){

                    strategy.setCity(c.getCity());
                    System.out.println("⚡ City auto-detected → " + c.getCity());
                    break;
                }
            }
        }

        // ⭐ GENDER fallback
        if(strategy.getGender() == null){

            if(briefLower.contains("female") || briefLower.contains("women"))
                strategy.setGender("Female");

            else if(briefLower.contains("male") || briefLower.contains("men"))
                strategy.setGender("Male");
        }

        // ⭐ INCOME fallback
        if(strategy.getMinIncome() == null){

            if(briefLower.contains("high income") ||
                    briefLower.contains("affluent") ||
                    briefLower.contains("premium")){

                strategy.setMinIncome(150000);
            }
            else if(briefLower.contains("middle income")){
                strategy.setMinIncome(80000);
            }
        }

        // ⭐ CREDIT SCORE fallback
        if(strategy.getMinCreditScore() == null){

            if(briefLower.contains("premium") ||
                    briefLower.contains("elite") ||
                    briefLower.contains("luxury")){

                strategy.setMinCreditScore(680);
            }
            else if(briefLower.contains("good credit")){
                strategy.setMinCreditScore(620);
            }
        }

        System.out.println("🎯 Final Strategy → "
                + strategy.getCity() + " | "
                + strategy.getGender() + " | "
                + strategy.getMinIncome() + " | "
                + strategy.getMinCreditScore());

        List<String> targetCustomers =
                segmentCustomers(customers, strategy);

        if (targetCustomers == null || targetCustomers.isEmpty()) {

            System.out.println("⚠ No customers → fallback first 50");

            targetCustomers = new ArrayList<>();

            for (Customer c : customers) {
                targetCustomers.add(c.getCustomer_id());
                if (targetCustomers.size() >= 50)
                    break;
            }
        }

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

        if(strategy.getCity() == null &&
                strategy.getGender() == null &&
                strategy.getMinIncome() == null &&
                strategy.getMinCreditScore() == null){

            System.out.println("⚠ Strategy empty → selecting TOP 50 smart fallback");

            List<String> top = new ArrayList<>();

            for(Customer c : customers){
                if(c.getCreditScore() > 700 && c.getMonthlyIncome() > 100000){
                    top.add(c.getCustomer_id());
                }
                if(top.size() >= 50) break;
            }

            return top;
        }

        List<String> selected = new ArrayList<>();

        String city = strategy.getCity();
        String gender = strategy.getGender();

        if(city != null) city = city.trim().toLowerCase();
        if(gender != null) gender = gender.trim().toLowerCase();

        for (Customer c : customers) {

            boolean match = true;

            if(city != null &&
                    !city.equals(c.getCity().toLowerCase()))
                match = false;

            if(gender != null &&
                    !gender.equals(c.getGender().toLowerCase()))
                match = false;

            if(strategy.getMinIncome() != null &&
                    c.getMonthlyIncome() < strategy.getMinIncome())
                match = false;

            if(strategy.getMinCreditScore() != null &&
                    c.getCreditScore() < strategy.getMinCreditScore())
                match = false;

            if(match)
                selected.add(c.getCustomer_id());
        }

        System.out.println("🎯 STRICT FILTER customers = " + selected.size());

        // ⭐ RELAX CREDIT
        if(selected.size() < 20 && strategy.getMinCreditScore() != null){

            System.out.println("⚡ Relax credit filter");

            selected.clear();

            for (Customer c : customers) {

                boolean match = true;

                if(city != null &&
                        !city.equals(c.getCity().toLowerCase()))
                    match = false;

                if(strategy.getMinIncome() != null &&
                        c.getMonthlyIncome() < strategy.getMinIncome())
                    match = false;

                if(match)
                    selected.add(c.getCustomer_id());
            }
        }

        System.out.println("🎯 AFTER CREDIT RELAX = " + selected.size());

        // ⭐ RELAX INCOME
        if(selected.size() < 20){

            System.out.println("⚡ Relax income filter");

            selected.clear();

            for (Customer c : customers) {

                if(city == null ||
                        city.equals(c.getCity().toLowerCase()))
                    selected.add(c.getCustomer_id());
            }
        }

        System.out.println("🎯 AFTER INCOME RELAX = " + selected.size());

        // ⭐ FINAL FALLBACK
        if(selected.isEmpty()){

            System.out.println("⚠ FINAL fallback 50");

            for(Customer c : customers){
                selected.add(c.getCustomer_id());
                if(selected.size() >= 50)
                    break;
            }
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

        CampaignMetrics metrics = new CampaignMetrics();

        if (reportData == null || reportData.isEmpty()) {
            System.out.println("⚠ Empty report → using mock metrics");

            metrics.setOpenRate(0.18);
            metrics.setClickRate(0.09);
            return metrics;
        }

        int total = reportData.size();
        int opened = 0;
        int clicked = 0;

        for (ReportData r : reportData) {

            if ("Y".equalsIgnoreCase(r.getEO()))
                opened++;

            if ("Y".equalsIgnoreCase(r.getEC()))
                clicked++;
        }

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
    public AgentRunSummary runOptimizedCampaign(String brief) {

        System.out.println("🚀 AI AGENT LOOP STARTED");

        AgentRunSummary summary = new AgentRunSummary();

        CampaignPlan plan = generateCampaignPlan(brief);

        System.out.println("📤 Launching first campaign");

        String firstCampaignId = launchCampaign(plan);

        try {
            Thread.sleep(4000);
        } catch (Exception e) {}

        summary.setFirstCampaignId(firstCampaignId);

        try {
            Thread.sleep(10000); // ⭐ VERY IMPORTANT wait for campaign execution
        } catch (Exception e) {}

        CampaignReportResponse report =
                fetchCampaignReport(firstCampaignId);

        CampaignMetrics metrics =
                analyzeReport(report.getData());

        System.out.println("📊 First Campaign Metrics → Open: "
                + metrics.getOpenRate()
                + " Click: "
                + metrics.getClickRate());

        summary.setFirstOpenRate(metrics.getOpenRate());
        summary.setFirstClickRate(metrics.getClickRate());

        // ⭐ Optimization trigger condition
        if(metrics.getClickRate() < 0.49){

            System.out.println("⚡ Optimization triggered");

            EmailContent improved =
                    generateImprovedEmail(
                            metrics,
                            plan.getSubject(),
                            plan.getBody()
                    );

            summary.setImprovedSubject(improved.getSubject());

            CampaignPlan improvedPlan = new CampaignPlan();
            improvedPlan.setSubject(improved.getSubject());
            improvedPlan.setBody(improved.getBody());
            improvedPlan.setCustomerIds(plan.getCustomerIds());

            System.out.println("📤 Launching optimized campaign");

            String secondCampaignId =
                    launchCampaign(improvedPlan);

            summary.setOptimized(true);
            summary.setSecondCampaignId(secondCampaignId);

            try {
                Thread.sleep(10000); // ⭐ wait again
            } catch (Exception e) {}

            CampaignReportResponse report2 =
                    fetchCampaignReport(secondCampaignId);

            CampaignMetrics metrics2 =
                    analyzeReport(report2.getData());

            System.out.println("📊 Optimized Campaign Metrics → Open: "
                    + metrics2.getOpenRate()
                    + " Click: "
                    + metrics2.getClickRate());

            summary.setSecondOpenRate(metrics2.getOpenRate());
            summary.setSecondClickRate(metrics2.getClickRate());

        } else {

            System.out.println("✅ Optimization not required");
            summary.setOptimized(false);
        }

        System.out.println("🏁 AI AGENT LOOP FINISHED");

        return summary;
    }

}