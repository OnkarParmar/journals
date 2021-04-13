package com.teamteach.journalmgmt.domain.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Getter
public class JournalCommand extends ValidatingCommand{
    @NotNull
    private String title;
    private String desc;
    private String[] children;
    private String journalType;
    private String ownerId;
}
