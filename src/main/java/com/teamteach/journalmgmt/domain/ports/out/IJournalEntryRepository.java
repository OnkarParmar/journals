package com.teamteach.journalmgmt.domain.ports.out;

import com.teamteach.journalmgmt.domain.models.*;

public interface IJournalEntryRepository {
   void saveJournalEntry(JournalEntry journalEntry);
}
