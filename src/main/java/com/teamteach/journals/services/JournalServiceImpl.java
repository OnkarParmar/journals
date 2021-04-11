package com.teamteach.journals.services;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
	public ObjectResponseDto saveMaster(JournalRequestDto journalRequestDto) {
		Journal journal = null;
		if(journalRequestDto.getOwnerId() != null){
			Query query = new Query(Criteria.where("ownerId").is(journalRequestDto.getOwnerId()));
			journal = mongoTemplate.findOne(query, Journal.class);
		}
		else {
			journalRequestDto.setOwnerId("0");
		}

		if (journal != null) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("A Journal with this ownerID already exists!")
					.build();
		} else {
			SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm:ss");
			Date date = new Date(System.currentTimeMillis());			
			journal = Journal.builder()
				.journalId(sequenceGeneratorService.generateSequence(Journal.SEQUENCE_NAME))
				.title(journalRequestDto.getTitle())
				.desc(journalRequestDto.getDesc())
				.journalType(journalRequestDto.getJournalType())
				.ownerId(journalRequestDto.getOwnerId())
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
	public ObjectListResponseDto<JournalResponse> findAll() {
		List<JournalResponse> journalResponses = new ArrayList<>();
		Query query = new Query();
		List<Journal> journals = mongoTemplate.find(query, Journal.class);
		for(Journal journal: journals){
			journalResponses.add(new JournalResponse(journal));
		}
		return new ObjectListResponseDto<>(
			true, 
			"Journal records retrieved successfully!", 
			journalResponses);
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
					.message("Journal deletion failed")
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
	public ObjectResponseDto findByTitle(String title) {
		Query query = new Query(Criteria.where("title").is(title));
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

	@Override
    public ObjectResponseDto savePrivate(JournalRequestDto journalRequestDto) {
        Query query = new Query();
		query.addCriteria(Criteria.where("ownerId").is("0"));		
		query.addCriteria(Criteria.where("journalType").is(journalRequestDto.getJournalType()));
		Journal journal = mongoTemplate.findOne(query, Journal.class);
		if(journal == null)
		{
			return ObjectResponseDto.builder()
									.success(false)
									.message("No master journal found with type " + journalRequestDto.getJournalType())
									.build();
		}
		journalRequestDto.setTitle(journal.getTitle());
		journalRequestDto.setDesc(journal.getDesc());
		return saveMaster(journalRequestDto);
	}
}
