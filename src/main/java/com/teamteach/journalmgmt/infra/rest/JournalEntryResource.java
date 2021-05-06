package com.teamteach.journalmgmt.infra.rest;

import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntriesResponse;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import java.util.List;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.infra.api.IJournalEntryResource;
import com.teamteach.journalmgmt.shared.AbstractAppController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
class JournalEntryResource extends AbstractAppController implements IJournalEntryResource {
    
    final IJournalEntryMgmt journalEntryMgmt;

    @Override
    @ApiOperation(value = "Creates the entry", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> saveEntry(JournalEntryCommand journalEntryCommand) {
        return ResponseEntity.ok(journalEntryMgmt.saveEntry(journalEntryCommand));
    }

    @Override
    @ApiOperation(value = "Finds entries with search filters", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectListResponseDto<JournalEntriesResponse>> searchEntries(JournalEntrySearchCommand journalEntrySearchCommand) {
        return ResponseEntity.ok(journalEntryMgmt.searchEntries(journalEntrySearchCommand));
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
    @ApiOperation(value = "Edits entries", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> editEntry(EditJournalEntryCommand editJournalEntryCommand){
        return ResponseEntity.ok(journalEntryMgmt.editEntry(editJournalEntryCommand));
    }

    @Override
    @ApiOperation(value = "Adds image", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> addImage(String entryId, MultipartFile file){
        return ResponseEntity.ok(journalEntryMgmt.saveTeamTeachFile(file, entryId));
    }
}