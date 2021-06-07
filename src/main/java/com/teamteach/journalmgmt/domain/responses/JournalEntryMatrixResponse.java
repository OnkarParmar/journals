package com.teamteach.journalmgmt.domain.responses;

import java.util.List;
import java.util.Map;

import com.teamteach.journalmgmt.domain.models.*;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class JournalEntryMatrixResponse  {
    private List<ChildProfile> childProfiles;
    private Map<String, Category> categories;
    private List<JournalEntriesResponse> journalEntryMatrix;
}