package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.JournalEntriesResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.io.IOException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JournalEntryUse implements IJournalEntryMgmt {

    final IJournalEntryRepository journalEntryRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ICategoryMgmt categoryService;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private FileUploadService fileUploadService;

    // @Override
    // public ObjectResponseDto saveEntry(JournalEntryCommand journalEntryCommand) {
    //     if (journalEntryCommand.getChildren() == null || journalEntryCommand.getChildren().length == 0) {
    //         return ObjectResponseDto.builder()
    //                 .success(false)
    //                 .message("Journal Entry can't be created without a child!")
    //                 .build();
    //     }
    //     if (journalEntryCommand.getText() == null || journalEntryCommand.getText().equals("")) {
    //         return ObjectResponseDto.builder()
    //                 .success(false)
    //                 .message("Journal Entry can't be created without a text!")
    //                 .build();
    //     }
    //     if (journalEntryCommand.getCategoryId() == null || journalEntryCommand.getCategoryId().equals("")) {
    //         return ObjectResponseDto.builder()
    //                 .success(false)
    //                 .message("Journal Entry can't be created without a category!")
    //                 .build();
    //     }
    //     if (journalEntryCommand.getMood() == null || journalEntryCommand.getMood().equals("")) {
    //         return ObjectResponseDto.builder()
    //                 .success(false)
    //                 .message("Journal Entry can't be created without a mood!")
    //                 .build();
    //     }
    //     Date date = new Date(System.currentTimeMillis());
    //     Query query = new Query(Criteria.where("createdAt").gte(date));
    //     JournalEntry journalEntry = mongoTemplate.findOne(query, JournalEntry.class);

    //     Query journalQuery = new Query(Criteria.where("journalId").is(journalEntryCommand.getJournalId()));
    //     Journal journal = mongoTemplate.findOne(journalQuery, Journal.class);
    //     if (journal == null) {
    //         return ObjectResponseDto.builder()
    //                 .success(false)
    //                 .message("No journal exists with given journalId!")
    //                 .build();
    //     }

    //     for (String child : journalEntryCommand.getChildren()) {
    //         if (child.equals("")) {
    //             return ObjectResponseDto.builder()
    //             .success(false)
    //             .message("Child-id can not be blank!")
    //             .build();
    //         }
    //     }

    //     if (journalEntry != null) {
    //         return ObjectResponseDto.builder()
    //                 .success(false)
    //                 .message("An entry at the same time already exists!")
    //                 .build();
    //     } else {
    //         String entryId = sequenceGeneratorService.generateSequence(JournalEntry.SEQUENCE_NAME);
    //         int n = entryId.length();
    //         String categoryId = entryId.substring(n-1,n);
    //         journalEntry = JournalEntry.builder()
    //                 .entryId(entryId)
    //                 .ownerId(journal.getOwnerId())
    //                 .journalId(journalEntryCommand.getJournalId())
    //                 .text(journalEntryCommand.getText())
    //                 .children(journalEntryCommand.getChildren())
    //                 .categoryId(categoryId)
    //                 .mood(journalEntryCommand.getMood())
    //                 .createdAt(date)
    //                 .build();    
                    
    //         mongoTemplate.save(journalEntry);
    //         journal.setUpdatedAt(date);
    //         mongoTemplate.save(journal);
    //         return ObjectResponseDto.builder()
    //                 .success(true)
    //                 .message("Entry created successfully")
    //                 .object(journalEntry)
    //                 .build();
    //     }
    // }

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
        Query query = new Query(Criteria.where("_id").is(id));
        JournalEntry entry = mongoTemplate.findOne(query, JournalEntry.class);
        Date created = entry.getCreatedAt();
        boolean flag = isEditable(created);
        if(flag == true){
            try {
                mongoTemplate.remove(query, JournalEntry.class);
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
        } else {
            return ObjectResponseDto.builder()
                        .success(false)
                        .message("Entries older than 24 hours cannot be deleted!")
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
    public boolean isEditable(Date created){
        int MILLI_TO_HOUR = 1000 * 60 * 60;
        Date current = new Date();
        long diff = current.getTime() - created.getTime();
        if((diff/MILLI_TO_HOUR) > 24 )
            return false;
        else
            return true;
    }

    @Override
    public ObjectResponseDto saveEntry(EditJournalEntryCommand editJournalEntryCommand){
        if (editJournalEntryCommand.getEntryId() != null && editJournalEntryCommand.getEntryId().equals("")){
            return ObjectResponseDto.builder()
                        .success(false)
                        .message("EntryId cannot be empty")
                        .build();
        }
        JournalEntry entry = null;
        boolean flag = true;
        String entryId = null;
        Query journalQuery = new Query(Criteria.where("journalId").is(editJournalEntryCommand.getJournalId()));
        Journal journal = mongoTemplate.findOne(journalQuery, Journal.class);
        Date date = null;
        if (journal == null) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("No journal exists with given journalId!")
                    .build();
        }
        if(editJournalEntryCommand.getEntryId() != null){
            entryId = editJournalEntryCommand.getEntryId();
            Query query = new Query(Criteria.where("_id").is(editJournalEntryCommand.getEntryId()));
            entry = mongoTemplate.findOne(query, JournalEntry.class);
            if (entry == null) {
                return ObjectResponseDto.builder()
                            .success(false)
                            .message("No entry found with given entryId")
                            .object(entry)
                            .build();
            }
            date = entry.getCreatedAt();
            flag = isEditable(date);
        } else {
            entry = new JournalEntry();
            entryId = sequenceGeneratorService.generateSequence(JournalEntry.SEQUENCE_NAME);
            entry.setEntryId(entryId);  
            date = new Date(System.currentTimeMillis());
            entry.setCreatedAt(date);
            entry.setOwnerId(journal.getOwnerId());
            entry.setJournalId(journal.getJournalId());
        }        
        journal.setUpdatedAt(date);
        mongoTemplate.save(journal);    
        if(flag == true){
            if(editJournalEntryCommand.getMood() != null && !editJournalEntryCommand.getMood().equals("")){
                entry.setMood(editJournalEntryCommand.getMood());
            }
            if(editJournalEntryCommand.getText() != null && !editJournalEntryCommand.getText().equals("")){
                entry.setText(editJournalEntryCommand.getText());
            }
            if(editJournalEntryCommand.getCategoryId() != null && !editJournalEntryCommand.getCategoryId().equals("")){
                int n = entryId.length();
                String categoryId = entryId.substring(n-1,n);
                entry.setCategoryId(categoryId);
                //entry.setCategoryId(editJournalEntryCommand.getCategoryId());
            }
            if(editJournalEntryCommand.getChildren() != null && editJournalEntryCommand.getChildren().length != 0){
                entry.setChildren(editJournalEntryCommand.getChildren());
            }
            String url = null;
            if(editJournalEntryCommand.getEntryImage() != null){
                try {
                    String fileExt = FilenameUtils.getExtension(editJournalEntryCommand.getEntryImage().getOriginalFilename()).replaceAll("\\s", "");
                    String fileName = "journalEntry_"+entryId+"."+fileExt;
                    url = fileUploadService.saveTeamTeachFile("journalEntryImages", fileName.replaceAll("\\s", ""), IOUtils.toByteArray(editJournalEntryCommand.getEntryImage().getInputStream()));
                } catch (IOException ioe) {
                    return ObjectResponseDto.builder()
                                            .success(false)
                                            .message(ioe.getMessage())
                                            .build();
                }
                entry.setEntryImage(url);
            }            
        } else {
            return ObjectResponseDto.builder()
                        .success(false)
                        .message("Entries older than 24 hours cannot be edited!")
                        .build();
        }
        mongoTemplate.save(entry);
        return ObjectResponseDto.builder()
                                .success(true)
                                .message("Entry edited successfully")
                                .object(entry)
                                .build();
    }

    @Override
    public ObjectListResponseDto<JournalEntriesResponse> searchEntries(JournalEntrySearchCommand journalEntrySearchCommand) {
        Query query = new Query();
        SimpleDateFormat formatter = null;
        Date fromDate = null;
        Date toDate = null;
        Calendar cal = Calendar.getInstance();
        int firstDay = 0;
        int lastDay = 0;
        if (journalEntrySearchCommand.getViewMonth() == null) {
            if (journalEntrySearchCommand.getFromDate() != null && !journalEntrySearchCommand.getFromDate().equals("")){
                String fromDateStr = journalEntrySearchCommand.getFromDate();
                formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS\'Z\'");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                fromDateStr += "T00:00:00.000Z";                
                try {
                    fromDate = formatter.parse(fromDateStr);
                } catch(ParseException e){
                    e.printStackTrace();
                }
            }
            if (journalEntrySearchCommand.getToDate() != null && !journalEntrySearchCommand.getToDate().equals("")){
                String toDateStr = journalEntrySearchCommand.getToDate();
                toDateStr += "T23:59:59.999Z"; 
                try {
                    toDate = formatter.parse(toDateStr);
                } catch(ParseException e){
                    e.printStackTrace();
                }
            }
        } else {
            formatter = new SimpleDateFormat("MMMM yyyy");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                fromDate = formatter.parse(journalEntrySearchCommand.getViewMonth());
                cal.setTime(fromDate);
                firstDay = cal.get(Calendar.DAY_OF_WEEK)-1;
                cal.add(Calendar.MONTH, 1);
                toDate = cal.getTime();
                cal.add(Calendar.DATE, -1);
                lastDay = cal.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
            query.addCriteria(Criteria.where("categoryId").in(journalEntrySearchCommand.getCategories()));
        }
        if (journalEntrySearchCommand.getChildren() != null && !journalEntrySearchCommand.getChildren().isEmpty()) {
            query.addCriteria(Criteria.where("children").in(journalEntrySearchCommand.getChildren()));
        }
        if (fromDate != null && toDate != null) {
            query.addCriteria(Criteria.where("createdAt").lte(toDate).gte(fromDate));
        } else if (fromDate != null) {
            query.addCriteria(Criteria.where("createdAt").gte(fromDate));
        }
        List<JournalEntry> entries = mongoTemplate.find(query, JournalEntry.class);
        List<JournalEntriesResponse> journalEntriesGrid = new ArrayList<>();
        if (journalEntrySearchCommand.getViewMonth() == null) {
            for (JournalEntry entry : entries) {
                JournalEntriesResponse journalEntriesResponse = new JournalEntriesResponse();
                JournalEntryResponse journalEntryResponse = new JournalEntryResponse(entry);
                Category category = categoryService.findById(entry.getCategoryId());
                if(category != null){
                    journalEntryResponse.setCategory(category);
                }
                Date created = entry.getCreatedAt();
                journalEntriesResponse.addEntry(journalEntryResponse);
                journalEntriesResponse.setEditable(isEditable(created));
                journalEntriesGrid.add(journalEntriesResponse);
            }
        } else {
            for (int i = 1; i <= 42; i++) {
                JournalEntriesResponse journalEntriesResponse = new JournalEntriesResponse();
                journalEntriesResponse.setDay(i < firstDay ? 0 : i > (lastDay+firstDay) ? 0 : i-firstDay);
                journalEntriesGrid.add(journalEntriesResponse);
            }
            for (JournalEntry entry : entries) {
                Date entryDate = entry.getCreatedAt();
                cal.setTime(entryDate);
                int day = cal.get(Calendar.DAY_OF_MONTH)-1;
                JournalEntriesResponse journalEntriesResponse = journalEntriesGrid.get(firstDay+day);

                JournalEntryResponse journalEntryResponse = new JournalEntryResponse(entry);
                Category category = categoryService.findById(entry.getCategoryId());
                if(category != null){
                    journalEntryResponse.setCategory(category);
                }
                journalEntriesResponse.addEntry(journalEntryResponse);
            }
        }
        return new ObjectListResponseDto<JournalEntriesResponse>(true, "Entry records retrieved successfully!", journalEntriesGrid);
    }

}