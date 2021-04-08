package com.teamteach.journals.models.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import com.teamteach.journals.models.entities.*;

import java.util.*;

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Document(collection = "journals")
    public class Journal extends BaseModel {
        @Transient
        public static final String SEQUENCE_NAME = "journals_sequence";

        @Id
        protected String id;
        private String name;
        private List<Category> categories;

        @Builder
        public Journal(String id, Date createdAt, Date updatedAt, String name) {
            super(createdAt, updatedAt);

            this.id = id;
            this.name = name;
        }

    }
