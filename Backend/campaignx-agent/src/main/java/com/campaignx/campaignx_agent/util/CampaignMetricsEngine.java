//package com.campaignx.campaignx_agent.util;
//
//import com.campaignx.campaignx_agent.model.CampaignMetrics;
//import com.campaignx.campaignx_agent.model.Customer;
//
//import java.util.List;
//
//public class CampaignMetricsEngine {
//
//    public static CampaignMetrics calculateMetrics(List<Customer> targets) {
//
//        System.out.println("⚡ Offline Engagement Engine Running");
//
//        int total = targets.size();
//
//        double openScoreSum = 0;
//        double clickScoreSum = 0;
//
//        for(Customer c : targets){
//
//            double openProb = 0.70;   // ⭐ RULEBOOK BASE
//            double clickProb = 0.30;  // ⭐ RULEBOOK BASE
//
//            // ⭐ Income uplift
//            if(c.getMonthlyIncome() > 120000)
//                openProb += 0.05;
//
//            // ⭐ Credit uplift
//            if(c.getCreditScore() > 720)
//                clickProb += 0.05;
//
//            // ⭐ Young digital segment uplift
//            if(c.getAge() < 35)
//                clickProb += 0.03;
//
//            // ⭐ saturation cap
//            if(openProb > 0.90) openProb = 0.90;
//            if(clickProb > 0.60) clickProb = 0.60;
//
//            openScoreSum += openProb;
//            clickScoreSum += (openProb * clickProb);
//        }
//
//        CampaignMetrics m = new CampaignMetrics();
//
//        if(total == 0){
//            m.setOpenRate(0);
//            m.setClickRate(0);
//            return m;
//        }
//
//        double openRate = openScoreSum / total;
//        double clickRate = clickScoreSum / total;
//
//        m.setOpenRate(openRate);
//        m.setClickRate(clickRate);
//
//        System.out.println("📊 Offline Metrics → Open:"
//                + openRate + " Click:" + clickRate);
//
//        return m;
//    }
//}

package com.campaignx.campaignx_agent.util;

import com.campaignx.campaignx_agent.model.CampaignMetrics;
import com.campaignx.campaignx_agent.model.Customer;

import java.util.List;

public class CampaignMetricsEngine {

    public static CampaignMetrics calculateMetrics(List<Customer> targets){

        CampaignMetrics metrics = new CampaignMetrics();

        if(targets == null || targets.isEmpty()){

            System.out.println("⚠ No target customers → default low engagement");

            metrics.setOpenRate(0.30);
            metrics.setClickRate(0.09);
            return metrics;
        }

        double avgIncome = 0;
        double avgCredit = 0;
        double avgAge = 0;

        for(Customer c : targets){

            avgIncome += c.getMonthlyIncome();
            avgCredit += c.getCreditScore();
            avgAge += c.getAge();
        }

        avgIncome /= targets.size();
        avgCredit /= targets.size();
        avgAge /= targets.size();

        // ⭐ SEGMENT QUALITY SCORE MODEL
        double incomeScore = Math.min(avgIncome / 200000.0 , 1.0);
        double creditScore = Math.min(avgCredit / 850.0 , 1.0);
        double ageScore = (avgAge > 30 && avgAge < 55) ? 0.08 : 0.03;

        double segmentScore =
                (incomeScore * 0.45) +
                        (creditScore * 0.45) +
                        ageScore;

        // ⭐ EMAIL FATIGUE PENALTY (large cohort lower engagement)
        double sizePenalty = Math.min(targets.size() / 2000.0 , 0.15);

        // ⭐ FINAL OPEN RATE MODEL
        double openRate =
                0.35 +
                        (segmentScore * 0.40) -
                        sizePenalty;

        // clamp realistic bounds
        if(openRate < 0.12) openRate = 0.12;
        if(openRate > 0.82) openRate = 0.82;

        // ⭐ RULEBOOK LOGIC → CLICK = 30% OF OPEN
        double clickRate = openRate * 0.30;

        metrics.setOpenRate(openRate);
        metrics.setClickRate(clickRate);

        System.out.println("📊 Offline Metrics Engine → " +
                "AvgIncome:" + (int)avgIncome +
                " AvgCredit:" + (int)avgCredit +
                " Cohort:" + targets.size());

        return metrics;
    }
}