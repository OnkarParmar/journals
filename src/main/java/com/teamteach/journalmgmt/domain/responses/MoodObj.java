package com.teamteach.journalmgmt.domain.responses;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class MoodObj {
    private String name;
    private String url;
    private int count;
}
