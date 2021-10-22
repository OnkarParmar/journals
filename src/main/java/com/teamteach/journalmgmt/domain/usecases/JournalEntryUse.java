package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.responses.*;
import com.teamteach.journalmgmt.infra.persistence.dal.JournalDAL;
import com.teamteach.commons.security.jwt.JwtOperationsWrapperSvc;
import com.teamteach.commons.security.jwt.JwtUser;

import org.springframework.beans.factory.annotation.Autowired;
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
	private MoodsService moodsService;

    @Autowired
    private JournalDAL journalDAL;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

	@Autowired
	private ProfileService profileService;

    @Autowired
    private JwtOperationsWrapperSvc jwtOperationsWrapperSvc;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    public ObjectResponseDto lock(String id) {
        HashMap<SearchKey,Object> searchCriteria = new HashMap<>();
        searchCriteria.put(new SearchKey("_id",false),id);
        List<JournalEntry> journalEntries = journalDAL.getJournalEntries(searchCriteria);
        JournalEntry entry = journalEntries.isEmpty() ? null : journalEntries.get(0);
        if(entry.isLocked()){
            entry.setLocked(false);
            journalDAL.saveJournalEntry(entry);
            return ObjectResponseDto.builder()
                        .success(true)
                        .message("Entry unlocked")
                        .object(entry)
                        .build();
        } else {
            entry.setLocked(true);
            journalDAL.saveJournalEntry(entry);
            return ObjectResponseDto.builder()
                        .success(true)
                        .message("Entry locked")
                        .object(entry)
                        .build();
        }        
    }

    @Override
    public ObjectResponseDto delete(String id) {
        HashMap<SearchKey,Object> searchCriteria = new HashMap<>();
        searchCriteria.put(new SearchKey("_id",false),id);
        List<JournalEntry> journalEntries = journalDAL.getJournalEntries(searchCriteria);
        JournalEntry entry = journalEntries.isEmpty() ? null : journalEntries.get(0);

        searchCriteria = new HashMap<>();
        searchCriteria.put(new SearchKey("journalId",false),entry.getJournalId());
        List<Journal> journals = journalDAL.getJournals(searchCriteria);
        Journal journal = journals.isEmpty() ? null : journals.get(0);

        Date created = entry.getCreatedAt();
        boolean isEditable = isEditable(created);
        if(isEditable == true){
            try {
                journalDAL.removeJournalEntries("_id", id);
                JournalResponse journalResponse = new JournalResponse(journal);
                journalResponse.setMoods(moodsService.getMoodsCount(journal.getJournalId()));
                journalResponse.setEntryCount();        
                return ObjectResponseDto.builder()
                        .success(true)
                        .message("Entry deleted successfully")
                        .object(journalResponse)
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
    public ObjectResponseDto findById(String id, String accessToken) {
        HashMap<SearchKey,Object> searchCriteria = new HashMap<>();
        searchCriteria.put(new SearchKey("_id",false),id);
        List<JournalEntry> journalEntries = journalDAL.getJournalEntries(searchCriteria);
        JournalEntry entry = journalEntries.isEmpty() ? null : journalEntries.get(0);

        if (entry == null) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("An entry with this id does not exist!")
                    .build();
        } else {
            JournalEntryResponse journalEntryResponse = new JournalEntryResponse(entry);
            Map<String, String> moodTable = new HashMap<>();
            searchCriteria = new HashMap<>();
            searchCriteria.put(new SearchKey("isParent",false),true);
            List<Mood> moods = journalDAL.getMoods(searchCriteria);
            for (Mood mood : moods) {
                moodTable.put(mood.getName(), mood.getUrl());
            }
            journalEntryResponse.setMood(moodTable.get(entry.getMood()));
            if (entry.getRecommendationId() != null) {
                Recommendation recommendation = recommendationService.getSuggestion(accessToken, entry);
                if (recommendation != null) {
                    journalEntryResponse.setSuggestion(recommendation.getSuggestion());
                    journalEntryResponse.setUrl(recommendation.getUrl());    
                }
            }
    
            return ObjectResponseDto.builder()
                    .success(true)
                    .message("Entry record retrieved!")
                    .object(journalEntryResponse)
                    .build();
        }
    }

    @Override
    public ObjectResponseDto getLastSuggestion(String id, String token) {
        String[] tokens = token.split(" ");
        JwtUser jwtUser = jwtOperationsWrapperSvc.validateToken(tokens[1]);
        JournalEntry journalEntry = journalDAL.getLastSuggestionEntry(id, jwtUser.getPrincipal());

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
        HashMap<SearchKey,Object> searchCriteria = new HashMap<>();
        searchCriteria.put(new SearchKey("journalId",false),editJournalEntryCommand.getJournalId());
        List<Journal> journals = journalDAL.getJournals(searchCriteria);
        Journal journal = journals.isEmpty() ? null : journals.get(0);
        Date now = new Date(System.currentTimeMillis());
        if (journal == null) {
            return ObjectResponseDto.builder()
                    .success(false)
                    .message("No journal exists with given journalId!")
                    .build();
        }
        if(editJournalEntryCommand.getEntryId() != null){
            entryId = editJournalEntryCommand.getEntryId();
            searchCriteria = new HashMap<>();
            searchCriteria.put(new SearchKey("_id",false),editJournalEntryCommand.getEntryId());
            List<JournalEntry> journalEntries = journalDAL.getJournalEntries(searchCriteria);
            entry = journalEntries.isEmpty() ? null : journalEntries.get(0);
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
        journalDAL.saveJournal(journal, false);
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
        journalDAL.saveJournalEntry(entry);
		JournalResponse journalResponse = new JournalResponse(journal);
		journalResponse.setMoods(moodsService.getMoodsCount(journal.getJournalId()));
		journalResponse.setEntryCount();
        return ObjectResponseDto.builder()
                                .success(true)
                                .message("Entry saved successfully")
                                .object(journalResponse)
                                .build();
    }

    @Override
    public ObjectResponseDto searchEntries(JournalEntrySearchCommand journalEntrySearchCommand, String accessToken) {
        String[] tokens = accessToken.split(" ");
        JwtUser jwtUser = jwtOperationsWrapperSvc.validateToken(tokens[1]);
        String ownerId = jwtUser.getPrincipal();
        if (ownerId == null || ownerId.equals("")) {
            return new ObjectResponseDto(
                                         false,
                                         "Owner ID is necessary to search entries",
                                         null
            );
        }

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

        HashMap<SearchKey,Object> searchCriteria = new HashMap<>();
        HashMap<SearchKey,Object> containCriteria = new HashMap<>();

        if (journalEntrySearchCommand.getEntryId() != null) {
            searchCriteria.put(new SearchKey("entryId",false),journalEntrySearchCommand.getEntryId());
        }
        searchCriteria.put(new SearchKey("ownerId",true),ownerId);
        if (journalEntrySearchCommand.getMoods() != null && !journalEntrySearchCommand.getMoods().isEmpty()) {
            containCriteria.put(new SearchKey("mood",false),journalEntrySearchCommand.getMoods());
        }
        if (journalEntrySearchCommand.getCategories() != null && !journalEntrySearchCommand.getCategories().isEmpty()) {
            containCriteria.put(new SearchKey("categoryId",false),journalEntrySearchCommand.getCategories());
        }
        if (journalEntrySearchCommand.getChildren() != null && !journalEntrySearchCommand.getChildren().isEmpty()) {
            containCriteria.put(new SearchKey("children",false),journalEntrySearchCommand.getChildren());
        }
        List<JournalEntry> entries = journalDAL.getSearchJournalEntries(searchCriteria, containCriteria, fromDate, toDate);
        List<JournalEntriesResponse> journalEntriesGrid = new ArrayList<>();
        List<ChildProfile> childProfiles = null;
        Map<String, Category> categories = null;
        Map<String, ChildProfile> childTable = new HashMap<>();
        String timeZone = "UTC";
        ParentProfileResponseDto parentProfile = profileService.getProfile(ownerId, accessToken);
        if (parentProfile != null) {
            timeZone = parentProfile.getTimezone();
        } else {
            System.out.println("Profile fetch failed!");
            System.out.println(journalEntrySearchCommand);
        }
        if (journalEntrySearchCommand.isGoalReport()) {
            childProfiles = profileService.getProfile(ownerId, accessToken).getChildren();
            for (ChildProfile childProfile : childProfiles) {
                childTable.put(childProfile.getProfileId(), childProfile);
            }
            childProfiles = new ArrayList<ChildProfile>();
            for (ChildProfile childProfile :  childTable.values()) {
                childProfiles.add(ChildProfile.builder()
                                            .name(childProfile.getName())
                                            .profileImage(childProfile.getProfileImage())
                                            .build()
                                    );
                childProfile.setName(childProfile.getName());
            }
            categories = recommendationService.getCategories(accessToken);
        }
        Map<String, String> moodTable = new HashMap<>();
        searchCriteria = new HashMap<>();
        searchCriteria.put(new SearchKey("isParent",false),true);
        List<Mood> moods = journalDAL.getMoods(searchCriteria);
        for (Mood mood : moods) {
            moodTable.put(mood.getName(), mood.getUrl());
        }
        if (journalEntrySearchCommand.getViewMonth() == null) {
            for (JournalEntry entry : entries) {
                JournalEntriesResponse journalEntriesResponse = new JournalEntriesResponse();
                JournalEntryResponse journalEntryResponse = new JournalEntryResponse(entry,timeZone);
                ArrayList<ChildProfile> listChild = new ArrayList<ChildProfile>();

                if (journalEntrySearchCommand.isGoalReport()) {
                    for(String child: entry.getChildren()) {
                        listChild.add(childTable.get(child));
                    }
                    listChild.sort((o1, o2)-> o1.getName().compareTo(o2.getName()));
                    for(ChildProfile child: listChild) {
                        journalEntryResponse.addChildProfile(child);
                    }
                    Category category = categories.get(entry.getCategoryId());
                    if(category != null){
                        journalEntryResponse.setCategoryId(category.getCategoryId());
                    }
                }
                journalEntryResponse.setMood(moodTable.get(entry.getMood()));
                if (entry.getRecommendationId() != null) {
                    Recommendation recommendation = recommendationService.getSuggestion(accessToken, entry);
                    if (recommendation != null) {
                        journalEntryResponse.setSuggestion(recommendation.getSuggestion());
                        journalEntryResponse.setUrl(recommendation.getUrl());    
                    }
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

                JournalEntryResponse journalEntryResponse = new JournalEntryResponse(entry,timeZone);

                journalEntryResponse.setCategoryId(entry.getCategoryId());
                if (!journalEntrySearchCommand.isSummaryOnly()) {
                    journalEntryResponse.setMood(moodTable.get(entry.getMood()));
                    if (entry.getRecommendationId() != null) {
                        Recommendation recommendation = recommendationService.getSuggestion(accessToken, entry);
                        if (recommendation != null) {
                            journalEntryResponse.setSuggestion(recommendation.getSuggestion());
                            journalEntryResponse.setUrl(recommendation.getUrl());    
                        }
                    }
                }
                journalEntriesResponse.addEntry(journalEntryResponse);
            }
        }
        JournalEntryMatrixResponse journalEntryMatrix = new JournalEntryMatrixResponse(childProfiles, categories, journalEntriesGrid);
        return new ObjectResponseDto(true, "Entry records retrieved successfully!", journalEntryMatrix);
    }
}