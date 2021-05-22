package com.teamteach.journalmgmt.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@Document(collection = "moods")
public class Mood {
    @Id
    private String name;
    private String url;
    private boolean isParent;
    private boolean isTeacher;
}
