package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.responses.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    RestTemplate restTemplate = new RestTemplate();

    public Map<String, Category> getCategories(String accessToken){
        Map<String, Category> categories = new HashMap<>();
        Category category = null;
        try {
            String categoriesUrl = "https://ms.digisherpa.ai/recommendations/categories";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken); 
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
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
}