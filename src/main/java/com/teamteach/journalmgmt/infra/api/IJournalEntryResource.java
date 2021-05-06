package com.teamteach.journalmgmt.infra.api;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntriesResponse;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import springfox.documentation.annotations.ApiIgnore;

import org.springframework.web.multipart.MultipartFile;

@RequestMapping("journals/entry")
public interface IJournalEntryResource {

    @PostMapping("/create")
    ResponseEntity<ObjectResponseDto> saveEntry(@RequestBody JournalEntryCommand journalEntryCommand);

    @PostMapping("/search")
    ResponseEntity<ObjectListResponseDto<JournalEntriesResponse>> searchEntries(@RequestBody JournalEntrySearchCommand journalEntrySearchCommand);

    @PostMapping("/picture/{entryId}")
    ResponseEntity<ObjectResponseDto> addImage(@PathVariable String entryId, @RequestParam("file") MultipartFile file);

    @ApiIgnore
    @GetMapping("/getAll")
    ResponseEntity<ObjectListResponseDto<JournalEntryResponse>> findAllEntries();

    @ApiIgnore
    @GetMapping("")
    ResponseEntity<ObjectResponseDto> findEntryById(@PathVariable("id") String id);

    @DeleteMapping("/{id}")
    ResponseEntity<ObjectResponseDto> deleteEntry(@PathVariable("id") String id);

    @PutMapping("")
    ResponseEntity<ObjectResponseDto> editEntry(@RequestBody EditJournalEntryCommand editJournalEntryCommand);
}
