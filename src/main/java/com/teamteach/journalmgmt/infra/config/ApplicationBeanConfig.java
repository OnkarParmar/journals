package com.teamteach.journalmgmt.infra.config;

import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.infra.persistence.dal.JournalDAL;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.teamteach.commons.connectors.rabbit.core.IMessagingPort;

@Configuration
@RequiredArgsConstructor
public class ApplicationBeanConfig {

    final IJournalRepository journalRepository;
    final IJournalEntryRepository journalEntryRepository;
    final MongoTemplate mongoTemplate;
    final IMessagingPort messagingPort;

    @Bean("journalMgmtSvc")
    IJournalMgmt journalMgmt() {
        return new JournalUse(journalRepository, messagingPort);
    }
    @Bean("journalEntryMgmtSvc")
    IJournalEntryMgmt journalEntryMgmt() {
        return new JournalEntryUse(journalEntryRepository);
    }
    @Bean("journalDALayer")
    IJournalRepository journalDAL() {
        return new JournalDAL(mongoTemplate);
    }
    @Bean("journalEntryDALayer")
    IJournalEntryRepository journalEntryDAL() {
        return new JournalDAL(mongoTemplate);
    }
    @Bean("moodMgmtSvc")
    IMoodMgmt moodMgmt() {
        return new MoodUse(journalRepository);
    }
    @Bean("categoryMgmtSvc")
    ICategoryMgmt categoryMgmt() {
        return new CategoryUse(journalRepository);
    }
}
