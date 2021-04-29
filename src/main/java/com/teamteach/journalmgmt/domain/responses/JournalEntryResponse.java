package com.teamteach.journalmgmt.domain.responses;

import lombok.Builder;
import lombok.Data;
import java.text.SimpleDateFormat;

import com.teamteach.journalmgmt.domain.models.*;

@Data
public class JournalEntryResponse {
    protected String entryId;
    private String text;
    private String createdAt;
    private String journalId;
    private String ownerId;
    private String mood;
    private String[] children;
    private Category category;

    @Builder
    public JournalEntryResponse(JournalEntry journalEntry) {
        SimpleDateFormat formatter= new SimpleDateFormat("dd MMMM, yyyy   hh:mm aa");
        this.entryId = journalEntry.getEntryId();
        this.text = journalEntry.getText();
        this.createdAt = formatter.format(journalEntry.getCreatedAt());
        this.mood = journalEntry.getMood();
        this.children = journalEntry.getChildren();
        this.ownerId = journalEntry.getOwnerId();
        this.journalId = journalEntry.getJournalId();
    }

}