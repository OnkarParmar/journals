package com.teamteach.journalmgmt.domain.responses;

import java.util.ArrayList;
import java.util.List;

import com.teamteach.journalmgmt.domain.models.ChildProfile;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class JournalEntryMatrixResponse  {
    private List<ChildProfile> childProfiles;
    private List<JournalEntriesResponse> journalEntryMatrix;
}