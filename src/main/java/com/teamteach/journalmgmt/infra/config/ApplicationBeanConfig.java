package com.teamteach.journalmgmt.infra.config;

import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.infra.persistence.dal.JournalDAL;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@RequiredArgsConstructor
public class ApplicationBeanConfig {

    final IJournalRepository journalRepository;
    final MongoTemplate mongoTemplate;
    //final IJournalEntryMgmt journalEntryMgmt;

    @Bean("journalMgmtSvc")
    IJournalMgmt journalMgmt() {
        return new JournalUse(journalRepository);
    }
    @Bean("journalDALayer")
    IJournalRepository journalDAL() {
        return new JournalDAL(mongoTemplate);
    }
    // @Bean("journalEntryMgmt")
    // IJournalEntryMgmt journalEntry() {
    //     return new JournalEntryUse(journalEntryMgmt);
    // }
}
