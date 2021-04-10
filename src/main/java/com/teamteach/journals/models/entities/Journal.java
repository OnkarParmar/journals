package com.teamteach.journals.models.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Document(collection = "journals")
    public class Journal extends BaseModel {
        @Transient
        public static final String SEQUENCE_NAME = "journals_sequence";

        @Id
        protected String journalId;
        private String journalType;
        private String ownerId;
        private String title;
        private String desc;
        private List<Category> categories;
        private List<Mood> moods;

        @Builder
        public Journal(
                String journalId,
                Date createdAt,
                Date updatedAt,
                String title,
                String desc,
                String[] children) {
            super(createdAt, updatedAt);

            this.journalId = journalId;
            this.title = title;
            this.children = children;
            this.desc = desc;
        }

    }
