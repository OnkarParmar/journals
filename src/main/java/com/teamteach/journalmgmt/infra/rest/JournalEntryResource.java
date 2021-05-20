package com.teamteach.journalmgmt.infra.rest;

import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntriesResponse;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

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
    @ApiOperation(value = "Sends entries report with search filters", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> sendEntriesReport(JournalEntrySearchCommand journalEntrySearchCommand, HttpHeaders headers) {
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(journalEntryMgmt.sendEntriesReport(journalEntrySearchCommand, token));
    }

    @Override
    @ApiOperation(value = "Uploads url of report", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> uploadPDF(String journalId) {
        return ResponseEntity.ok(journalEntryMgmt.uploadReport(journalId));
    }

    @Override
    @ApiOperation(value = "Finds entries", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectListResponseDto<JournalEntryResponse>> findAllEntries() {
        return ResponseEntity.ok(journalEntryMgmt.findAllEntries());
    }

    @Override
    @ApiOperation(value = "Finds entry by id", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> findEntryById(String id) {
        return ResponseEntity.ok(journalEntryMgmt.findById(id));
    }

    @Override
    @ApiOperation(value = "Deletes entries", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> deleteEntry(String id) {
        return ResponseEntity.ok(journalEntryMgmt.delete(id));
    }

    @Override
    @ApiOperation(value = "Saves entry", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> saveEntry(String journalId,
                                                        String entryId,
                                                        MultipartFile file,
                                                        String mood,
                                                        String text,
                                                        String[] children,
                                                        String categoryId ){        
        EditJournalEntryCommand editJournalEntryCommand = new EditJournalEntryCommand(journalId,entryId,mood,text,children,categoryId,file);
        return ResponseEntity.ok(journalEntryMgmt.saveEntry(editJournalEntryCommand));
    }
}