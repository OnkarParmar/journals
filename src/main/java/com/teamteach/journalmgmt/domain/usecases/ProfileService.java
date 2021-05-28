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
public class ProfileService {

    RestTemplate restTemplate = new RestTemplate();

    public ParentProfileResponseDto getProfile(String ownerId, String accessToken){
        List<ChildProfile> children = new ArrayList<>();
        ParentProfileResponseDto parentProfile = null;
        try {
            String parentProfileUrl = "https://ms.digisherpa.ai/profiles/owner/"+ownerId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken); 
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity <String> entity = new HttpEntity <> (null, headers);
            System.out.println(parentProfileUrl);
            System.out.println(headers);
            ResponseEntity <String> response = restTemplate.exchange(parentProfileUrl, HttpMethod.GET, entity, String.class);
            JsonNode respoJsonNode = new ObjectMapper().readTree(response.getBody());
            boolean success = respoJsonNode.get("success").asBoolean();
            if (success) {
                JsonNode parentProfileJson = respoJsonNode.get("object");
                JsonNode childJsonArray = parentProfileJson.get("children");
                for (JsonNode childJson : childJsonArray) {
                    children.add(new ChildProfile(childJson.get("name").asText(),
                                                    childJson.get("profileId").asText(),
                                                    childJson.get("info").asText(),
                                                    childJson.get("birthYear").asText(),
                                                    childJson.get("profileImage").asText()
                                                    ));
                }
                parentProfile = ParentProfileResponseDto.builder()
																.profileId(parentProfileJson.get("profileId").asText())
																.fname(parentProfileJson.get("fname").asText())
																.lname(parentProfileJson.get("lname").asText())
																.email(parentProfileJson.get("email").asText())
																.children(children)
																.build();
            } 
        }
        catch (IOException e) {
            return null;
        }
        return parentProfile;
    }    
}