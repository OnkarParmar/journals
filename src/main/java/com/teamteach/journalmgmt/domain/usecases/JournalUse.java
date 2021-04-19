package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JournalUse implements IJournalMgmt{

    final IJournalRepository journalRepository;

	@Autowired
    private SequenceGeneratorService sequenceGeneratorService;

	@Autowired
	private MongoTemplate mongoTemplate;

    @Override
    public ObjectResponseDto saveMaster(JournalCommand journalCommand){
        Journal journal = null;
		if(journalCommand.getOwnerId() != null){
			Query query = new Query(Criteria.where("ownerId").is(journalCommand.getOwnerId()));
			journal = mongoTemplate.findOne(query, Journal.class);
		}
		else {
			journalCommand.setOwnerId("0");
		}

		if (journal != null) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("A Journal with this ownerID already exists!")
					.build();
		} else {
			Date date = new Date(System.currentTimeMillis());
			journal = Journal.builder()
				.journalId(sequenceGeneratorService.generateSequence(Journal.SEQUENCE_NAME))
				.title(journalCommand.getTitle())
				.desc(journalCommand.getDesc())
				.journalType(journalCommand.getJournalType())
				.ownerId(journalCommand.getOwnerId())
				.createdAt(date)
				.updatedAt(date)
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
	public ObjectListResponseDto findById(String ownerId) {
		List<JournalResponse> journalResponses = new ArrayList<>();
		Query query = new Query();
		query.addCriteria(Criteria.where("ownerId").is(ownerId));

		List<Journal> journals = mongoTemplate.find(query, Journal.class);
		for(Journal journal: journals){
			if (journal == null) {
				return ObjectListResponseDto.builder()
						.success(false)
						.message("A Journal with this parentId does not exist!")
						.build();
			} else {
				journalResponses.add(new JournalResponse(journal));
			}
		}
		return new ObjectListResponseDto<>(
			true, 
			"Journal records retrieved successfully!", 
			journalResponses);
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
	public ObjectResponseDto savePrivate(JournalCommand journalCommand) {
		Query query = new Query();
		query.addCriteria(Criteria.where("ownerId").is("0"));
		query.addCriteria(Criteria.where("journalType").is(journalCommand.getJournalType()));
		Journal journal = mongoTemplate.findOne(query, Journal.class);
		if(journal == null)
		{
			return ObjectResponseDto.builder()
					.success(false)
					.message("No master journal found with type " + journalCommand.getJournalType())
					.build();
		}
		journalCommand.setTitle(journal.getTitle());
		journalCommand.setDesc(journal.getDesc());
		return saveMaster(journalCommand);
	}
}
