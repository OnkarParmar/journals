package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.responses.*;
import com.teamteach.journalmgmt.infra.external.JournalEntryReportService;
import com.lowagie.text.DocumentException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JournalEntryUse implements IJournalEntryMgmt {

    final IJournalEntryRepository journalEntryRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private JournalEntryReportService journalEntriesReportService;

    @Override
    public ObjectResponseDto lock(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        JournalEntry entry = mongoTemplate.findOne(query, JournalEntry.class);
        if(entry.isLocked()){
            entry.setLocked(false);
            mongoTemplate.save(entry);    
            return ObjectResponseDto.builder()
                        .success(true)
                        .message("Entry unlocked")
                        .object(entry)
                        .build();
        } else {
            entry.setLocked(true);
            mongoTemplate.save(entry);    
            return ObjectResponseDto.builder()
                        .success(true)
                        .message("Entry locked")
                        .object(entry)
                        .build();
        }        
    }

    @Override
    public ObjectResponseDto delete(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        JournalEntry entry = mongoTemplate.findOne(query, JournalEntry.class);
        Date created = entry.getCreatedAt();
        boolean isEditable = isEditable(created);
        if(isEditable == true){
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
        boolean isEditable = true;
        String entryId = null;
        Query journalQuery = new Query(Criteria.where("journalId").is(editJournalEntryCommand.getJournalId()));
        Journal journal = mongoTemplate.findOne(journalQuery, Journal.class);
        Date now = new Date(System.currentTimeMillis());
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
            Date createdAt = entry.getCreatedAt();
            isEditable = isEditable(createdAt);
        } else {
            entry = new JournalEntry();
            entryId = sequenceGeneratorService.generateSequence(JournalEntry.SEQUENCE_NAME);
            entry.setEntryId(entryId);  
            Date createdAt = now;
            entry.setCreatedAt(createdAt);
            entry.setOwnerId(journal.getOwnerId());
            entry.setJournalId(journal.getJournalId());
        }        
        entry.setUpdatedAt(now);
        journal.setUpdatedAt(now);
        mongoTemplate.save(journal);    
        if(isEditable == true){
            if(editJournalEntryCommand.getMood() != null && !editJournalEntryCommand.getMood().equals("")){
                entry.setMood(editJournalEntryCommand.getMood());
            }
            if(editJournalEntryCommand.getText() != null && !editJournalEntryCommand.getText().equals("")){
                entry.setText(editJournalEntryCommand.getText());
            }
            if(editJournalEntryCommand.getCategoryId() != null && !editJournalEntryCommand.getCategoryId().equals("")){
                entry.setCategoryId(editJournalEntryCommand.getCategoryId());
            } else {
                int n = entryId.length();
                String categoryId = entryId.substring(n-1,n);
                entry.setCategoryId(categoryId);
            }
            if(editJournalEntryCommand.getChildren() != null && editJournalEntryCommand.getChildren().length != 0){
                entry.setChildren(editJournalEntryCommand.getChildren());
            } else {
                return ObjectResponseDto.builder()
                        .success(false)
                        .message("Entry cannot be created without child")
                        .build();
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
            if (editJournalEntryCommand.getRecommendationId() != null) {
                entry.setRecommendationId(editJournalEntryCommand.getRecommendationId());
            }            
            if (editJournalEntryCommand.getSuggestionIndex() != null) {
                entry.setSuggestionIndex(editJournalEntryCommand.getSuggestionIndex());
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
                                .message("Entry saved successfully")
                                .object(entry)
                                .build();
    }

    @Override
    public ObjectResponseDto searchEntries(JournalEntrySearchCommand journalEntrySearchCommand, String accessToken) {
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
           return new ObjectResponseDto(
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
        List<ChildProfile> childProfiles = profileService.getProfile(journalEntrySearchCommand.getOwnerId(), accessToken).getChildren();
        Map<String, ChildProfile> childTable = new HashMap<>();
        for (ChildProfile childProfile : childProfiles) {
            childTable.put(childProfile.getProfileId(), childProfile);
        }
        Map<String, Category> categories = recommendationService.getCategories(accessToken);
        List<Category> categoryList = new ArrayList<Category>(categories.values());
        Map<String, String> moodTable = new HashMap<>();
        query = new Query(Criteria.where("isParent").is(true));
        List<Mood> moods = mongoTemplate.find(query, Mood.class);
        for (Mood mood : moods) {
            moodTable.put(mood.getName(), mood.getUrl());
        }
        if (journalEntrySearchCommand.getViewMonth() == null) {
            for (JournalEntry entry : entries) {
                JournalEntriesResponse journalEntriesResponse = new JournalEntriesResponse();
                JournalEntryResponse journalEntryResponse = new JournalEntryResponse(entry, moodTable);
                journalEntryResponse.setChildProfiles(childProfiles); //TODO : filter childProfiles as per entry.children
                Category category = categories.get(entry.getCategoryId());
                if(category != null){
                    journalEntryResponse.setCategory(category);
                }
                Date created = entry.getCreatedAt();
                journalEntryResponse.setEditable(isEditable(created));
                journalEntriesResponse.addEntry(journalEntryResponse);
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

                JournalEntryResponse journalEntryResponse = new JournalEntryResponse(entry, moodTable);
                Category category = categories.get(entry.getCategoryId());
                if(category != null){
                    journalEntryResponse.setCategory(category);
                }
                journalEntriesResponse.addEntry(journalEntryResponse);
            }
        }
        JournalEntryMatrixResponse journalEntryMatrix = new JournalEntryMatrixResponse(childProfiles, categoryList, journalEntriesGrid);
        return new ObjectResponseDto(true, "Entry records retrieved successfully!", journalEntryMatrix);
    }

    @Override
    public ObjectResponseDto sendEntriesReport(String journalId, JournalEntryReportCommand journalEntryReportCommand, String accessToken) {
        Query query = new Query(Criteria.where("journalId").is(journalId));
        Journal journal = mongoTemplate.findOne(query, Journal.class);
        String ownerId = journal.getOwnerId();
        ParentProfileResponseDto parentProfile = profileService.getProfile(ownerId, accessToken);
        String email = journalEntryReportCommand.getEmail() != null ? 
                                journalEntryReportCommand.getEmail() : parentProfile.getEmail();
        JournalEntryProfile journalEntryProfile = JournalEntryProfile.builder()
                                                                    .email(email)
                                                                    .fname(parentProfile.getFname())
                                                                    .lname(parentProfile.getLname())
                                                                    .action("sendreport")
                                                                    .url(journalEntryReportCommand.getUrl())
                                                                    .build();                                     
        journalEntriesReportService.sendJournalEntryReportEvent(journalEntryProfile, "event.sendreport");
        return new ObjectResponseDto(
            true,
            "Journal entries report URL sent successfully!",
            journalEntryProfile);
    }

    @Override
    public ObjectResponseDto uploadReport(String journalId, JournalEntrySearchCommand journalEntrySearchCommand, String accessToken) {
        ParentProfileResponseDto parentProfile = profileService.getProfile(journalEntrySearchCommand.getOwnerId(), accessToken);
        String email = journalEntrySearchCommand.getEmail() != null ? journalEntrySearchCommand.getEmail() : parentProfile.getEmail();

        ObjectResponseDto searchResponse = searchEntries(journalEntrySearchCommand, accessToken);
        Object object = searchResponse.getObject();    
        JournalEntryMatrixResponse journalEntryMatrixResponse = (JournalEntryMatrixResponse)object;
        List<JournalEntriesResponse> journalEntryMatrix = journalEntryMatrixResponse.getJournalEntryMatrix();
        List<JournalEntryResponse> entryList = new ArrayList<>();
        for(JournalEntriesResponse matrixEntries : journalEntryMatrix){
            //System.out.println(matrixEntries);
            if(matrixEntries.getEntries() != null && !matrixEntries.getEntries().isEmpty()){
                entryList.add(matrixEntries.getEntries().get(0));
            }
        }
        String children = journalEntryMatrixResponse.getChildProfiles().stream().map(e -> e.getName()).collect(Collectors.joining("| "));
        JournalEntryProfile journalEntryProfile = JournalEntryProfile.builder()
                                                                    .email(email)
                                                                    .fname(parentProfile.getFname())
                                                                    .lname(parentProfile.getLname())
                                                                    .fromDate(journalEntrySearchCommand.getFromDate())
                                                                    .toDate(journalEntrySearchCommand.getToDate()) 
                                                                    .children(children)                                                                    
                                                                    .entryList(entryList)
                                                                    .build();
        pdfService.setReport(journalEntryProfile);  
        //System.out.println(journalEntryProfile);
        String url = null;
        int i=0;
            try {
                String fileExt = FilenameUtils.getExtension(pdfService.generatePdf().getName()).replaceAll("\\s", "");
                String fileName = "journal_"+journalId+"_"+i+"."+fileExt;
                url = fileUploadService.saveTeamTeachFile("reports", fileName.replaceAll("\\s", ""),Files.readAllBytes(pdfService.generatePdf().toPath()));
                i++;
            } catch (IOException ioe) {
                return new ObjectResponseDto(
                    false,
                    "URL generation failed!",
                    null);
            } catch (DocumentException doe) {
                return new ObjectResponseDto(
                    false,
                    "URL generation failed!",
                    null);
            }
        return new ObjectResponseDto(
            true,
            "URL generated successfully!",
            url);
    }
}