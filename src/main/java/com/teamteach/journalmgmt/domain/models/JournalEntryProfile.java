package com.teamteach.journalmgmt.domain.models;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class JournalEntryProfile {
    String action;
    String fname;
    String lname;
    String email;
}