package com.teamteach.journalmgmt.domain.ports.out;

import com.teamteach.journalmgmt.domain.models.*;

public interface IJournalEntryRepository {
   boolean journalExistsById(String journalId);
   String setupInitialJournal(JournalEntry journalEntry);
   Journal getJournalByJournalId(String journalId);
   void saveJournal(JournalEntry journalEntry);
}
