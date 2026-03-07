package com.campaignx.campaignx_agent.model;

import lombok.Data;
import java.util.List;

@Data
public class CustomerCohortResponse {

    private List<Customer> data;
    private int total_count;
}