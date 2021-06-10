package com.teamteach.journalmgmt.domain.ports.out;

import com.teamteach.journalmgmt.domain.models.JournalEntryProfile;
import com.teamteach.journalmgmt.domain.models.SendReportInfo;

public interface IJournalEntryReportService {
    void sendJournalEntryReportEvent(SendReportInfo sendReportInfo, String q);
    // JournalEntryProfile getReport(JournalEntryProfile journalEntryProfile);
}
