package com.teamteach.journals.services.interfaces;

import com.teamteach.journals.models.entities.*;
import com.teamteach.journals.models.requests.*;
import com.teamteach.journals.models.responses.*;

public interface JournalService {
    ObjectResponseDto save(JournalRequestDto journalRequestDto);
    ObjectListResponseDto<JournalResponse> findAll();
    ObjectResponseDto delete(String id);
    ObjectResponseDto findByTitle(String title);
    ObjectResponseDto findById(String id);  
}
