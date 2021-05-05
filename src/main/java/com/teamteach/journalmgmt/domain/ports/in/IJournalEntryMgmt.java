package com.teamteach.journalmgmt.domain.ports.in;

import java.util.Date;
import java.util.List;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntriesResponse;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

public interface IJournalEntryMgmt {
    ObjectResponseDto saveEntry(JournalEntryCommand journalEntryCommand);

    ObjectListResponseDto<JournalEntryResponse> findAllEntries();

    ObjectResponseDto findById(String id);

    ObjectResponseDto delete(String id);

    boolean isEditable(Date current);

    ObjectResponseDto editEntry(EditJournalEntryCommand editJournalEntryCommand);

    ObjectListResponseDto<JournalEntriesResponse> searchEntries(JournalEntrySearchCommand journalEntrySearchCommand);
}
