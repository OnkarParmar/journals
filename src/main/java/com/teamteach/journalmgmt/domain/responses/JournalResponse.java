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
    private List<MoodObj> moods;
    private int entryCount;
    private ParentProfileResponseDto parentProfile;
    private String name;
    private String journalType;
    private Boolean active;
    private String journalYear;
    private String info;

    @Builder
    public JournalResponse(Journal journal) {
        SimpleDateFormat formatter= new SimpleDateFormat("dd MMM, yyyy");
        this.journalId = journal.getJournalId();
        this.title = journal.getTitle();
        this.desc = journal.getDesc();    
        this.createdAt = formatter.format(journal.getCreatedAt());
        this.updatedAt = formatter.format(journal.getUpdatedAt());
        this.parentProfile = null;
        this.name = journal.getName();
        this.journalType = journal.getJournalType();
        this.active = journal.isActive();
        this.journalYear = journal.getJournalYear();
        this.info = journal.getInfo();
    }
    public void setEntryCount() {
        this.entryCount = moods.stream().map(x -> x.getCount()).reduce(0, Integer::sum);
    }
}