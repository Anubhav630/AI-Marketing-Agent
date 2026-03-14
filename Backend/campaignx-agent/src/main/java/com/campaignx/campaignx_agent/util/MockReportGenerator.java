package com.campaignx.campaignx_agent.util;

import com.campaignx.campaignx_agent.model.CampaignReportResponse;
import com.campaignx.campaignx_agent.model.ReportData;

import java.util.ArrayList;
import java.util.List;

public class MockReportGenerator {

    public static CampaignReportResponse generateMockReport(){

        List<ReportData> data = new ArrayList<>();

        for(int i=0;i<50;i++){

            ReportData r = new ReportData();

            r.setEO(Math.random() > 0.4 ? "Y" : "N");
            r.setEC(Math.random() > 0.7 ? "Y" : "N");

            data.add(r);
        }

        CampaignReportResponse res = new CampaignReportResponse();
        res.setData(data);

        return res;
    }
}

