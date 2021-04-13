package com.teamteach.journalmgmt.domain.usecases;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.text.SimpleDateFormat;
import java.util.*;

import com.teamteach.journalmgmt.domain.models.*;

@Data
public class JournalResponse {
    protected String journalId;
    private String title;
    private String createdAt;

    @Builder
    public JournalResponse(Journal journal) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        this.journalId = journal.getJournalId();
        this.title = journal.getTitle();
        this.createdAt = formatter.format(journal.getCreatedAt());
    }

}