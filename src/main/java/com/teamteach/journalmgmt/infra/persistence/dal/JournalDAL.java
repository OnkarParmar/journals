package com.teamteach.journalmgmt.infra.persistence.dal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamteach.commons.utils.AnonymizeService;

import com.teamteach.journalmgmt.domain.models.Journal;
import com.teamteach.journalmgmt.domain.models.JournalEntry;
import com.teamteach.journalmgmt.domain.models.SearchKey;
import com.teamteach.journalmgmt.domain.ports.out.IJournalEntryRepository;
import com.teamteach.journalmgmt.domain.ports.out.IJournalRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

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
    @Override
    public List<Journal> getJournals(HashMap<SearchKey,Object> searchCriteria){
        Query query = new Query();
        if(searchCriteria != null){
            for(Map.Entry<SearchKey,Object> criteria : searchCriteria.entrySet()){
                if(criteria.getKey().isAnonymizable()){
                    query.addCriteria(Criteria.where(criteria.getKey().getField()).is(AnonymizeService.anonymizeData((String)criteria.getValue())));
                } else{
                    query.addCriteria(Criteria.where(criteria.getKey().getField()).is(criteria.getValue()));
                }
            }
        }
        List<Journal> journals = mongoTemplate.find(query,Journal.class);
        return journals;
    }
    @Override
    public List<JournalEntry> getJournalEntries(HashMap<SearchKey,Object> searchCriteria){
        Query query = new Query();
        if(searchCriteria != null){
            for(Map.Entry<SearchKey,Object> criteria : searchCriteria.entrySet()){
                if(criteria.getKey().isAnonymizable()){
                    query.addCriteria(Criteria.where(criteria.getKey().getField()).is(AnonymizeService.anonymizeData((String)criteria.getValue())));
                } else{
                    query.addCriteria(Criteria.where(criteria.getKey().getField()).is(criteria.getValue()));
                }
            }
        }
        List<JournalEntry> journalEntries = mongoTemplate.find(query,JournalEntry.class);
        return journalEntries;
    }
    @Override
    public JournalEntry getJournalDashboardEntries(String ownerId, String journalId){
        Query query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(ownerId));
        query.addCriteria(Criteria.where("journalId").is(journalId));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        JournalEntry journalEntry = mongoTemplate.findOne(query, JournalEntry.class);
        return journalEntry;
    }
}