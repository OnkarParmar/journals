package com.teamteach.journalmgmt.infra.persistence.dal;

import com.teamteach.journalmgmt.domain.models.Journal;
import com.teamteach.journalmgmt.domain.ports.out.IJournalRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;

@Component
@RequiredArgsConstructor
public class JournalDAL  implements IJournalRepository {
    final MongoTemplate mongoTemplate;

    @Override
    public boolean journalExistsById(String journalId) {
        return true;
    }

    @Override
    public String setupInitialJournal(Journal journal) {
        return null;
    }

    @Override
    public Journal getJournalByJournalId(String journalId) {
        return null;
    }

    @Override
    public void saveJournal(Journal journal) {
        mongoTemplate.save(journal);
    }
}