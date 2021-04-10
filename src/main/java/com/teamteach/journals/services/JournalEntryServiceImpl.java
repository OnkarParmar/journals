package com.teamteach.journals.services;

import java.text.SimpleDateFormat;
import java.util.*;

import com.teamteach.journals.models.entities.*;
import com.teamteach.journals.models.requests.*;
import com.teamteach.journals.models.responses.*;
import com.teamteach.journals.services.interfaces.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class JournalEntryServiceImpl implements JournalEntryService {
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ObjectResponseDto saveEntry(JournalEntryRequestDto journalEntryRequestDto) {
        Query query = new Query(Criteria.where("text").is(journalEntryRequestDto.getText()));
        JournalEntry journalEntry = mongoTemplate.findOne(query, JournalEntry.class);

        if (journalEntry != null) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("An entry with this text already exists!")
                    .build();
        } else {
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm:ss");
			Date date = new Date(System.currentTimeMillis());
            journalEntry = JournalEntry.builder()
                    .entryId(sequenceGeneratorService.generateSequence(JournalEntry.SEQUENCE_NAME))
                    .text(journalEntryRequestDto.getText())
                    .children(journalEntryRequestDto.getChildren())
                    .category(journalEntryRequestDto.getCategory())
                    .mood(journalEntryRequestDto.getMood())
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
    public ObjectListResponseDto<JournalEntry> searchEntries(JournalEntrySearchDto journalEntrySearchDto) {
        Query query = new Query();
        if (journalEntrySearchDto.getMoods() != null) {
            query.addCriteria(Criteria.where("mood").in(journalEntrySearchDto.getMoods()));
        }
        if (journalEntrySearchDto.getCategories() != null) {
            query.addCriteria(Criteria.where("category").in(journalEntrySearchDto.getCategories()));
        }
        if (journalEntrySearchDto.getChildren() != null) {
            query.addCriteria(Criteria.where("children").in(journalEntrySearchDto.getChildren()));
        }
        if (journalEntrySearchDto.getFromDate() != null) {
            query.addCriteria(Criteria.where("fromDate").is(journalEntrySearchDto.getFromDate()));
        }
        if (journalEntrySearchDto.getToDate() != null) {
            query.addCriteria(Criteria.where("toDate").is(journalEntrySearchDto.getToDate()));
        }
        List<JournalEntry> entries = mongoTemplate.find(query, JournalEntry.class);
        return new ObjectListResponseDto<>(true, "Entry records retrieved successfully!", entries);
    }
}
