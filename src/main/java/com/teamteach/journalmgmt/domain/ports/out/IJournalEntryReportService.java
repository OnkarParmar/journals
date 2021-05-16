package com.teamteach.journalmgmt.domain.ports.out;

import com.teamteach.journalmgmt.domain.models.JournalEntryProfile;

public interface IJournalEntryReportService {
    void sendJournalEntryReportEvent(JournalEntryProfile journalEntryProfile, String q);
}
