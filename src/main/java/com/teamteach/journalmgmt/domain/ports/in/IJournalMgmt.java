package com.teamteach.journalmgmt.domain.ports.in;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.models.SendReportInfo;
import com.teamteach.journalmgmt.domain.responses.JournalDashboardResponse;
import com.teamteach.journalmgmt.domain.responses.JournalResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

public interface IJournalMgmt {
    ObjectResponseDto createJournal(JournalCommand journalCommand);
    ObjectListResponseDto<JournalResponse> findAll();
    ObjectResponseDto delete(String id);
    ObjectResponseDto findByTitle(String title);
    ObjectListResponseDto<JournalResponse> findById(String id, String token);
    ObjectResponseDto savePrivate(JournalCommand journalCommand);
    ObjectResponseDto buildReport(String journalId, JournalEntrySearchCommand journalEntrySearchCommand, String accessToken);
    ObjectResponseDto sendReport(SendReportInfo sendReportInfo, String accessToken);
    ObjectListResponseDto<JournalDashboardResponse> getJournalDashboard(String accessToken);
}