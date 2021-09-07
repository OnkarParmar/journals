package com.teamteach.journalmgmt.infra.persistence.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import com.teamteach.journalmgmt.domain.models.Journal;
import com.teamteach.journalmgmt.domain.responses.ParentProfileResponseDto;
import com.teamteach.commons.utils.AnonymizeService;
import com.teamteach.journalmgmt.domain.usecases.ProfileService;

import java.util.*;

@Component
public class DataSeeder implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    }
}
