package com.teamteach.journals.services.interfaces;

import com.teamteach.journals.models.entities.*;
import com.teamteach.journals.models.requests.*;
import com.teamteach.journals.models.responses.*;

public interface CategoryService {
    ObjectResponseDto saveCategory(Category category);
    ObjectListResponseDto findCategories();
    ObjectResponseDto deleteCategory(String id);
    ObjectResponseDto findCategoryByTitle(String title);
    ObjectResponseDto findCategoryById(String id);
    ObjectResponseDto findCategoryByColour(String colour);
}

