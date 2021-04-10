package com.teamteach.journals.services;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.teamteach.journals.models.entities.*;
import com.teamteach.journals.models.requests.*;
import com.teamteach.journals.models.responses.*;
import com.teamteach.journals.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MoodServiceImpl implements MoodService {
	@Autowired
	private SequenceGeneratorService sequenceGeneratorService;

	@Autowired
	private MongoTemplate mongoTemplate;

    @Override
	public ObjectResponseDto saveMood(Mood mood) {
		Query query = new Query(Criteria.where("name").is(mood.getName()));
		Mood mood2 = mongoTemplate.findOne(query, Mood.class);

		if (mood2 != null) {
			return ObjectResponseDto.builder()
                .success(false)
                .message("A Mood with this title already exists!")
                .build();
		} else {
            mood2 = Mood.builder()
                .moodId(sequenceGeneratorService.generateSequence(Mood.SEQUENCE_NAME))
                .name(mood.getName())
                .build();
            mongoTemplate.save(mood2);
            return ObjectResponseDto.builder()
                .success(true)
                .message("Mood created successfully")
                .object(mood2)
                .build();
        }
    }

    @Override
    public ObjectListResponseDto findMoods() {
        Query query = new Query();
        List<Mood> moods = mongoTemplate.find(query, Mood.class);
        return new ObjectListResponseDto<>(
                true,
                "Moods retrieved successfully!",
				moods);
    }

    @Override
	public ObjectResponseDto deleteMood(String id) {
		Query query = new Query(Criteria.where("id").is(id));
		try {
			mongoTemplate.remove(query, "moods");
			return ObjectResponseDto.builder()
					.success(true)
					.message("Mood deleted successfully")
					.build();
		} catch (RuntimeException e) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("Mood deletion failed")
					.build();
		}
	}

    @Override
	public ObjectResponseDto findMoodById(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));

		Mood mood = mongoTemplate.findOne(query, Mood.class);

		if (mood == null) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("A mood with this id does not exist!")
					.build();
		} else {
			return ObjectResponseDto.builder()
					.success(true)
					.message("Mood retrieved!")
					.object(mood)
					.build();
		}
	}

    @Override
	public ObjectResponseDto findByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		Mood mood = mongoTemplate.findOne(query, Mood.class);

		if (mood == null) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("A mood with this name does not exist!")
					.build();
		} else {
			return ObjectResponseDto.builder()
					.success(true)
					.message("Mood retrieved!")
					.object(mood)
					.build();
		}
	}
}