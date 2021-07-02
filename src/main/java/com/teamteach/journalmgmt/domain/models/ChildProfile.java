package com.teamteach.journalmgmt.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Data
@AllArgsConstructor
@Builder
public class ChildProfile {
    private String name;
    private String profileId;
    private String info;
    private String birthYear;
    private String profileImage;
}
