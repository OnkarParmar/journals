package com.teamteach.journalmgmt.infra.rest;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.responses.JournalResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.domain.models.*;
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
    final IMoodMgmt moodMgmt;

    @Override
    @ApiOperation(value = "Creates the journal", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> createJournal(@Valid JournalCommand journalCommand){
        if(journalCommand.getOwnerId() == null) //creating a master copy 
                return ResponseEntity.ok(journalMgmt.createJournal(journalCommand));
        else // retrieve the existing journal of the owner with the ownerId
        {
            return ResponseEntity.ok(journalMgmt.savePrivate(journalCommand));
        }
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
    @ApiOperation(value = "Creates mood", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> saveMood(Mood mood) {
        return ResponseEntity.ok(moodMgmt.saveMood(mood));
    }

    @Override
    @ApiOperation(value = "Finds moods", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectListResponseDto> findAllMoods() {
        return ResponseEntity.ok(moodMgmt.findMoods());
    }

    @Override
    @ApiOperation(value = "Finds mood by id", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> findMoodById(String id) {
        return ResponseEntity.ok(moodMgmt.findMoodById(id));
    }

    @Override
    @ApiOperation(value = "Finds mood by name", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> findMoodByName(String name) {
        return ResponseEntity.ok(moodMgmt.findByName(name));
    }

    @Override
    @ApiOperation(value = "Deletes mood", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> deleteMood(String id) {
        return ResponseEntity.ok(moodMgmt.deleteMood(id));
    }
}
