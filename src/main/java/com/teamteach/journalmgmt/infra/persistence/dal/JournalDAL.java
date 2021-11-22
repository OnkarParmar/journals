package com.teamteach.journalmgmt.infra.persistence.dal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.teamteach.commons.utils.AnonymizeService;

import com.teamteach.journalmgmt.domain.models.Journal;
import com.teamteach.journalmgmt.domain.models.JournalEntry;
import com.teamteach.journalmgmt.domain.models.Mood;
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
    public void saveJournal(Journal journal, boolean anonymize) {
        if (anonymize) {
            journal.setOwnerId(AnonymizeService.anonymizeData(journal.getOwnerId()));
        }
     
        mongoTemplate.save(journal);
        
    }
    @Override
    public long countJournal(String journalType , String ownerId){
        Query query = new Query(Criteria.where("active").is(true).and("journalType").is(journalType).and("ownerId").is(AnonymizeService.anonymizeData(ownerId)));
        return mongoTemplate.count(query, Journal.class);
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
    public List<Mood> getMoods(HashMap<SearchKey,Object> searchCriteria){
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
        List<Mood> moods = mongoTemplate.find(query,Mood.class);
        return moods;
    }

    @Override
    public JournalEntry getLastSuggestionEntry(String id, String ownerId){
        Query query = new Query();
        query.addCriteria(Criteria.where("recommendationId").is(id).and("ownerId").is(AnonymizeService.anonymizeData(ownerId)));
        query.with(Sort.by(Sort.Direction.DESC, "updatedAt"));
        JournalEntry journalEntry = mongoTemplate.findOne(query, JournalEntry.class);
        return journalEntry;
    }

    @Override
    public List<JournalEntry> getSearchJournalEntries(HashMap<SearchKey,Object> searchCriteria, HashMap<SearchKey,Object> containCriteria, Date fromDate, Date toDate){
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
        if(containCriteria != null){
            for(Map.Entry<SearchKey,Object> criteria : containCriteria.entrySet()){
                ArrayList<String> list = (ArrayList)criteria.getValue();
                query.addCriteria(Criteria.where(criteria.getKey().getField()).in(list.toArray(new String[list.size()])));
            }
        }
        if (fromDate != null && toDate != null) {
            query.addCriteria(Criteria.where("createdAt").lte(toDate).gte(fromDate));
        } else if (fromDate != null) {
            query.addCriteria(Criteria.where("createdAt").gte(fromDate));
        }
        query.with(Sort.by(Direction.DESC, "createdAt"));
        List<JournalEntry> journalEntries = mongoTemplate.find(query,JournalEntry.class);
        return journalEntries;
    }

    @Override
    public JournalEntry getJournalDashboardEntries(String ownerId, String journalId){
        Query query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(AnonymizeService.anonymizeData(ownerId)));
        query.addCriteria(Criteria.where("journalId").is(journalId));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        JournalEntry journalEntry = mongoTemplate.findOne(query, JournalEntry.class);
        return journalEntry;
    }
}