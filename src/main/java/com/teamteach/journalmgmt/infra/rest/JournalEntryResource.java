package com.teamteach.journalmgmt.infra.rest;

import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;
import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.infra.api.IJournalEntryResource;
import com.teamteach.journalmgmt.infra.api.IJournalResource;
import com.teamteach.journalmgmt.shared.AbstractAppController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;

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
    public ResponseEntity<ObjectListResponseDto<JournalEntry>> searchEntries(JournalEntrySearchCommand journalEntrySearchCommand) {
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
}