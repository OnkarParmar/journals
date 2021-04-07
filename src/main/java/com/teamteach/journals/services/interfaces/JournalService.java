package com.teamteach.journals.services.interfaces;

import com.teamteach.journals.models.entities.*;
import com.teamteach.journals.models.requests.*;
import com.teamteach.journals.models.responses.*;

public interface JournalService {
    ObjectResponseDto save(JournalRequestDto journalRequestDto);

    ObjectResponseDto saveEntry(JournalRequestDto journalRequestDto);

    ObjectListResponseDto<Journal> findAll();

    ObjectResponseDto delete(String id);

    ObjectResponseDto findByName(String name);

    ObjectResponseDto findById(String id);
}
