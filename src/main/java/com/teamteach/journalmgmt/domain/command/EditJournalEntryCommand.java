package com.teamteach.journalmgmt.domain.command;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class EditJournalEntryCommand {
    private String journalId;
    private String entryId;
    private String mood;
    private String text;
    private String[] children;
    private String categoryId;
    private MultipartFile entryImage;
    private String recommendationId;
    private String suggestionIndex;
}