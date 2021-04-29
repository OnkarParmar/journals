package com.teamteach.journalmgmt.domain.responses;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class JournalEntriesResponse  {
    private int day;
    private List<JournalEntryResponse> entries;

    public JournalEntriesResponse() {
        this.entries = new ArrayList<>();
    }
    public void addEntry(JournalEntryResponse entry) {
        this.entries.add(entry);
    }
}