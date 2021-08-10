package com.teamteach.journalmgmt.domain.ports.out;

import java.util.HashMap;
import java.util.List;

import com.teamteach.journalmgmt.domain.models.*;

public interface IJournalEntryRepository {
   void saveJournalEntry(JournalEntry journalEntry);
   void removeJournalEntries(String type, String id);
   List<JournalEntry> getJournalEntries(HashMap<SearchKey,Object> searchCriteria);
   JournalEntry getJournalDashboardEntries(String ownerId, String journalId);
}
