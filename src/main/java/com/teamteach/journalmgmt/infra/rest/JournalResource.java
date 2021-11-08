package com.teamteach.journalmgmt.infra.rest;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.models.SendReportInfo;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.responses.JournalDashboardResponse;
import com.teamteach.journalmgmt.domain.responses.JournalResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;
import com.teamteach.journalmgmt.infra.api.IJournalResource;
import com.teamteach.journalmgmt.shared.AbstractAppController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
class JournalResource extends AbstractAppController implements IJournalResource {

    final IJournalMgmt journalMgmt;

    @Override
    @ApiOperation(value = "Creates the journal", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> createJournal(@Valid JournalCommand journalCommand){
        // if(journalCommand.getOwnerId() == null) //creating a master copy 
                return ResponseEntity.ok(journalMgmt.createJournal(journalCommand));
        // else // retrieve the existing journal of the owner with the ownerId
        // {
        //     return ResponseEntity.ok(journalMgmt.savePrivate(journalCommand));
        // }
    }

    @Override
    @ApiOperation(value = "Deletes journals", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> deleteJournal(String id){
        return ResponseEntity.ok(journalMgmt.delete(id));
    }

    @Override
    @ApiOperation(value = "Finds journal by ownerId", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectListResponseDto<JournalResponse>> findJournalById(HttpHeaders headers, String ownerId){
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(journalMgmt.findById(ownerId, token));
    }

    @Override
    @ApiOperation(value = "Sends entries report with search filters", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> sendReport(SendReportInfo sendReportInfo, HttpHeaders headers) {
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);        
        return ResponseEntity.ok(journalMgmt.sendReport(sendReportInfo, token));
    }

    @Override
    @ApiOperation(value = "Uploads url of report", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> buildReport(String journalId, JournalEntrySearchCommand journalEntrySearchCommand, HttpHeaders headers) {
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(journalMgmt.buildReport(journalId,journalEntrySearchCommand,token));
    }

    @Override
    @ApiOperation(value = "Gets Journal Dashboard", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectListResponseDto<JournalDashboardResponse>> getJournalDashboard(HttpHeaders headers){
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(journalMgmt.getJournalDashboard(token));
    }
}
