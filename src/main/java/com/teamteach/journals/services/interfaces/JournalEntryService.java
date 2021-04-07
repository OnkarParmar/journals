package com.teamteach.journals.services.interfaces;

import com.teamteach.journals.models.entities.JournalEntry;
import com.teamteach.journals.models.requests.*;
import com.teamteach.journals.models.responses.*;

public interface JournalEntryService {

    ObjectResponseDto saveEntry(JournalEntryRequestDto journalEntryRequestDto);

    ObjectListResponseDto<JournalEntry> findAllEntries();
}
