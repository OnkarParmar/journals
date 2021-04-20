package com.teamteach.journalmgmt.domain.ports.in;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.responses.JournalResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;
import com.teamteach.journalmgmt.domain.usecases.*;

public interface IJournalMgmt {
    ObjectResponseDto saveMaster(JournalCommand journalCommand);
    ObjectListResponseDto<JournalResponse> findAll();
    ObjectResponseDto delete(String id);
    ObjectResponseDto findByTitle(String title);
    ObjectListResponseDto findById(String id);
    ObjectResponseDto savePrivate(JournalCommand journalCommand);
}
