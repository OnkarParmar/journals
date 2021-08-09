package com.teamteach.journalmgmt.infra.persistence.dal;

import com.teamteach.journalmgmt.domain.models.Journal;
import com.teamteach.journalmgmt.domain.models.JournalEntry;
import com.teamteach.journalmgmt.domain.ports.out.IJournalEntryRepository;
import com.teamteach.journalmgmt.domain.ports.out.IJournalRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Component
@RequiredArgsConstructor
public class JournalDAL  implements IJournalRepository, IJournalEntryRepository {
    final MongoTemplate mongoTemplate;

    @Override
    public void saveJournalEntry(JournalEntry journalEntry) {
        mongoTemplate.save(journalEntry);
    }
    @Override
    public void saveJournal(Journal journal) {
        mongoTemplate.save(journal);
    }
    @Override
    public void removeJournal(String id){
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, "journals");
    }
    @Override
    public void removeJournalEntries(String type, String id){
        Query query = new Query(Criteria.where(type).is(id));
        mongoTemplate.remove(query, JournalEntry.class);
    }
}