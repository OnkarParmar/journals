package com.teamteach.journalmgmt.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import com.teamteach.journalmgmt.domain.models.ChildProfile;

@AllArgsConstructor
@Data
@Builder
public class ParentProfileResponseDto {
    private String profileId;
    private String fname;
    private String lname;
    private String email;
    private List<ChildProfile> children;
}