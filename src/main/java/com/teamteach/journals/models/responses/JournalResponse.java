package com.teamteach.journals.models.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.text.SimpleDateFormat;
import java.util.*;

import com.teamteach.journals.models.entities.*;

    @Data
    public class JournalResponse {
        protected String journalId;
        private String title;
        private String createdAt;

        @Builder
        public JournalResponse(Journal journal) {
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            this.journalId = journal.getJournalId();
            this.title = journal.getTitle();
            this.createdAt = formatter.format(journal.getCreatedAt());
        }

    }
