package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.responses.*;

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
public class PdfRenderingService {
    @Value("${gateway.url}")
    static String gateway;

    static RestTemplate restTemplate = new RestTemplate();

    public static String renderPdf(String accessToken, SendReportInfo sendReportInfo){
        try {
            String renderPdfUrl = gateway+"/pdf/render";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken); 
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SendReportInfo> entity = new HttpEntity<>(sendReportInfo, headers);
            ResponseEntity<String> response = restTemplate.exchange(renderPdfUrl, HttpMethod.POST, entity, String.class);
            JsonNode respoJsonNode = new ObjectMapper().readTree(response.getBody());
            boolean success = respoJsonNode.get("success").asBoolean();
            if (success) {
                return respoJsonNode.get("pdfUrl").asText();
            } 
        }
        catch (IOException e) {
            return null;
        }
        return null;
    }     
}