package com.teamteach.journalmgmt.domain.command;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EditJournalEntryCommand {
    private String entryId;
    private String mood;
    private String text;
    private String[] children;
    private String categoryId;
}