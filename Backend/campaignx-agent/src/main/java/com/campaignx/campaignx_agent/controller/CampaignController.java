package com.campaignx.campaignx_agent.controller;

import com.campaignx.campaignx_agent.model.*;
import com.campaignx.campaignx_agent.service.AgentService;
import com.campaignx.campaignx_agent.service.CampaignService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/campaign")
public class CampaignController {

    private final CampaignService campaignService;
    private final AgentService agentService;

    public CampaignController(CampaignService campaignService,
                              AgentService agentService) {
        this.campaignService = campaignService;
        this.agentService = agentService;
    }

    // existing endpoint
    @GetMapping("/customers")
    public CustomerCohortResponse getCustomers() {
        return campaignService.getCustomers();
    }

    // NEW AGENT ENDPOINT
    @PostMapping("/plan")
    public CampaignPlan createCampaignPlan(@RequestBody CampaignBrief request) {

        return agentService.generateCampaignPlan(request.getBrief());
    }

    @PostMapping("/send")
    public String sendCampaign(@RequestBody CampaignPlan plan) {

        return agentService.launchCampaign(plan);
    }

    @GetMapping("/report")
    public CampaignReportResponse getReport(@RequestParam String campaign_id) {

        return agentService.fetchCampaignReport(campaign_id);
    }

    @PostMapping("/optimize")
    public EmailContent optimizeCampaign(@RequestParam String campaign_id) {

        CampaignReportResponse report =
                agentService.fetchCampaignReport(campaign_id);

        CampaignMetrics metrics =
                agentService.analyzeReport(report.getData());

        return agentService.generateImprovedEmail(
                metrics,
                report.getData().get(0).getSubject(),
                report.getData().get(0).getBody()
        );
    }

    @PostMapping("/approve")
    public String approveCampaign(@RequestBody CampaignPlan plan) {

        return agentService.launchCampaign(plan);
    }

    @PostMapping("/auto-run")
    public AgentRunSummary autoRun(@RequestBody CampaignBrief request) {

        return agentService.runOptimizedCampaign(
                request.getBrief()
        );
    }

    @PostMapping("/run-agent")
    public AgentRunSummary runAgent(@RequestBody CampaignBrief req) {
        return agentService.runOptimizedCampaign(req.getBrief());
    }
}