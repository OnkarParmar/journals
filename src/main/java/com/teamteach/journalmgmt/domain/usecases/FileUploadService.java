package com.teamteach.journalmgmt.domain.usecases;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class FileUploadService {
    RestTemplate restTemplate = new RestTemplate();
    private final String S3_POSTURL = "https://ms.digisherpa.ai/files/upload/";

    public String saveTeamTeachFile(String folder, String filename, byte[] fileByteArray) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
            .builder("form-data")
            .name("file")
            .filename(filename)
            .build();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(fileByteArray, fileMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(S3_POSTURL+folder, HttpMethod.POST, requestEntity, String.class);
            System.out.println(response);
            return response.getBody().toString();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
