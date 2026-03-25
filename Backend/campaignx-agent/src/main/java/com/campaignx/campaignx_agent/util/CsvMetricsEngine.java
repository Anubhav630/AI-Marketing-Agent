package com.campaignx.campaignx_agent.util;

import com.campaignx.campaignx_agent.model.CampaignMetrics;
import com.campaignx.campaignx_agent.model.Customer;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CsvMetricsEngine {

    public static CampaignMetrics calculateMetrics(
            List<String> targetIds,
            List<Customer> allCustomers) {

        System.out.println("⚡ Using CSV Metrics Engine");

        Set<String> idSet = new HashSet<>(targetIds);

        int total = 0;
        int opened = 0;
        int clicked = 0;

        Random r = new Random();

        for(Customer c : allCustomers){

            if(!idSet.contains(c.getCustomer_id()))
                continue;

            total++;

            // ⭐ engagement probability logic
            double openProb = 0.25;
            double clickProb = 0.08;

            // income boost
            if(c.getMonthlyIncome() > 100000)
                openProb += 0.15;

            // credit boost
            if(c.getCreditScore() > 700)
                clickProb += 0.07;

            // premium segment boost
            if(c.getMonthlyIncome() > 150000 && c.getCreditScore() > 720){
                openProb += 0.10;
                clickProb += 0.08;
            }

            if(r.nextDouble() < openProb){
                opened++;

                if(r.nextDouble() < clickProb)
                    clicked++;
            }
        }

        CampaignMetrics m = new CampaignMetrics();

        if(total == 0){
            m.setOpenRate(0);
            m.setClickRate(0);
            return m;
        }

        m.setOpenRate((double) opened / total);
        m.setClickRate((double) clicked / total);

        System.out.println("📊 CSV Engine Metrics → Open:"
                + m.getOpenRate() + " Click:" + m.getClickRate());

        return m;
    }
}