package com.teamteach.journals.services;

import java.text.SimpleDateFormat;
import java.util.*;

import com.teamteach.journals.models.entities.*;
import com.teamteach.journals.models.requests.*;
import com.teamteach.journals.models.responses.*;
import com.teamteach.journals.services.interfaces.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class JournalServiceImpl implements JournalService {
	@Autowired
	private SequenceGeneratorService sequenceGeneratorService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public ObjectResponseDto save(JournalRequestDto journalRequestDto) {
		Query query = new Query(Criteria.where("name").is(journalRequestDto.getName()));
		Journal journal = mongoTemplate.findOne(query, Journal.class);

		if (journal != null) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("A Journal with this name already exists!")
					.build();
		} else {
			SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
			Date date = new Date(System.currentTimeMillis());
			journal = Journal.builder()
				.id(sequenceGeneratorService.generateSequence(Journal.SEQUENCE_NAME))
				.name(journalRequestDto.getName())
				.createdAt(date)
				.build();
			mongoTemplate.save(journal);
			return ObjectResponseDto.builder()
					.success(true)
					.message("Journal created successfully")
					.object(journal)
					.build();
		}
	}

	@Override
	public ObjectListResponseDto<Journal> findAll() {
		Query query = new Query();
		List<Journal> journals = mongoTemplate.find(query, Journal.class);
		return new ObjectListResponseDto<>(
			true, 
			"Journal records retrieved successfully!", 
			journals);
	}

	@Override
	public ObjectResponseDto delete(String id) {
		Query query = new Query(Criteria.where("id").is(id));
		try {
			mongoTemplate.remove(query, "journals");
			return ObjectResponseDto.builder()
					.success(true)
					.message("Journal deleted successfully")
					.build();
		} catch (RuntimeException e) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("Journal deletiond failed")
					.build();
		}
	}

	@Override
	public ObjectResponseDto findById(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));

		Journal journal = mongoTemplate.findOne(query, Journal.class);

		if (journal == null) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("A Journal with this id does not exist!")
					.build();
		} else {
			return ObjectResponseDto.builder()
					.success(true)
					.message("Journal record retrieved!")
					.object(journal)
					.build();
		}
	}

	@Override
	public ObjectResponseDto findByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		Journal journal = mongoTemplate.findOne(query, Journal.class);

		if (journal == null) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("A Journal with this name does not exist!")
					.build();
		} else {
			return ObjectResponseDto.builder()
					.success(true)
					.message("Journal record retrieved!")
					.object(journal)
					.build();
		}
	}
}
