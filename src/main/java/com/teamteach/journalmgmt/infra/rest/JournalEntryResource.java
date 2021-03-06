package com.teamteach.journalmgmt.infra.rest;

import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntriesResponse;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.infra.api.IJournalEntryResource;
import com.teamteach.journalmgmt.shared.AbstractAppController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;


import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
class JournalEntryResource extends AbstractAppController implements IJournalEntryResource {
    
    final IJournalEntryMgmt journalEntryMgmt;

    @Override
    @ApiOperation(value = "Finds entries with search filters", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> searchEntries(JournalEntrySearchCommand journalEntrySearchCommand, HttpHeaders headers) {
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(journalEntryMgmt.searchEntries(journalEntrySearchCommand,token));
    }

    @Override
    @ApiOperation(value = "Finds entry by id", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> findEntryById(String id, HttpHeaders headers) {
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(journalEntryMgmt.findById(id, token));
    }

    @Override
    @ApiOperation(value = "Finds last suggestion by id", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> getLastSuggestion(String id, HttpHeaders headers) {
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(journalEntryMgmt.getLastSuggestion(id, token));
    }

    @Override
    @ApiOperation(value = "Deletes entry", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> deleteEntry(String id) {
        return ResponseEntity.ok(journalEntryMgmt.delete(id));
    }

    @Override
    @ApiOperation(value = "Lock entry", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> lockEntry(String id) {
        return ResponseEntity.ok(journalEntryMgmt.lock(id));
    }

    @Override
    @ApiOperation(value = "Saves entry", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> saveEntry(String journalId,
                                                        String entryId,
                                                        MultipartFile file,
                                                        String mood,
                                                        String text,
                                                        String[] children,
                                                        String categoryId,
                                                        String recommendationId,
                                                        String suggestionIndex){        
        EditJournalEntryCommand editJournalEntryCommand = new EditJournalEntryCommand(journalId,entryId,mood,
                                                                                        text,children,categoryId,file,
                                                                                        recommendationId,suggestionIndex);
        return ResponseEntity.ok(journalEntryMgmt.saveEntry(editJournalEntryCommand));
    }
}