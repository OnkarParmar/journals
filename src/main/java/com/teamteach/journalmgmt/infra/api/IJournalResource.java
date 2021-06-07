package com.teamteach.journalmgmt.infra.api;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.responses.JournalResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RequestMapping("journals")
public interface IJournalResource {

    @ApiIgnore
    @PostMapping("/create")
        ResponseEntity<ObjectResponseDto> createJournal(@RequestBody @Valid JournalCommand journalCommand);

    @GetMapping("/owner/{ownerId}")
        ResponseEntity<ObjectListResponseDto<JournalResponse>> findJournalById(@RequestHeader HttpHeaders headers, @PathVariable String ownerId) ;
    
    @ApiIgnore
    @DeleteMapping("/{id}")
        ResponseEntity<ObjectResponseDto> deleteJournal(@PathVariable String id) ;

    @PostMapping("/sendReport/{journalId}")
    ResponseEntity<ObjectResponseDto> sendReport(@PathVariable String journalId, 
                                                        @RequestBody JournalEntryReportCommand journalEntryReportCommand, 
                                                        @RequestHeader HttpHeaders headers);

    @PostMapping("/buildReport/{journalId}")
    ResponseEntity<ObjectResponseDto> buildReport(@PathVariable String journalId, 
                                                @RequestBody JournalEntrySearchCommand journalEntrySearchCommand, 
                                                @RequestHeader HttpHeaders headers);
    
    
}
