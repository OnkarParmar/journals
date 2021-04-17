package com.teamteach.journalmgmt.infra.api;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;

@RequestMapping("journals")
public interface IJournalResource {

    @PostMapping("/create")
        ResponseEntity<ObjectResponseDto> saveJournal(@RequestBody @Valid JournalCommand journalCommand);

    // @GetMapping("/")
    //     ResponseEntity<ObjectListResponseDto<JournalResponse>> findAllJournals();

    @GetMapping("/parent/{ownerId}")
        ResponseEntity<ObjectListResponseDto<JournalResponse>> findJournalById(@PathVariable String ownerId) ;
    
    // @GetMapping("/title/{title}")
    //     ResponseEntity<ObjectResponseDto> findJournalByTitle(@PathVariable String title) ;

    @DeleteMapping("/{id}")
        ResponseEntity<ObjectResponseDto> deleteJournal(@PathVariable String id) ;

    @PostMapping("/moods")
        ResponseEntity<ObjectResponseDto> saveMood(@RequestBody Mood mood);

    @GetMapping("/moods")
        ResponseEntity<ObjectListResponseDto> findAllMoods();

    @GetMapping("/moods/id/{id}")
        ResponseEntity<ObjectResponseDto> findMoodById(@PathVariable("id") String id);

    @GetMapping("/moods/name/{name}")
        ResponseEntity<ObjectResponseDto> findMoodByName(@PathVariable("name") String name);

    @DeleteMapping("/moods/{id}")
        ResponseEntity<ObjectResponseDto> deleteMood(@PathVariable("id") String id);

    @PostMapping("/categories")
        ResponseEntity<ObjectResponseDto> saveCategory(@RequestBody Category category);

    @GetMapping("/categories")
        ResponseEntity<ObjectListResponseDto> findCategories();

    @GetMapping("/categories/id/{id}")
        ResponseEntity<ObjectResponseDto> findCategoryById(@PathVariable("id") String id);

    @GetMapping("/categories/title/{title}")
        ResponseEntity<ObjectResponseDto> findCategoryByTitle(@PathVariable("title") String title);

    @GetMapping("/categories/colour/{colour}")
        ResponseEntity<ObjectResponseDto> findCategoryByColour(@PathVariable("colour") String colour);

    @DeleteMapping("/categories/{id}")
        ResponseEntity<ObjectResponseDto> deleteCategory(@PathVariable("id") String id);
}
