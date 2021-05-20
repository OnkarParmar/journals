package com.teamteach.journalmgmt.domain.models;

import lombok.Data;
import lombok.Builder;
import java.util.*;
import com.teamteach.journalmgmt.domain.responses.*;

@Data
@Builder
public class JournalEntryProfile {
    String action;
    String fname;
    String lname;
    String email;
    String url;
    List<JournalEntryResponse> entryList;
}