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
import com.teamteach.journals.services.interfaces.*;

@RestController
@RequestMapping("")
public class JournalController {

    @Autowired
        private JournalService journalService;

    @Autowired
        private MoodService moodService;
    
    @Autowired
        private CategoryService categoryService;

    @PostMapping("")
        public ResponseEntity<ObjectResponseDto> saveJournal(@RequestBody JournalRequestDto journalRequestDto) {
            if(journalRequestDto.getOwnerId() == null) //creating a master copy 
                return ResponseEntity.ok(journalService.saveMaster(journalRequestDto));
            else // retrieve the existing journal of the owner with the ownerId
            {
                return ResponseEntity.ok(journalService.savePrivate(journalRequestDto));
            }
        }
    
    @PostMapping("/moods")
    public ResponseEntity<ObjectResponseDto> saveMood(@RequestBody Mood mood) {
        return ResponseEntity.ok(moodService.saveMood(mood));
    }

    @PostMapping("/categories")
    public ResponseEntity<ObjectResponseDto> saveCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.saveCategory(category));
    }

    @GetMapping("")
        public ResponseEntity<ObjectListResponseDto<JournalResponse>> findAllJournals() {
            return ResponseEntity.ok(journalService.findAll());
        }
        
    @GetMapping("/moods")
    public ResponseEntity<ObjectListResponseDto> findAllMoods() {
        return ResponseEntity.ok(moodService.findMoods());
    }    

    @GetMapping("/categories")
    public ResponseEntity<ObjectListResponseDto> findCategories() {
        return ResponseEntity.ok(categoryService.findCategories());
    }

    @GetMapping("/id/{id}")
        public ResponseEntity<ObjectResponseDto> findJournalById(@PathVariable("id") String id) {
            return ResponseEntity.ok(journalService.findById(id));
        }

    @GetMapping("/moods/id/{id}")
    public ResponseEntity<ObjectResponseDto> findMoodById(@PathVariable("id") String id) {
        return ResponseEntity.ok(moodService.findMoodById(id));
    }    

    @GetMapping("/categories/id/{id}")
    public ResponseEntity<ObjectResponseDto> findCategoryById(@PathVariable("id") String id) {
        return ResponseEntity.ok(categoryService.findCategoryById(id));
    }
        
    @GetMapping("/title/{title}")
    public ResponseEntity<ObjectResponseDto> findJournalByTitle(@PathVariable("title") String title) {
        return ResponseEntity.ok(journalService.findByTitle(title));
    }

    @GetMapping("/moods/name/{name}")
    public ResponseEntity<ObjectResponseDto> findMoodByName(@PathVariable("name") String name) {
        return ResponseEntity.ok(moodService.findByName(name));
    }

    @GetMapping("/categories/title/{title}")
    public ResponseEntity<ObjectResponseDto> findCategoryByTitle(@PathVariable("title") String title) {
        return ResponseEntity.ok(categoryService.findCategoryByTitle(title));
    }

    @GetMapping("/categories/colour/{colour}")
    public ResponseEntity<ObjectResponseDto> findCategoryByColour(@PathVariable("colour") String colour) {
        return ResponseEntity.ok(categoryService.findCategoryByColour(colour));
    }

    @DeleteMapping("/{id}")
        public ResponseEntity<ObjectResponseDto> deleteJournal(@PathVariable("id") String id) {
            return ResponseEntity.ok(journalService.delete(id));
        }
        
    @DeleteMapping("/moods/{id}")
    public ResponseEntity<ObjectResponseDto> deleteMood(@PathVariable("id") String id) {
        return ResponseEntity.ok(moodService.deleteMood(id));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ObjectResponseDto> deleteCategory(@PathVariable("id") String id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }
}
