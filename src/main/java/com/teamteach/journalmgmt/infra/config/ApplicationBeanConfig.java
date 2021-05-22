package com.teamteach.journalmgmt.infra.config;

import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.infra.persistence.dal.JournalDAL;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.RestTemplate;

import com.teamteach.commons.connectors.rabbit.core.IMessagingPort;

@Configuration
@RequiredArgsConstructor
public class ApplicationBeanConfig {

    final IJournalRepository journalRepository;
    final IJournalEntryRepository journalEntryRepository;
    final MongoTemplate mongoTemplate;
    final IMessagingPort messagingPort;
    final RestTemplate restTemplate;

    @Bean("journalMgmtSvc")
    IJournalMgmt journalMgmt() {
        return new JournalUse(journalRepository, messagingPort,restTemplate );
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
}
