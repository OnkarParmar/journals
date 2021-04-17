package com.teamteach.journalmgmt.domain.ports.in;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.domain.models.*;

public interface ICategoryMgmt{
    ObjectResponseDto saveCategory(Category category);
    ObjectListResponseDto findCategories();
    ObjectResponseDto deleteCategory(String id);
    ObjectResponseDto findCategoryByTitle(String title);
    ObjectResponseDto findCategoryById(String id);
    ObjectResponseDto findCategoryByColour(String colour);
}