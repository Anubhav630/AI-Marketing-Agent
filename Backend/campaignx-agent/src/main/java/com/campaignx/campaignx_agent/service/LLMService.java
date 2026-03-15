//package com.campaignx.campaignx_agent.service;
//
//import com.campaignx.campaignx_agent.model.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.util.*;
//
//
//
//@Service
//public class LLMService {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Value("${openrouter.api.key}")
//    private String openRouterApiKey;
//
//    @Value("${openrouter.api.url}")
//    private String openRouterUrl;
//
//    @Value("${openrouter.model}")
//    private String openRouterModel;
//
//
//    public String callLLM(String prompt) {
//
//        int maxRetries = 3;
//
//        for(int attempt = 1; attempt <= maxRetries; attempt++) {
//
//            try {
//
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
//                headers.setBearerAuth(openRouterApiKey);
//
//                Map<String,Object> body = new HashMap<>();
//
//                body.put("model", openRouterModel);
//
//                List<Map<String,String>> messages = new ArrayList<>();
//
//                Map<String,String> msg = new HashMap<>();
//                msg.put("role", "user");
//                msg.put("content", prompt);
//
//                messages.add(msg);
//
//                body.put("messages", messages);
//                body.put("temperature", 0.7);
//                body.put("max_tokens", 300);
//
//                HttpEntity<Map<String,Object>> request =
//                        new HttpEntity<>(body, headers);
//
//                ResponseEntity<Map> response =
//                        restTemplate.postForEntity(openRouterUrl, request, Map.class);
//
//                Map result = response.getBody();
//
//                List choices = (List) result.get("choices");
//                Map choice = (Map) choices.get(0);
//                Map message = (Map) choice.get("message");
//
//                return message.get("content").toString();
//
//            } catch(Exception e) {
//
//                System.out.println("⚠ LLM call failed attempt " + attempt);
//
//                try { Thread.sleep(1500); } catch(Exception ex){}
//            }
//        }
//
//        // ⭐ FINAL FALLBACK RESPONSE
//        System.out.println("🚨 LLM completely failed → using fallback content");
//
//        return """
//    {
//      "subject": "Special Offer for Valued Customers",
//      "body": "Dear Customer, explore our new financial product designed to maximize your returns. Click below to learn more."
//    }
//    """;
//    }
//
//
//    public Strategy interpretBrief(String brief) {
//
//        String prompt = """
//You are an AI banking marketing strategist.
//
//Extract realistic customer targeting filters from campaign brief.
//
//IMPORTANT DATA RULES:
//- Premium / Elite / Luxury → minCreditScore = 680
//- Good credit → minCreditScore = 620
//- High income / affluent → minIncome = 150000
//- Moderate income → minIncome = 80000
//- Women / female → gender = Female
//- Men / male → gender = Male
//- Senior citizens → age > 50 (ignore if age not supported)
//- City mentioned → set city
//- If not clearly implied → return null
//
//Return ONLY valid JSON.
//
//FORMAT:
//
//{
// "city": "string or null",
// "gender": "string or null",
// "minIncome": number or null,
// "minCreditScore": number or null
//}
//
//Campaign Brief:
//""" + brief;
//
//        String response = callLLM(prompt);
//
//        Strategy strategy = new Strategy();
//
//        try {
//
//            ObjectMapper mapper = new ObjectMapper();
//
//            int start = response.indexOf("{");
//            int end = response.lastIndexOf("}") + 1;
//            String json = response.substring(start, end);
//
//            Map<String,Object> result =
//                    mapper.readValue(json, Map.class);
//
//            // CITY
//            Object cityObj = result.get("city");
//            if(cityObj instanceof String)
//                strategy.setCity((String) cityObj);
//            else if(cityObj instanceof List && !((List<?>) cityObj).isEmpty())
//                strategy.setCity(((List<?>) cityObj).get(0).toString());
//
//            // GENDER
//            Object genderObj = result.get("gender");
//            if(genderObj instanceof String)
//                strategy.setGender((String) genderObj);
//            else if(genderObj instanceof List && !((List<?>) genderObj).isEmpty())
//                strategy.setGender(((List<?>) genderObj).get(0).toString());
//
//            // CREDIT SCORE
//            if(result.get("minCreditScore") != null)
//                strategy.setMinCreditScore(
//                        ((Number) result.get("minCreditScore")).intValue()
//                );
//
//            // INCOME
//            if(result.get("minIncome") != null)
//                strategy.setMinIncome(
//                        ((Number) result.get("minIncome")).intValue()
//                );
//
//        } catch(Exception e) {
//            System.out.println("LLM parse failed → fallback strategy");
//        }
//
//        return strategy;
//    }
//
//    public EmailContent generateEmail(String brief, Strategy strategy) {
//
//        String prompt = """
//You are an expert financial marketing copywriter AI.
//
//Write a persuasive, medium-length professional email.
//
//Rules:
//- Premium banking tone
//- Clear benefits
//- Strong call-to-action
//- Not too long (120–180 words)
//- Not generic
//- Personalized to target segment
//
//Return ONLY valid JSON.
//
//FORMAT:
//
//{
// "subject": "short premium catchy subject",
// "body": "professional marketing email"
//}
//
//Campaign Context:
//""" + brief + """
//
//Target Segment Insight:
//City: """ + strategy.getCity() + """
//Gender: """ + strategy.getGender() + """
//Income Segment: """ + strategy.getMinIncome() + """
//Credit Segment: """ + strategy.getMinCreditScore();
//        String response = callLLM(prompt);
//
//        EmailContent email = new EmailContent();
//
//        try {
//
//            ObjectMapper mapper = new ObjectMapper();
//
//            // extract JSON safely
//            int start = response.indexOf("{");
//            int end = response.lastIndexOf("}");
//
//            if(start != -1 && end != -1 && end > start){
//
//                String json = response.substring(start, end + 1);
//
//                Map<String,Object> map =
//                        mapper.readValue(json, Map.class);
//
//                if(map.get("subject") != null)
//                    email.setSubject(map.get("subject").toString());
//
//                if(map.get("body") != null)
//                    email.setBody(map.get("body").toString());
//            }
//            // ⭐ IF STILL EMPTY → USE RAW LLM TEXT AS BODY
//            if(email.getBody() == null){
//
//                email.setSubject("AI Generated Campaign");
//                email.setBody(response);
//            }
//
//        }
//        catch(Exception e){
//
//            System.out.println("⚠ Email JSON parse error → using raw LLM text");
//
//            email.setSubject("AI Generated Campaign");
//            email.setBody(response);
//        }
//
//        return email;
//    }
//
//
//
//    public String optimizeCampaign(CampaignMetrics metrics){
//
//        String prompt = """
//        A marketing campaign produced the following results:
//
//        Open Rate: """ + metrics.getOpenRate() + """
//        Click Rate: """ + metrics.getClickRate() + """
//
//        Suggest improvements to increase engagement.
//        """;
//
//        return callLLM(prompt);
//    }
//}
package com.campaignx.campaignx_agent.service;

import com.campaignx.campaignx_agent.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;



@Service
public class LLMService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Value("${openrouter.api.url}")
    private String openRouterUrl;

    @Value("${openrouter.model}")
    private String openRouterModel;


    public String callLLM(String prompt) {

        int maxRetries = 3;

        for(int attempt = 1; attempt <= maxRetries; attempt++) {

            try {

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(openRouterApiKey);

                Map<String,Object> body = new HashMap<>();

                body.put("model", openRouterModel);

                List<Map<String,String>> messages = new ArrayList<>();

                Map<String,String> msg = new HashMap<>();
                msg.put("role", "user");
                msg.put("content", prompt);

                messages.add(msg);

                body.put("messages", messages);
                body.put("temperature", 0.7);
                body.put("max_tokens", 300);

                HttpEntity<Map<String,Object>> request =
                        new HttpEntity<>(body, headers);

                ResponseEntity<Map> response =
                        restTemplate.postForEntity(openRouterUrl, request, Map.class);

                Map result = response.getBody();

                List choices = (List) result.get("choices");
                Map choice = (Map) choices.get(0);
                Map message = (Map) choice.get("message");

                return message.get("content").toString();

            } catch(Exception e) {

                System.out.println("⚠ LLM call failed attempt " + attempt);

                try { Thread.sleep(1500); } catch(Exception ex){}
            }
        }

        // ⭐ FINAL FALLBACK RESPONSE
        System.out.println("🚨 LLM completely failed → using fallback content");

        return """
    {
      "subject": "Special Offer for Valued Customers",
      "body": "Dear Customer, explore our new financial product designed to maximize your returns. Click below to learn more."
    }
    """;
    }


    public Strategy interpretBrief(String brief) {

        String prompt = """
You are an AI banking marketing strategist.

Extract realistic customer targeting filters from campaign brief.

IMPORTANT DATA RULES:
- Premium / Elite / Luxury → minCreditScore = 680
- Good credit → minCreditScore = 620
- High income / affluent → minIncome = 150000
- Moderate income → minIncome = 80000
- Women / female → gender = Female
- Men / male → gender = Male
- Senior citizens → age > 50 (ignore if age not supported)
- City mentioned → set city
- If not clearly implied → return null

Return ONLY valid JSON.

FORMAT:

{
 "city": "string or null",
 "gender": "string or null",
 "minIncome": number or null,
 "minCreditScore": number or null
}

Campaign Brief:
""" + brief;

        String response = callLLM(prompt);

        Strategy strategy = new Strategy();

        try {

            ObjectMapper mapper = new ObjectMapper();

            int start = response.indexOf("{");
            int end = response.lastIndexOf("}") + 1;
            String json = response.substring(start, end);

            Map<String,Object> result =
                    mapper.readValue(json, Map.class);

            // CITY
            Object cityObj = result.get("city");
            if(cityObj != null)
                strategy.setCity(cityObj.toString().trim());

            // GENDER
            Object genderObj = result.get("gender");
            if(genderObj instanceof String)
                strategy.setGender((String) genderObj);
            else if(genderObj instanceof List && !((List<?>) genderObj).isEmpty())
                strategy.setGender(((List<?>) genderObj).get(0).toString());

            // CREDIT SCORE
            if(result.get("minCreditScore") != null)
                strategy.setMinCreditScore(
                        ((Number) result.get("minCreditScore")).intValue()
                );

            // INCOME
            if(result.get("minIncome") != null)
                strategy.setMinIncome(
                        ((Number) result.get("minIncome")).intValue()
                );

        } catch(Exception e) {

            System.out.println("⚠ LLM parse failed → using rule-based fallback");

            String lower = brief.toLowerCase();

            Strategy fallback = new Strategy();

            // CITY heuristic
            if(lower.contains("mumbai")) fallback.setCity("Mumbai");
            else if(lower.contains("delhi")) fallback.setCity("Delhi");
            else if(lower.contains("bangalore")) fallback.setCity("Bangalore");
            else if(lower.contains("hyderabad")) fallback.setCity("Hyderabad");
            else if(lower.contains("pune")) fallback.setCity("Pune");
            else if(lower.contains("chennai")) fallback.setCity("Chennai");

            // GENDER
            if(lower.contains("female") || lower.contains("women"))
                fallback.setGender("Female");
            if(lower.contains("male") || lower.contains("men"))
                fallback.setGender("Male");

            // INCOME
            if(lower.contains("premium") || lower.contains("affluent"))
                fallback.setMinIncome(150000);
            else if(lower.contains("middle"))
                fallback.setMinIncome(80000);

            // CREDIT
            if(lower.contains("premium") || lower.contains("elite"))
                fallback.setMinCreditScore(680);
            else if(lower.contains("good credit"))
                fallback.setMinCreditScore(620);

            return fallback;
        }

        return strategy;
    }

//    public EmailContent generateEmail(String brief, Strategy strategy) {
//
//        String prompt = """
//You are a senior banking marketing copywriter.
//
//Write a premium persuasive email.
//
//Constraints:
//- 120–160 words
//- Strong CTA
//- Not generic
//- Focus on benefits
//- Personalized to segment
//
//Return ONLY JSON.
//
//{
// "subject": "...",
// "body": "..."
//}
//
//Campaign Brief:
//""" + brief;
//
//        String response = callLLM(prompt);
//
//        EmailContent email = new EmailContent();
//
////        try {
////
////            ObjectMapper mapper = new ObjectMapper();
////
////            String clean = response
////                    .replace("```json","")
////                    .replace("```","")
////                    .trim();
////
////            Map<String,Object> map =
////                    mapper.readValue(clean, Map.class);
////
////            email.setSubject(map.get("subject").toString());
////            email.setBody(map.get("body").toString());
////
////        } catch (Exception ex){
////
////            System.out.println("⚠ Email parse failed → fallback smart template");
////
////            email.setSubject("Exclusive Financial Opportunity");
////            email.setBody(
////                    "Dear Customer,\n\n" +
////                            "We are excited to introduce a premium financial solution designed " +
////                            "to enhance your wealth growth while ensuring stability and flexibility. " +
////                            "Enjoy higher returns, seamless digital experience, and dedicated support.\n\n" +
////                            "Click here to explore more.\n\n" +
////                            "Regards,\nBank Team"
////            );
////        }
//
//        System.out.println("===== RAW LLM EMAIL RESPONSE =====");
//        System.out.println(response);
//        System.out.println("==================================");
//
//        try {
//
//            ObjectMapper mapper = new ObjectMapper();
//
//            String cleaned = response
//                    .replace("```json", "")
//                    .replace("```", "")
//                    .trim();
//
//            int start = cleaned.indexOf("{");
//            int end = cleaned.lastIndexOf("}");
//
//            if(start == -1 || end == -1){
//                throw new RuntimeException("No JSON found in LLM response");
//            }
//
//            String json = cleaned.substring(start, end + 1);
//
//            Map<String,Object> map =
//                    mapper.readValue(json, Map.class);
//
//            Object subjectObj = map.get("subject");
//            Object bodyObj = map.get("body");
//
//            if(subjectObj == null || bodyObj == null){
//                throw new RuntimeException("Missing subject/body");
//            }
//
//            email.setSubject(subjectObj.toString());
//            email.setBody(bodyObj.toString());
//
//        }
//        catch(Exception ex){
//
//            System.out.println("⚠ Email parse failed → fallback smart template");
//
//            email.setSubject("Exclusive Financial Opportunity");
//            email.setBody(
//                    "Dear Customer,\n\n" +
//                            "Discover our premium banking solution designed to maximize " +
//                            "returns while ensuring flexibility and digital convenience.\n\n" +
//                            "Click to explore now.\n\n" +
//                            "Regards,\nBank Team"
//            );
//        }
//        return email;
//    }

public EmailContent generateEmail(String brief, Strategy strategy) {

    String prompt = """
You are a senior banking marketing copywriter.

Write a premium persuasive email.

STRICT RULE:
Return ONLY this format:

SUBJECT: <short subject>
BODY: <email content>

Campaign Brief:
""" + brief;

    String response = callLLM(prompt);

    System.out.println("===== RAW LLM EMAIL RESPONSE =====");
    System.out.println(response);
    System.out.println("==================================");

    EmailContent email = new EmailContent();

    try {

        String subject = null;
        String body = null;

        String[] lines = response.split("\n");

        for(String line : lines){

            if(line.toLowerCase().startsWith("subject:")){
                subject = line.substring(8).trim();
            }

            if(line.toLowerCase().startsWith("body:")){
                body = response.substring(response.toLowerCase().indexOf("body:") + 5).trim();
                break;
            }
        }

        if(subject == null || body == null){
            throw new RuntimeException("AI format not respected");
        }

        email.setSubject(subject);
        email.setBody(body);

    }
    catch(Exception ex){

        System.out.println("⚠ Email parse failed → fallback smart template");

        email.setSubject("Exclusive Financial Opportunity");
        email.setBody(
                "Dear Customer,\n\n" +
                        "We are delighted to introduce a premium financial solution " +
                        "designed to enhance your savings growth with stability and " +
                        "digital convenience.\n\n" +
                        "Click below to explore more.\n\n" +
                        "Regards,\nBank Team"
        );
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