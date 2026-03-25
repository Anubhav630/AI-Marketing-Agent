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

    // ⭐ Fetch dataset (for dashboard table preview)
    @GetMapping("/customers")
    public CustomerCohortResponse getCustomers() {
        return campaignService.getCustomers();
    }

    // ⭐ STEP 1 — AI generates targeting + email preview
    @PostMapping("/plan")
    public CampaignPlan createCampaignPlan(@RequestBody CampaignBrief request) {
        return agentService.generateCampaignPlan(request.getBrief());
    }

    // ⭐ STEP 2 — Human approves & campaign launched
    @PostMapping("/approve")
    public String approveCampaign(@RequestBody CampaignPlan plan) {
        return agentService.launchCampaign(plan);
    }

    // ⭐ STEP 3 — FULL AGENT LOOP (auto optimize + metrics engine)
    @PostMapping("/run-agent")
    public AgentRunSummary runAgent(@RequestBody CampaignBrief req) {
        return agentService.runOptimizedCampaign(req.getBrief());
    }

}