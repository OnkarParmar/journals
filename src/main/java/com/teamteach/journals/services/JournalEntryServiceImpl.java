package com.teamteach.journals.services;
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
            /*SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());*/
            journalEntry = JournalEntry.builder()
                    .entryId(sequenceGeneratorService.generateSequence(JournalEntry.SEQUENCE_NAME))
                    .text(journalEntryRequestDto.getText())
                    .children(journalEntryRequestDto.getChildren())
                    .category(journalEntryRequestDto.getCategory())
                    .mood(journalEntryRequestDto.getMood())
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
    public ObjectListResponseDto<JournalEntry> findAllEntries() {
        Query query = new Query();
        List<JournalEntry> entries = mongoTemplate.find(query, JournalEntry.class);
        return new ObjectListResponseDto<>(
                true,
                "Entries retrieved successfully!",
                entries);
    }
}
