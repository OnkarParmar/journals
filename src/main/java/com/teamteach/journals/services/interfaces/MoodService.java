package com.teamteach.journals.services.interfaces;

import com.teamteach.journals.models.entities.*;
import com.teamteach.journals.models.requests.*;
import com.teamteach.journals.models.responses.*;

public interface MoodService {
    ObjectResponseDto saveMood(Mood mood);
    ObjectListResponseDto findMoods();
    ObjectResponseDto deleteMood(String id);
    ObjectResponseDto findByName(String name);
    ObjectResponseDto findMoodById(String id);
}