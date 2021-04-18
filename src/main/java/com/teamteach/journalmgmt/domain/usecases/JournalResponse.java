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
    private String desc;
    private String createdAt;
    private int totalEntries;
    private String updatedAt;

    @Builder
    public JournalResponse(Journal journal) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm:ss");
        this.journalId = journal.getJournalId();
        this.title = journal.getTitle();
        this.desc = journal.getDesc();    
        //this.totalEntries = db.journals.count(ownerId(journal.getOwnerId()));
        this.createdAt = formatter.format(journal.getCreatedAt());
        this.updatedAt = formatter.format(journal.getUpdatedAt());
    }

}