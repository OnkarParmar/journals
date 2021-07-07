package com.teamteach.journalmgmt.domain.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.responses.*;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
public class HtmlService {

    private JournalEntryProfile report;

    @Autowired
    private IJournalEntryMgmt entryUse;

    @Autowired
    private ProfileService profileService;

    public JournalEntryProfile returnDataReport(String journalId, JournalEntrySearchCommand journalEntrySearchCommand, String accessToken) {
        ParentProfileResponseDto parentProfile = profileService.getProfile(journalEntrySearchCommand.getOwnerId(), accessToken);
        String email = journalEntrySearchCommand.getEmail() != null ? journalEntrySearchCommand.getEmail() : parentProfile.getEmail();
        ObjectResponseDto searchResponse = entryUse.searchEntries(journalEntrySearchCommand, accessToken);
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
        // JournalEntryProfile journalEntryProfile = JournalEntryProfile.builder()
        //                                                             .email(email)
        //                                                             .fname(parentProfile.getFname())
        //                                                             .lname(parentProfile.getLname())                                                            
        //                                                             .filterChildren(journalEntrySearchCommand.getChildren())
        //                                                             .children(childProfiles)
        //                                                             .entryList(entryList)
        //                                                             .build();                                                            
        return journalEntryProfile;
    }
}