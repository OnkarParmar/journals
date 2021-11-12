package com.teamteach.journalmgmt.domain.models;

import com.teamteach.journalmgmt.domain.models.BaseModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Document(collection = "journals")
public class Journal  {
    @Transient
        public static final String SEQUENCE_NAME = "journals_sequence";

    @Id
    protected String journalId;
    private String journalType;
    private String ownerId;
    private String title;
    private String desc; // Used by Kuntal To Tell Good Morning To The user
    private List<Category> categories;
    private List<Mood> moods;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private String journalYear;
    private boolean active;
    private String info; 

    @Builder
    public Journal(
            String journalId,
            String journalType,
            String ownerId,
            Date createdAt,
            Date updatedAt,
            String title,
            String desc,
            String name,
            String journalYear,
            String info) 
            {
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
                this.journalId = journalId;
                this.title = title;
                this.desc = desc;
                this.journalType = journalType;
                this.ownerId = ownerId;
                this.name = name;
                this.journalYear = journalYear;
                this.info = info;
            }
}
