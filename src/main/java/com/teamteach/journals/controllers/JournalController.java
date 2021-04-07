package com.teamteach.journals.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamteach.journals.models.entities.*;
import com.teamteach.journals.models.requests.*;
import com.teamteach.journals.models.responses.*;
import com.teamteach.journals.services.interfaces.JournalService;
import com.teamteach.journals.services.interfaces.JournalEntryService;

@RestController
@RequestMapping("")
public class JournalController {

    @Autowired
        private JournalService journalService;

    @Autowired
        private JournalEntryService journalEntryService;

    @PostMapping("")
        public ResponseEntity<ObjectResponseDto> save(@RequestBody JournalRequestDto journalRequestDto) {
            return ResponseEntity.ok(journalService.save(journalRequestDto));
        }

    @PostMapping("/entry")
        public ResponseEntity<ObjectResponseDto> saveEntry(@RequestBody JournalEntryRequestDto journalEntryRequestDto) {
            return ResponseEntity.ok(journalEntryService.saveEntry(journalEntryRequestDto));
        }

    @GetMapping("")
        public ResponseEntity<ObjectListResponseDto<Journal>> findAll() {
            return ResponseEntity.ok(journalService.findAll());
        }

    @GetMapping("/entry")
        public ResponseEntity<ObjectListResponseDto<JournalEntry>> findAllEntries() {
            return ResponseEntity.ok(journalEntryService.findAllEntries());
        }

    @GetMapping("/{id}")
        public ResponseEntity<ObjectResponseDto> find(@PathVariable("id") String id) {
            return ResponseEntity.ok(journalService.findById(id));
        }    

    @DeleteMapping("/{id}")
        public ResponseEntity<ObjectResponseDto> delete(@PathVariable("id") String id) {
            return ResponseEntity.ok(journalService.delete(id));
        }

}
