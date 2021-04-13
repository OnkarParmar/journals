package com.teamteach.journalmgmt.domain.command;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Getter
public class JournalEntryCommand extends ValidatingCommand {

    private String[] children;
    private String ownerId;
    private String text;
    private String category;
    private String mood;

    protected void validateSelf() {
        super.validateSelf();
    }
}
