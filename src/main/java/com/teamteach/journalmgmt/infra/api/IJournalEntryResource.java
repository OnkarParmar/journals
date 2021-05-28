package com.teamteach.journalmgmt.infra.api;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.responses.JournalEntriesResponse;
import com.teamteach.journalmgmt.domain.responses.JournalEntryResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import springfox.documentation.annotations.ApiIgnore;
import org.springframework.http.HttpHeaders;

import org.springframework.web.multipart.MultipartFile;

@RequestMapping("journals/entry")
public interface IJournalEntryResource {

    @PostMapping("/search")
    ResponseEntity<ObjectResponseDto> searchEntries(@RequestBody JournalEntrySearchCommand journalEntrySearchCommand,
                                                                                @RequestHeader HttpHeaders headers);
    @ApiIgnore
    @GetMapping("/getAll")
    ResponseEntity<ObjectListResponseDto<JournalEntryResponse>> findAllEntries();

    @ApiIgnore
    @GetMapping("/{id}")
    ResponseEntity<ObjectResponseDto> findEntryById(@PathVariable("id") String id);

    @PostMapping("/sendReport/{journalId}")
    ResponseEntity<ObjectResponseDto> sendEntriesReport(@PathVariable String journalId, 
                                                        @RequestBody JournalEntryReportCommand journalEntryReportCommand, 
                                                        @RequestHeader HttpHeaders headers);

    @PostMapping("/getReport/{journalId}")
    ResponseEntity<ObjectResponseDto> uploadPDF(@PathVariable String journalId, 
                                                @RequestBody JournalEntrySearchCommand journalEntrySearchCommand, 
                                                @RequestHeader HttpHeaders headers);

    @PutMapping("/{id}/toggleLock")
    ResponseEntity<ObjectResponseDto> lockEntry(@PathVariable("id") String id);

    @DeleteMapping("/{id}")
    ResponseEntity<ObjectResponseDto> deleteEntry(@PathVariable("id") String id);

    @PostMapping("{journalId}")
    ResponseEntity<ObjectResponseDto> saveEntry(@PathVariable String journalId,
                                                @RequestParam(value = "entryId",required=false) String entryId,
                                                @RequestParam(value = "entryImage",required=false) MultipartFile file,
                                                @RequestParam(value = "mood",required=true) String mood,
                                                @RequestParam(value = "text",required=false) String text,
                                                @RequestParam(value = "children[]",required=true) String[] children,
                                                @RequestParam(value = "categoryId",required=true) String categoryId,
                                                @RequestParam(value = "recommendationId",required=true) String recommendationId,
                                                @RequestParam(value = "suggestionIndex",required=true) String suggestionIndex);
}