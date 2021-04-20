package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.responses.JournalResponse;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import com.teamteach.commons.connectors.rabbit.core.IMessagingPort;

import java.util.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JournalUse implements IJournalMgmt{

    final IJournalRepository journalRepository;
    final IMessagingPort messagingPort;

	@Autowired
    private SequenceGeneratorService sequenceGeneratorService;

	@Autowired
	private MongoTemplate mongoTemplate;

    @PostConstruct
    void registerWithMQ() {
        messagingPort.registerGeneralResponseListener("event.createjournal", UserSignupInfo.class, queueConsumer);
    }

    Consumer<UserSignupInfo> queueConsumer = new Consumer<UserSignupInfo>() {
        @Override
        public void accept(UserSignupInfo userSignupInfo) {
            savePrivate(new JournalCommand(userSignupInfo));
        }
    };

    @Override
    public ObjectResponseDto createJournal(JournalCommand journalCommand){
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
					.object(journal)
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
	public ObjectListResponseDto<JournalResponse> findById(String ownerId) {
		List<JournalResponse> journalResponses = new ArrayList<>();
		Query query = new Query(Criteria.where("ownerId").is(ownerId));

		List<Journal> journals = mongoTemplate.find(query, Journal.class);
		if (journals == null) {
			return new ObjectListResponseDto<>(false, "A Journal with this parentId does not exist!", null);
		} else {
			for (Journal journal : journals) {
				JournalResponse journalResponse = new JournalResponse(journal);
				Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(Criteria.where("journalId").is(journal.getJournalId())),
					Aggregation.project()
								.and("mood").as("name"),
					Aggregation.group("name")
									.count().as("count"),
					Aggregation.project("count").and("name").previousOperation()
				);
				AggregationResults<ObjectCount> results = mongoTemplate.aggregate(aggregation, JournalEntry.class, ObjectCount.class);
				journalResponse.setMoods(results.getMappedResults());
				journalResponse.setEntryCount();
				journalResponses.add(journalResponse);
			}
		}
		return new ObjectListResponseDto<>(true, "Journal records retrieved successfully!", journalResponses);
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
		return createJournal(journalCommand);
	}
}
