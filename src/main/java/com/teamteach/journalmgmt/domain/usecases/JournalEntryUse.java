package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;

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
        if (journalEntryCommand.getChildren() == null || journalEntryCommand.getChildren().length == 0) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("Journal Entry can't be created without a child!")
                    .build();
        }
        if (journalEntryCommand.getText() == null || journalEntryCommand.getText().equals("")) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("Journal Entry can't be created without a text!")
                    .build();
        }
        if (journalEntryCommand.getText() == null || journalEntryCommand.getText().equals("")) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("Journal Entry can't be created without a text!")
                    .build();
        }
        if (journalEntryCommand.getCategory() == null || journalEntryCommand.getCategory().equals("")) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("Journal Entry can't be created without a category!")
                    .build();
        }
        if (journalEntryCommand.getMood() == null || journalEntryCommand.getMood().equals("")) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("Journal Entry can't be created without a mood!")
                    .build();
        }
        Date date = new Date(System.currentTimeMillis());
        Query query = new Query(Criteria.where("createdAt").gte(date));
        JournalEntry journalEntry = mongoTemplate.findOne(query, JournalEntry.class);

        Query journalQuery = new Query(Criteria.where("journalId").is(journalEntryCommand.getJournalId()));
        Journal journal = mongoTemplate.findOne(journalQuery, Journal.class);
        if (journal == null) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("No journal exists with given journalId!")
                    .build();
        }

        for (String child : journalEntryCommand.getChildren()) {
            if (child.equals("")) {
                return ObjectResponseDto.builder()
                .success(false)
                .message("Child-id can not be blank!")
                .build();
            }
        }

        if (journalEntry != null) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("An entry at the same time already exists!")
                    .build();
        } else {
            journalEntry = JournalEntry.builder()
                    .entryId(sequenceGeneratorService.generateSequence(JournalEntry.SEQUENCE_NAME))
                    .ownerId(journal.getOwnerId())
                    .journalId(journalEntryCommand.getJournalId())
                    .text(journalEntryCommand.getText())
                    .children(journalEntryCommand.getChildren())
                    .category(journalEntryCommand.getCategory())
                    .mood(journalEntryCommand.getMood())
                    .createdAt(date)
                    .build();            
            mongoTemplate.save(journalEntry);
            journal.setUpdatedAt(date);
            mongoTemplate.save(journal);
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
        SimpleDateFormat formatter= new SimpleDateFormat("dd MMMM, yyyy   hh:mm aa");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String fromDateStr = null;
        String toDateStr = null;
        if (journalEntrySearchCommand.getFromDate() != null && !journalEntrySearchCommand.getFromDate().equals("")){
            fromDateStr = journalEntrySearchCommand.getFromDate();
        }
        if (journalEntrySearchCommand.getToDate() != null && !journalEntrySearchCommand.getToDate().equals("")){
            toDateStr = journalEntrySearchCommand.getToDate();
        }
        if (journalEntrySearchCommand.getEntryId() != null) {
            query.addCriteria(Criteria.where("entryId").is(journalEntrySearchCommand.getEntryId()));
        }
        if (journalEntrySearchCommand.getOwnerId() == null || journalEntrySearchCommand.getOwnerId().equals("")) {
           return new ObjectListResponseDto<>(
                                        false,
                                        "Owner ID is necessary to search entries",
                                        null
           );

        } else {
            query.addCriteria(Criteria.where("ownerId").is(journalEntrySearchCommand.getOwnerId()));
        }
        if (journalEntrySearchCommand.getMoods() != null && !journalEntrySearchCommand.getMoods().isEmpty()) {
            query.addCriteria(Criteria.where("mood").in(journalEntrySearchCommand.getMoods()));
        }
        if (journalEntrySearchCommand.getCategories() != null && !journalEntrySearchCommand.getCategories().isEmpty()) {
            query.addCriteria(Criteria.where("category").in(journalEntrySearchCommand.getCategories()));
        }
        if (journalEntrySearchCommand.getChildren() != null && !journalEntrySearchCommand.getChildren().isEmpty()) {
            query.addCriteria(Criteria.where("children").in(journalEntrySearchCommand.getChildren()));
        }
        if (fromDateStr != null && toDateStr != null) {
            try {
                fromDateStr += "T00:00:00.000Z";                
                Date fromDate = formatter.parse(fromDateStr);
                toDateStr += "T23:59:59.999Z"; 
                Date toDate = formatter.parse(toDateStr);
                query.addCriteria(Criteria.where("createdAt").lte(toDate).gte(fromDate));
            }
            catch(ParseException e){
                e.printStackTrace();
            }
        } else if (fromDateStr != null) {
            try {
                fromDateStr += "T00:00:00.000Z";                
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