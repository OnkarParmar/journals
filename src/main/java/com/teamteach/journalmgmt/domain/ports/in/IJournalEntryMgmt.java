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
    //ObjectResponseDto saveEntry(JournalEntryCommand journalEntryCommand);

    ObjectListResponseDto<JournalEntryResponse> findAllEntries();

    ObjectResponseDto findById(String id);

    ObjectResponseDto delete(String id);

    boolean isEditable(Date current);

    //ObjectResponseDto saveTeamTeachFile(MultipartFile file, String id);

    ObjectResponseDto saveEntry(EditJournalEntryCommand editJournalEntryCommand);

    ObjectListResponseDto<JournalEntriesResponse> searchEntries(JournalEntrySearchCommand journalEntrySearchCommand, String token);
}
