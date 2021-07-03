package com.teamteach.journalmgmt.domain.responses;

import lombok.Builder;
import lombok.Data;
import java.text.SimpleDateFormat;
import java.util.*;

import com.teamteach.journalmgmt.domain.models.*;

@Data
public class JournalEntryResponse  implements Comparable<JournalEntryResponse> {
    protected String entryId;
    private String text;
    private String createdAt;
    private String createdAtTime;
    private Date createdDate;
    private String journalId;
    private String ownerId;
    private String mood;
    private List<String> children;
    private List<ChildProfile> childProfiles;
    private String categoryId;
    private String entryImage;
    private boolean editable;
    private boolean locked;
    private String recommendationId;
    private String suggestionIndex; 
    private String suggestion;  

    @Builder
    public JournalEntryResponse(JournalEntry journalEntry){
        SimpleDateFormat formatter= new SimpleDateFormat("dd MMM, yyyy");
        SimpleDateFormat formattertime = new SimpleDateFormat("h:mm a");
        this.entryId = journalEntry.getEntryId();
        this.text = journalEntry.getText();
        this.createdAt = formatter.format(journalEntry.getCreatedAt());
        this.createdAtTime = formattertime.format(journalEntry.getCreatedAt());
        this.createdDate = journalEntry.getCreatedAt();
        this.ownerId = journalEntry.getOwnerId();
        this.children = Arrays.asList(journalEntry.getChildren());
        this.journalId = journalEntry.getJournalId();
        this.categoryId = journalEntry.getCategoryId();
        this.entryImage = journalEntry.getEntryImage();
        this.locked = journalEntry.isLocked();
        this.recommendationId = journalEntry.getRecommendationId();
        this.suggestionIndex = journalEntry.getSuggestionIndex();

        this.childProfiles = new ArrayList<>();
    }

    public void addChildProfile(ChildProfile childProfile){
      this.childProfiles.add(childProfile);
    }

    @Override 
    public int compareTo(JournalEntryResponse u) {
      if (getCreatedDate() == null || u.getCreatedDate() == null) {
        return 0;
      }
      return getCreatedDate().compareTo(u.getCreatedDate());
    }
}