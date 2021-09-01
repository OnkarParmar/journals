package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.EditJournalEntryCommand;
import com.teamteach.journalmgmt.domain.command.JournalEntrySearchCommand;
import com.teamteach.journalmgmt.domain.models.*;

import java.util.*;
import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

@Service
public class RecommendationService {
    @Value("${gateway.url}")
    String gateway;
    RestTemplate restTemplate = new RestTemplate();

    public Map<String, Category> getCategories(String accessToken){
        Map<String, Category> categories = new HashMap<>();
        Category category = null;
        try {
            String categoriesUrl = gateway+"/recommendations/categories";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken); 
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity <String> entity = new HttpEntity <> (null, headers);
            ResponseEntity <String> response = restTemplate.exchange(categoriesUrl, HttpMethod.GET, entity, String.class);
            JsonNode respoJsonNode = new ObjectMapper().readTree(response.getBody());
            boolean success = respoJsonNode.get("success").asBoolean();
            if (success) {
                JsonNode categoriesJson = respoJsonNode.get("objects");
                for (JsonNode categoryJson : categoriesJson) {
                    String categoryId = categoryJson.get("categoryId").asText();
                    category = new Category(categoryId,categoryJson.get("title").asText(),categoryJson.get("colour").asText());
                    categories.put(categoryId,category);
                }
            } 
        }
        catch (IOException e) {
            return null;
        }
        return categories;
    }    
    public String getSuggestion(String accessToken, JournalEntry searchParams){
        String suggestion = null;
        try {
            String url = gateway+"/recommendations";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken); 
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<JournalEntry> entity = new HttpEntity<>(searchParams, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JsonNode respoJsonNode = new ObjectMapper().readTree(response.getBody());
            boolean success = respoJsonNode.get("success").asBoolean();
            if (success) {
                JsonNode recommendation = respoJsonNode.get("object");
                suggestion = recommendation.get("recommendation").asText();
            } 
        }
        catch (IOException e) {
            return null;
        }
        return suggestion;
    }    
}