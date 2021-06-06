package com.teamteach.journalmgmt.domain.usecases;

import java.util.*;

import com.teamteach.journalmgmt.domain.models.JournalEntry;
import com.teamteach.journalmgmt.domain.models.Mood;
import com.teamteach.journalmgmt.domain.responses.MoodObj;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Service
public class MoodsService {

	@Autowired
	private MongoTemplate mongoTemplate;

    public List<MoodObj> getMoodsCount(String journalId) {
		Query query = new Query();

		List<Mood> moods = mongoTemplate.find(query, Mood.class);

		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(Criteria.where("journalId").is(journalId)),
			Aggregation.project().and("mood").as("name"),
			Aggregation.group("name").count().as("count"),
			Aggregation.project("count").and("name").previousOperation()
		);
		AggregationResults<MoodObj> results = mongoTemplate.aggregate(aggregation, JournalEntry.class, MoodObj.class);
		List<MoodObj> moodObjs = results.getMappedResults();
		List<MoodObj> moodResponseList = new ArrayList<>();
		for (Mood mood : moods) {
			MoodObj moodObj = moodObjs.stream().filter(m->mood.getName().equals(m.getName())).findAny().orElse(null);
			if (moodObj != null) {
				moodObj.setUrl(mood.getUrl());
			} else {
				moodObj = new MoodObj(mood.getName(), mood.getUrl(), 0);
			}
			moodResponseList.add(moodObj);
		}
        return moodResponseList;
    }
}
