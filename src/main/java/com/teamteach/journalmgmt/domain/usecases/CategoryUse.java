package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.responses.ObjectListResponseDto;
import com.teamteach.journalmgmt.domain.responses.ObjectResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CategoryUse implements ICategoryMgmt{

    final IJournalRepository journalRepository;

	@Autowired
    private SequenceGeneratorService sequenceGeneratorService;

	@Autowired
	private MongoTemplate mongoTemplate;

    @Override
	public ObjectResponseDto saveCategory(Category category) {
		Query query = new Query(Criteria.where("title").is(category.getTitle()));
		Category category2 = mongoTemplate.findOne(query, Category.class);

		if (category2 != null) {
			return ObjectResponseDto.builder()
                .success(false)
                .message("A Category with this title already exists!")
                .build();
		} else {
            category2 = Category.builder()
                .categoryId(category.getCategoryId())
                .title(category.getTitle())
                .colour(category.getColour())
                .build();
            mongoTemplate.save(category2);
            return ObjectResponseDto.builder()
                .success(true)
                .message("Category created successfully")
                .object(category2)
                .build();
        }
    }

    @Override
    public ObjectListResponseDto findCategories() {
        Query query = new Query();
        List<Category> categories = mongoTemplate.find(query, Category.class);
        return new ObjectListResponseDto<>(
                true,
                "Categories retrieved successfully!",
				categories);
    }

    @Override
	public ObjectResponseDto deleteCategory(String id) {
		Query query = new Query(Criteria.where("id").is(id));
		try {
			mongoTemplate.remove(query, "categories");
			return ObjectResponseDto.builder()
					.success(true)
					.message("Category deleted successfully")
					.build();
		} catch (RuntimeException e) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("Category deletion failed")
					.build();
		}
	}

    @Override
	public ObjectResponseDto findCategoryById(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));

		Category category = mongoTemplate.findOne(query, Category.class);

		if (category == null) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("A category with this id does not exist!")
					.build();
		} else {
			return ObjectResponseDto.builder()
					.success(true)
					.message("Category retrieved!")
					.object(category)
					.build();
		}
	}

    @Override
	public ObjectResponseDto findCategoryByTitle(String title) {
		Query query = new Query();
		query.addCriteria(Criteria.where("title").is(title));

		Category category = mongoTemplate.findOne(query, Category.class);

		if (category == null) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("A category with this title does not exist!")
					.build();
		} else {
			return ObjectResponseDto.builder()
					.success(true)
					.message("Category retrieved!")
					.object(category)
					.build();
		}
	}

    @Override
	public ObjectResponseDto findCategoryByColour(String colour) {
		Query query = new Query();
		query.addCriteria(Criteria.where("colour").is(colour));

		Category category = mongoTemplate.findOne(query, Category.class);

		if (category == null) {
			return ObjectResponseDto.builder()
					.success(false)
					.message("A category with this colour does not exist!")
					.build();
		} else {
			return ObjectResponseDto.builder()
					.success(true)
					.message("Category retrieved!")
					.object(category)
					.build();
		}
	}
}