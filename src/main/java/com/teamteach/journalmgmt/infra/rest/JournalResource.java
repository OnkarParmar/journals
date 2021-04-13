package com.teamteach.journalmgmt.infra.rest;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.infra.api.IJournalResource;
import com.teamteach.journalmgmt.shared.AbstractAppController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
class JournalResource extends AbstractAppController implements IJournalResource {

    final IJournalMgmt journalMgmt;
    final IMoodMgmt moodMgmt;
    final ICategoryMgmt categoryMgmt;

    @Override
    @ApiOperation(value = "Creates the journal", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> saveJournal(@Valid JournalCommand journalCommand){
        if(journalCommand.getOwnerId() == null) //creating a master copy 
                return ResponseEntity.ok(journalMgmt.saveMaster(journalCommand));
        else // retrieve the existing journal of the owner with the ownerId
        {
            return ResponseEntity.ok(journalMgmt.savePrivate(journalCommand));
        }
    }

    @Override
    @ApiOperation(value = "Finds journals", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectListResponseDto<JournalResponse>> findAllJournals(){
        return ResponseEntity.ok(journalMgmt.findAll());
    }

    @Override
    @ApiOperation(value = "Deletes journals", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> deleteJournal(String id){
        return ResponseEntity.ok(journalMgmt.delete(id));
    }

    @Override
    @ApiOperation(value = "Finds journal by id", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> findJournalById(String id){
        return ResponseEntity.ok(journalMgmt.findById(id));
    }

    @Override
    @ApiOperation(value = "Finds journal by title", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> findJournalByTitle(String title){
        return ResponseEntity.ok(journalMgmt.findByTitle(title));
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

    @Override
    @ApiOperation(value = "Creates category", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> saveCategory(Category category) {
        return ResponseEntity.ok(categoryMgmt.saveCategory(category));
    }

    @Override
    @ApiOperation(value = "Finds categories", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectListResponseDto> findCategories() {
        return ResponseEntity.ok(categoryMgmt.findCategories());
    }

    @Override
    @ApiOperation(value = "Finds category by id", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> findCategoryById(String id) {
        return ResponseEntity.ok(categoryMgmt.findCategoryById(id));
    }

    @Override
    @ApiOperation(value = "Finds category by title", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> findCategoryByTitle(String title) {
        return ResponseEntity.ok(categoryMgmt.findCategoryByTitle(title));
    }

    @Override
    @ApiOperation(value = "Finds category by colour", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> findCategoryByColour(String colour) {
        return ResponseEntity.ok(categoryMgmt.findCategoryByColour(colour));
    }

    @Override
    @ApiOperation(value = "Deletes category", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ObjectResponseDto> deleteCategory(String id) {
        return ResponseEntity.ok(categoryMgmt.deleteCategory(id));
    }

}
