package com.campaignx.campaignx_agent.util;

import com.campaignx.campaignx_agent.model.Customer;
import com.campaignx.campaignx_agent.model.CustomerCohortResponse;

import java.util.*;

public class MockCustomerGenerator {

    public static CustomerCohortResponse generate() {

        List<Customer> list = new ArrayList<>();
        Random r = new Random();

        String[] cities = {"Mumbai","Delhi","Bangalore","Chennai"};
        String[] genders = {"Male","Female"};

        for(int i=1;i<=5000;i++){

            Customer c = new Customer();
            c.setCustomer_id("CUST" + String.format("%04d", i));
            c.setCity(cities[r.nextInt(cities.length)]);
            c.setGender(genders[r.nextInt(2)]);
            c.setCreditScore(600 + r.nextInt(200));
            c.setMonthlyIncome(30000 + r.nextInt(200000));
            c.setAge(21 + r.nextInt(40));

            list.add(c);
        }

        CustomerCohortResponse res = new CustomerCohortResponse();
        res.setData(list);

        return res;
    }
}
