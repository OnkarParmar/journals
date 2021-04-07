package com.teamteach.journals.services;

import com.teamteach.journals.models.requests.*;
import com.teamteach.journals.models.responses.*;
import com.teamteach.journals.services.interfaces.JournalEntryService;
import org.springframework.stereotype.Repository;

@Repository
public class JournalEntryServiceImpl implements JournalEntryService {

    @Override
    public ObjectResponseDto saveEntry(JournalEntryRequestDto journalEntryRequestDto) {
            return new ObjectResponseDto();
    }
}
