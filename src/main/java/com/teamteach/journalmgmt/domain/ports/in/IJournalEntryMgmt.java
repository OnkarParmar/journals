package com.teamteach.journalmgmt.domain.ports.in;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.domain.models.*;

public interface IJournalEntryMgmt {
    ObjectResponseDto saveEntry(JournalEntryCommand journalEntryCommand);

    ObjectListResponseDto<JournalEntryResponse> findAllEntries();

    ObjectResponseDto findById(String id);

    ObjectResponseDto delete(String id);

    ObjectListResponseDto<JournalEntry> searchEntries(JournalEntrySearchCommand journalEntrySearchCommand);
}
