package com.campaignx.campaignx_agent.service;

import com.campaignx.campaignx_agent.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LLMService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();


    public String callLLM(String prompt) {

        Map<String,Object> request = new HashMap<>();

        request.put("model", model);

        List<Map<String,String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role","user",
                "content",prompt
        ));

        request.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "http://localhost");
        headers.set("X-Title", "CampaignX-Agent");

        HttpEntity<Map<String,Object>> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(apiUrl, entity, Map.class);

        Map choice = (Map)((List)response.getBody().get("choices")).get(0);
        Map message = (Map)choice.get("message");

        return (String) message.get("content");
    }


    public Strategy interpretBrief(String brief) {

        String prompt = """
    You are an AI marketing planner.

    Extract customer targeting filters from the campaign brief.

    Return STRICT JSON.

    Format:

    {
      "city": "city name or null",
      "gender": "Male/Female or null",
      "minIncome": number or null,
      "minCreditScore": number or null
    }

    Campaign brief:
    """ + brief;

        String response = callLLM(prompt);

        Strategy strategy = new Strategy();

        try {

            ObjectMapper mapper = new ObjectMapper();

            response = response.replace("```json","")
                    .replace("```","")
                    .trim();

            int start = response.indexOf("{");
            int end = response.lastIndexOf("}") + 1;

            String json = response.substring(start, end);

            Map<String,Object> result =
                    mapper.readValue(json, Map.class);

            strategy.setCity((String) result.get("city"));
            strategy.setGender((String) result.get("gender"));

            if(result.get("minIncome") != null)
                strategy.setMinIncome((Integer) result.get("minIncome"));

            if(result.get("minCreditScore") != null)
                strategy.setMinCreditScore((Integer) result.get("minCreditScore"));

        } catch(Exception e) {
            e.printStackTrace();
        }

        return strategy;
    }

    public EmailContent generateEmail(String brief, Strategy strategy) {

        String prompt = """
        You are an AI marketing agent.

        Generate an email campaign.

        IMPORTANT RULES:
        - Return ONLY valid JSON
        - Do NOT explain anything
        - Do NOT use markdown
        - Do NOT add notes

        Format strictly:

        {
          "subject": "email subject",
          "body": "email body"
        }

        Campaign brief:
        """ + brief;

        String response = callLLM(prompt);

        EmailContent email = new EmailContent();

        try {

            ObjectMapper mapper = new ObjectMapper();

            // Remove markdown if LLM adds it
            response = response.replace("```json", "")
                    .replace("```", "")
                    .trim();

            // Find JSON boundaries
            int start = response.indexOf("{");
            int end = response.lastIndexOf("}") + 1;

            if(start == -1 || end == -1) {
                throw new RuntimeException("No JSON found in LLM response");
            }

            String json = response.substring(start, end);

            Map<String, Object> result =
                    mapper.readValue(json, Map.class);

            String subject = result.get("subject") != null
                    ? result.get("subject").toString()
                    : "Campaign Offer";

            String body = result.get("body") != null
                    ? result.get("body").toString()
                    : response;

            email.setSubject(subject);
            email.setBody(body);

        } catch (Exception e) {

            // fallback if parsing fails
            email.setSubject("Campaign Offer");
            email.setBody(response);
        }

        return email;
    }



    public String optimizeCampaign(CampaignMetrics metrics){

        String prompt = """
        A marketing campaign produced the following results:

        Open Rate: """ + metrics.getOpenRate() + """
        Click Rate: """ + metrics.getClickRate() + """

        Suggest improvements to increase engagement.
        """;

        return callLLM(prompt);
    }
}