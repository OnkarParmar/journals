package com.teamteach.journalmgmt.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class ParentProfileResponseDto {
    private String fname;
    private String lname;
    private String email;
    private List<String> children;
}