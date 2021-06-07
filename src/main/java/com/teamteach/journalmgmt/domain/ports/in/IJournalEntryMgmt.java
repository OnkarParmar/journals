package com.teamteach.journalmgmt.domain.ports.in;

import java.util.Date;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

public interface IJournalEntryMgmt {
    ObjectResponseDto findById(String id, String token);

    ObjectResponseDto getLastSuggestion(String id, String token);

    ObjectResponseDto delete(String id);

    ObjectResponseDto lock(String id);

    boolean isEditable(Date current);

    ObjectResponseDto saveEntry(EditJournalEntryCommand editJournalEntryCommand);

    ObjectResponseDto searchEntries(JournalEntrySearchCommand journalEntrySearchCommand, String token);
}