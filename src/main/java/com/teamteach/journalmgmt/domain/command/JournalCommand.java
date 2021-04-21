package com.teamteach.journalmgmt.domain.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import com.teamteach.journalmgmt.domain.models.UserSignupInfo;

@Data
@NoArgsConstructor
@Getter
public class JournalCommand extends ValidatingCommand{
    @NotNull
    private String title;
    private String desc;
    private String journalType;
    private String ownerId;
    private String[] children;

    public JournalCommand(UserSignupInfo userSignupInfo) {
        this.ownerId = userSignupInfo.getOwnerId();
        this.journalType = userSignupInfo.getProfiletype();
    }
}
