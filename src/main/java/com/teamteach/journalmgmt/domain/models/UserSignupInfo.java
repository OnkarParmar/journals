package com.teamteach.journalmgmt.domain.models;

import lombok.Data;

@Data
public class UserSignupInfo {
    String fname;
    String lname;
    String ownerId;
    String profiletype;
    String relation;
}
