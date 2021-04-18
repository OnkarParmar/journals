package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.time.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JournalEntryUse implements IJournalEntryMgmt {

    final IJournalEntryRepository journalEntryRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Override
    public ObjectResponseDto saveEntry(JournalEntryCommand journalEntryCommand) {
        Date date = new Date(System.currentTimeMillis());
        Query query = new Query(Criteria.where("createdAt").gte(date));
        JournalEntry journalEntry = mongoTemplate.findOne(query, JournalEntry.class);
        int count=0;

        if (journalEntry != null) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("An entry at the same time already exists!")
                    .build();
        } else {
            count++;
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm:ss");
            journalEntry = JournalEntry.builder()
                    .entryId(sequenceGeneratorService.generateSequence(JournalEntry.SEQUENCE_NAME))
                    .ownerId(journalEntryCommand.getOwnerId())
                    .text(journalEntryCommand.getText())
                    .children(journalEntryCommand.getChildren())
                    .category(journalEntryCommand.getCategory())
                    .mood(journalEntryCommand.getMood())
                    .createdAt(date)
                    .build();
            mongoTemplate.save(journalEntry);
            return ObjectResponseDto.builder()
                    .success(true)
                    .message("Entry created successfully")
                    .object(journalEntry)
                    .build();
        }
    }

    @Override
    public ObjectListResponseDto<JournalEntryResponse> findAllEntries() {
        List<JournalEntryResponse> journalEntryResponses = new ArrayList<>();
        Query query = new Query();
        List<JournalEntry> entries = mongoTemplate.find(query, JournalEntry.class);
        for(JournalEntry journalEntry: entries){
			journalEntryResponses.add(new JournalEntryResponse(journalEntry));
		}
        return new ObjectListResponseDto<>(
                true,
                "Entries retrieved successfully!",
                journalEntryResponses);
    }

    @Override
    public ObjectResponseDto delete(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        try {
            mongoTemplate.remove(query, "entries");
            return ObjectResponseDto.builder()
                    .success(true)
                    .message("Entry deleted successfully")
                    .build();
        } catch (RuntimeException e) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("Entry deletion failed")
                    .build();
        }
    }

    @Override
    public ObjectResponseDto findById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        JournalEntry journalEntry = mongoTemplate.findOne(query, JournalEntry.class);

        if (journalEntry == null) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("An entry with this id does not exist!")
                    .build();
        } else {
            return ObjectResponseDto.builder()
                    .success(true)
                    .message("Entry record retrieved!")
                    .object(journalEntry)
                    .build();
        }
    }

    @Override
    public ObjectListResponseDto<JournalEntry> searchEntries(JournalEntrySearchCommand journalEntrySearchCommand) {
        Query query = new Query();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS\'Z\'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (journalEntrySearchCommand.getEntryId() != null) {
            query.addCriteria(Criteria.where("entryId").is(journalEntrySearchCommand.getEntryId()));
        }
        if (journalEntrySearchCommand.getOwnerId() != null) {
            query.addCriteria(Criteria.where("ownerId").is(journalEntrySearchCommand.getOwnerId()));
        }
        if (journalEntrySearchCommand.getMoods() != null) {
            query.addCriteria(Criteria.where("mood").in(journalEntrySearchCommand.getMoods()));
        }
        if (journalEntrySearchCommand.getCategories() != null) {
            query.addCriteria(Criteria.where("category").in(journalEntrySearchCommand.getCategories()));
        }
        if (journalEntrySearchCommand.getChildren() != null) {
            query.addCriteria(Criteria.where("children").in(journalEntrySearchCommand.getChildren()));
        }
        if (journalEntrySearchCommand.getFromDate() != null && journalEntrySearchCommand.getToDate() != null) {
            try {
                String fromDateStr = journalEntrySearchCommand.getFromDate() + "T00:00:00.000Z";                
                Date fromDate = formatter.parse(fromDateStr);
                String toDateStr = journalEntrySearchCommand.getToDate() + "T23:59:59.999Z"; 
                Date toDate = formatter.parse(toDateStr);
                query.addCriteria(Criteria.where("createdAt").lte(toDate).gte(fromDate));
            }
            catch(ParseException e){
                e.printStackTrace();
            }
        } else if (journalEntrySearchCommand.getFromDate() != null) {
            try {
                String fromDateStr = journalEntrySearchCommand.getFromDate() + "T00:00:00.000Z";                
                Date fromDate = formatter.parse(fromDateStr);
                query.addCriteria(Criteria.where("createdAt").gte(fromDate));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        List<JournalEntry> entries = mongoTemplate.find(query, JournalEntry.class);
        return new ObjectListResponseDto<>(true, "Entry records retrieved successfully!", entries);
    }
}