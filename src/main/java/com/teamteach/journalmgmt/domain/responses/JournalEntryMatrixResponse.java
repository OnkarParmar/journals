package com.teamteach.journalmgmt.domain.responses;

import java.util.ArrayList;
import java.util.List;

import com.teamteach.journalmgmt.domain.models.*;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class JournalEntryMatrixResponse  {
    private List<ChildProfile> childProfiles;
    private List<Category> categories;
    private List<JournalEntriesResponse> journalEntryMatrix;
}