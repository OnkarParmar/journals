package com.teamteach.journalmgmt.infra.api;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.responses.JournalResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;
import com.teamteach.journalmgmt.domain.usecases.*;
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

    @ApiIgnore
    @PostMapping("/moods")
        ResponseEntity<ObjectResponseDto> saveMood(@RequestBody Mood mood);

    @ApiIgnore
    @GetMapping("/moods")
        ResponseEntity<ObjectListResponseDto> findAllMoods();

    @ApiIgnore
    @GetMapping("/moods/id/{id}")
        ResponseEntity<ObjectResponseDto> findMoodById(@PathVariable("id") String id);

    @ApiIgnore
    @GetMapping("/moods/name/{name}")
        ResponseEntity<ObjectResponseDto> findMoodByName(@PathVariable("name") String name);

    @ApiIgnore
    @DeleteMapping("/moods/{id}")
        ResponseEntity<ObjectResponseDto> deleteMood(@PathVariable("id") String id);

    @ApiIgnore
    @PostMapping("/categories")
        ResponseEntity<ObjectResponseDto> saveCategory(@RequestBody Category category);

    @ApiIgnore
    @GetMapping("/categories")
        ResponseEntity<ObjectListResponseDto> findCategories();

    @ApiIgnore
    @GetMapping("/categories/id/{id}")
        ResponseEntity<ObjectResponseDto> findCategoryById(@PathVariable("id") String id);

    @ApiIgnore
    @GetMapping("/categories/title/{title}")
        ResponseEntity<ObjectResponseDto> findCategoryByTitle(@PathVariable("title") String title);

    @ApiIgnore
    @GetMapping("/categories/colour/{colour}")
        ResponseEntity<ObjectResponseDto> findCategoryByColour(@PathVariable("colour") String colour);

    @ApiIgnore
    @DeleteMapping("/categories/{id}")
        ResponseEntity<ObjectResponseDto> deleteCategory(@PathVariable("id") String id);
}
