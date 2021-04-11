package com.teamteach.journals.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@EqualsAndHashCode
@Document(collection = "journalEntries")
public class JournalEntry extends BaseModel{
    @Transient
    public static final String SEQUENCE_NAME = "journalEntries_sequence";

    @Id
    protected String entryId;
    private String ownerId;
    private String mood;
    private String text;
    private String[] children;
    private String category;

    @Builder
    public JournalEntry (
            String entryId,
            String ownerId,
            String text,
            Date createdAt,
            Date updatedAt,
            String[] children,
            String category,
            String mood ) {
        super(createdAt, updatedAt);

        this.entryId = entryId;
        this.ownerId = ownerId;
        this.text = text;
        this.children = children;
        this.category = category;
        this.mood = mood;
    }
}
