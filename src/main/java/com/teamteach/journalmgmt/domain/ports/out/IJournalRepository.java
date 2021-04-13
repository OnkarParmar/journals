package com.teamteach.journalmgmt.domain.ports.out;

import com.teamteach.journalmgmt.domain.models.*;

public interface IJournalRepository {
   boolean journalExistsById(String journalId);
   String setupInitialJournal(Journal journal);
   Journal getJournalByJournalId(String journalId);
   void saveJournal(Journal journal);
}
