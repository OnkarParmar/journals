package com.teamteach.journalmgmt.domain.responses;

import lombok.Builder;
import lombok.Data;
import java.text.SimpleDateFormat;
import java.util.*;

import com.teamteach.journalmgmt.domain.models.*;

@Data
public class JournalEntryResponse {
    protected String entryId;
    private String text;
    private String createdAt;
    private String journalId;
    private String ownerId;
    private String mood;
    private List<ChildProfile> children;
    private Category category;
    private String entryImage;

    @Builder
    public JournalEntryResponse(JournalEntry journalEntry) {
        SimpleDateFormat formatter= new SimpleDateFormat("dd MMMM, yyyy   hh:mm aa");
        this.entryId = journalEntry.getEntryId();
        this.text = journalEntry.getText();
        this.createdAt = formatter.format(journalEntry.getCreatedAt());
        this.mood = journalEntry.getMood();
        this.ownerId = journalEntry.getOwnerId();
        this.children = new ArrayList<>();
        this.journalId = journalEntry.getJournalId();
        this.entryImage = journalEntry.getEntryImage();
    }

    public void addChild(ChildProfile child){
        children.add(child);
    }

}