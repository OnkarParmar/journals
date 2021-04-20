package com.teamteach.journalmgmt.domain.responses;

import lombok.Builder;
import lombok.Data;

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
    private List<ObjectCount> moods;
    private int entryCount;

    @Builder
    public JournalResponse(Journal journal) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm:ss");
        this.journalId = journal.getJournalId();
        this.title = journal.getTitle();
        this.desc = journal.getDesc();    
        this.createdAt = formatter.format(journal.getCreatedAt());
        this.updatedAt = formatter.format(journal.getUpdatedAt());
    }
    public void setEntryCount() {
        this.entryCount = moods.stream().map(x -> x.getCount()).reduce(0, Integer::sum);
    }
}