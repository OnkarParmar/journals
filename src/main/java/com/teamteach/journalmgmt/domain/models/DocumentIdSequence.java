package com.teamteach.journalmgmt.domain.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@Document(collection = "document_sequence")
public class DocumentIdSequence {

    @Id
    private String id;
    private long sequence;
}