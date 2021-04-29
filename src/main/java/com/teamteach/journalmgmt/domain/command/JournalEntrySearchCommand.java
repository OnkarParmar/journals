package com.teamteach.journalmgmt.domain.command;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Getter
public class JournalEntrySearchCommand extends ValidatingCommand{
    @NotNull
    private List<String> children;
    private String fromDate;
    private String toDate;
    private String ownerId;
    private String entryId;
    private List<String> categories;
    private List<String> moods;
    private String viewMonth;
}