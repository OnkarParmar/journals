package com.teamteach.journalmgmt.domain.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NoArgsConstructor;

import java.util.*;

import com.teamteach.journalmgmt.domain.models.*;

@NoArgsConstructor
@Data
@EqualsAndHashCode
@Document(collection = "journalEntries")
public class JournalEntry extends BaseModel{
    @Transient
    public static final String SEQUENCE_NAME = "journalEntries_sequence";

    @Id
    protected String entryId;
    private String journalId;
    private String ownerId;
    private String mood;
    private String text;
    private String[] children;
    private String categoryId;
    private String entryImage;
    private boolean locked;
    private String recommendationId;
    private String suggestionIndex;
    private String timezone;

    @Builder
    public JournalEntry (
            String entryId,
            String journalId,
            String ownerId,
            String text,
            Date createdAt,
            Date updatedAt,
            String[] children,
            String categoryId,
            String mood,
            boolean locked,
            String recommendationId,
            String suggestionIndex) {
        super(createdAt, updatedAt);

        this.entryId = entryId;
        this.journalId = journalId;
        this.ownerId = ownerId;
        this.text = text;
        this.children = children;
        this.categoryId = categoryId;
        this.mood = mood;
        this.locked = locked;
        this.recommendationId = recommendationId;
        this.suggestionIndex = suggestionIndex;
    }
}
