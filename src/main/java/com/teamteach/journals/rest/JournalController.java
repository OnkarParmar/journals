package com.teamteach.journals.rest; 


import java.security.Principal;
import java.util.HashMap;

import javax.inject.Inject;

import com.teamteach.journals.constants.Roles;
import com.teamteach.journals.domains.Journal;
import com.teamteach.journals.domains.enums.JournalStatus;
import com.teamteach.journals.services.JournalService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Controller("/")
public class JournalController {


    @Inject
    private JournalService journalService;

    @Secured({Roles.USER})
    @Post("/create")
    public Single<Journal> saveJournal(@Body Journal journal, Principal principal )
    {
        journal.setJournalerName(principal.getName());
        return journalService.save(journal);
    }

    @Secured({Roles.ADMIN, Roles.USER})
    @Get("/journals/{journalId}")
    public Single<Journal> findJournalByNo(@PathVariable(value ="journalId" ) String journalId){

        return journalService.findJournalByNo(journalId); 
    }

    @Secured({Roles.ADMIN, Roles.USER})
    @Get("/journals/getAll")
    public Flowable<Journal> findAll(){

        return journalService.findAll(); 
    }

    @Secured({Roles.ADMIN, Roles.USER})
    @Get("/journals/getJournalIn{name}")
    public Flowable<Journal> findByName(@PathVariable("name") String name){
        return journalService.findByName(name); 
    }

    @Secured({Roles.USER})
    @Get("/journals/")
    public Flowable<Journal> findAll(Principal principal){
        return journalService.findAll(principal.getName()); 

    }
}
