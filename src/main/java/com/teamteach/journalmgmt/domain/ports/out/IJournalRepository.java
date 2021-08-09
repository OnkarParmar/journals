package com.teamteach.journalmgmt.domain.ports.out;

import com.teamteach.journalmgmt.domain.models.*;

public interface IJournalRepository {
   void saveJournal(Journal journal);
   void removeJournal(String id);
}
