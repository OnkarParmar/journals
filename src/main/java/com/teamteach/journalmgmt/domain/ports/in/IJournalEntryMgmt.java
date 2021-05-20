package com.teamteach.journalmgmt.domain.ports.in;

import java.util.Date;
import java.util.List;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntriesResponse;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import org.springframework.web.multipart.MultipartFile;

public interface IJournalEntryMgmt {
    ObjectListResponseDto<JournalEntryResponse> findAllEntries();

    ObjectResponseDto findById(String id);

    ObjectResponseDto delete(String id);

    ObjectResponseDto lock(String id);

    boolean isEditable(Date current);

    ObjectResponseDto uploadReport(String journalId);

    ObjectResponseDto saveEntry(EditJournalEntryCommand editJournalEntryCommand);

    ObjectResponseDto searchEntries(JournalEntrySearchCommand journalEntrySearchCommand, String token);

    ObjectResponseDto sendEntriesReport(JournalEntrySearchCommand journalEntrySearchCommand, String accessToken);
}