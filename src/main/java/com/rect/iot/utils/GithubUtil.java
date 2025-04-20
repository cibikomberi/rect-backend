package com.rect.iot.utils;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GithubUtil {
    public static String fetchEmail(OAuth2User user, OAuth2AuthorizedClient authorizedClient) throws IllegalAccessException {
        String email = null;
        // Check if this is a GitHub login
        if (authorizedClient != null && authorizedClient.getClientRegistration().getRegistrationId().equals("github")) {
            // Get access token
            String accessToken = authorizedClient.getAccessToken().getTokenValue();

            // Create REST client for GitHub API
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>("", headers);

            try {
                // Make API call to GitHub's email endpoint
                ResponseEntity<String> apiResponse = restTemplate.exchange(
                        "https://api.github.com/user/emails",
                        HttpMethod.GET,
                        entity,
                        String.class);

                log.info("GitHub API response: " + apiResponse.getBody());

                // Parse JSON response to get email
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> emails = mapper.readValue(
                        apiResponse.getBody(),
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                // Find primary email
                for (Map<String, Object> emailObj : emails) {
                    if (Boolean.TRUE.equals(emailObj.get("primary"))) {
                        email = (String) emailObj.get("email");
                        break;
                    }
                }

                // If no primary email, use first one
                if (email == null && !emails.isEmpty()) {
                    email = (String) emails.get(0).get("email");
                }

                log.info("Found email via API: " + email);
                return email;
            }  catch (JsonMappingException e) {
                log.error("Error while processing response received from github");
                throw new IllegalAccessException();
            } catch (JsonProcessingException e) {
                log.error("Error while processing response received from github");
                throw new IllegalAccessException();
            }
        } else {
            // For other providers
            return user.getAttribute("email");
        }
    }
}
