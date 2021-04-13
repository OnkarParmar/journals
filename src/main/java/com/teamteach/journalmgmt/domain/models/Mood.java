package com.teamteach.journalmgmt.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@Document(collection = "moods")
public class Mood {
    @Transient
    public static final String SEQUENCE_NAME = "moods_sequence";

    @Id
    protected String moodId;
    private String name;
    private boolean isParent;
    private boolean isTeacher;
}
