package com.jpmc.midascore.incentive;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentiveClient {

    private static final Logger log = LoggerFactory.getLogger(IncentiveClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public IncentiveClient(RestTemplate restTemplate,
                           @Value("${incentive.base-url:http://localhost:8080}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public float fetchIncentive(Transaction tx) {
        try {
            var response = restTemplate.postForObject(baseUrl + "/incentive", tx, Incentive.class);
            return response != null ? response.getAmount() : 0f;
        } catch (Exception e) {
            log.warn("Incentive API call failed, defaulting incentive to 0. Reason: {}", e.getMessage());
            return 0f;
        }
    }
}
