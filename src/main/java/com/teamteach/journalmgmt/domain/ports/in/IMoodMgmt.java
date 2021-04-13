package com.teamteach.journalmgmt.domain.ports.in;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.domain.models.*;

public interface IMoodMgmt{
    ObjectResponseDto saveMood(Mood mood);
    ObjectListResponseDto findMoods();
    ObjectResponseDto deleteMood(String id);
    ObjectResponseDto findByName(String name);
    ObjectResponseDto findMoodById(String id);
}