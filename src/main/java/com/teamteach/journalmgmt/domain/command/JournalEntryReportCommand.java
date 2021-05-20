package com.teamteach.journalmgmt.domain.command;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class JournalEntryReportCommand extends ValidatingCommand{
    private String email;
    private String url; 
}