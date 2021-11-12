package com.teamteach.journalmgmt.domain.command;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditJournalCommand {

    private String title;
    private String name;
    private String desc;
    private String journalType;
    private String journalYear;
    private Boolean active;

}