package com.teamteach.journalmgmt.domain.ports.out;

import java.util.HashMap;
import java.util.List;

import com.teamteach.journalmgmt.domain.models.*;

public interface IJournalRepository {
   void saveJournal(Journal journal);
   void removeJournal(String id);
   List<Journal> getJournals(HashMap<SearchKey,Object> searchCriteria);
}
