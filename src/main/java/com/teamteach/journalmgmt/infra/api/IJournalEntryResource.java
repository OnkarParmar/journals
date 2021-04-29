package com.teamteach.journalmgmt.infra.api;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RequestMapping("journals/entry")
public interface IJournalEntryResource {

    @PostMapping("/create")
    ResponseEntity<ObjectResponseDto> saveEntry(@RequestBody JournalEntryCommand journalEntryCommand);

    @PostMapping("/search")
    ResponseEntity<ObjectListResponseDto<List<JournalEntryResponse>>> searchEntries(@RequestBody JournalEntrySearchCommand journalEntrySearchCommand);

    @ApiIgnore
    @GetMapping("/getAll")
    ResponseEntity<ObjectListResponseDto<JournalEntryResponse>> findAllEntries();

    @ApiIgnore
    @GetMapping("")
    ResponseEntity<ObjectResponseDto> findEntryById(@PathVariable("id") String id);

    @ApiIgnore
    @DeleteMapping("/{id}")
    ResponseEntity<ObjectResponseDto> deleteEntry(@PathVariable("id") String id);
}
