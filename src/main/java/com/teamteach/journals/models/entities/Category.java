package com.teamteach.journals.models.entities;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "categories")
public class Category {
    @Transient
    public static final String SEQUENCE_NAME = "categories_sequence";

    @Id
    protected String categoryId;
    private String title;
    private String colour; 
    private boolean isParent;
    private boolean isTeacher;   
}
