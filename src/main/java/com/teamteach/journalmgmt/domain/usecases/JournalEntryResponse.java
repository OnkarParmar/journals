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
public class JournalEntryResponse {
    protected String entryId;
    private String name;
    private String createdAt;

    @Builder
    public JournalEntryResponse(JournalEntry journalEntry) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.entryId = journalEntry.getEntryId();
        //this.name = journalEntry.getName();
        this.createdAt = formatter.format(journalEntry.getCreatedAt());
    }

}