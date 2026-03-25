package com.campaignx.campaignx_agent.util;

import com.campaignx.campaignx_agent.model.Customer;
import com.campaignx.campaignx_agent.model.CustomerCohortResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class CsvCustomerLoader {

    public static CustomerCohortResponse loadCustomers() {

        List<Customer> customers = new ArrayList<>();

        try {

            BufferedReader br =
                    new BufferedReader(new FileReader("customer_cohort_5000_v2.csv"));

            String headerLine = br.readLine();

            String[] headers = headerLine.split(",");

            Map<String,Integer> index = new HashMap<>();

            for(int i=0;i<headers.length;i++){
                index.put(headers[i].trim().toLowerCase(), i);
            }

            String line;

            while((line = br.readLine()) != null){

                String[] col = line.split(",");

                Customer c = new Customer();

                c.setCustomer_id(col[index.get("customer_id")].trim());
                c.setGender(col[index.get("gender")].trim());
                c.setCity(col[index.get("city")].trim());

                c.setAge(
                        Integer.parseInt(
                                col[index.get("age")].trim()
                        )
                );

                c.setMonthlyIncome(
                        Integer.parseInt(
                                col[index.get("monthly_income")].trim()
                        )
                );

                c.setCreditScore(
                        Integer.parseInt(
                                col[index.get("credit score")].trim()
                        )
                );

                customers.add(c);
            }

            br.close();

            System.out.println("✅ CSV customers loaded = " + customers.size());

        } catch (Exception e){

            System.out.println("❌ CSV LOAD FAILED");
            e.printStackTrace();
        }

        CustomerCohortResponse res = new CustomerCohortResponse();
        res.setData(customers);

        return res;
    }
}