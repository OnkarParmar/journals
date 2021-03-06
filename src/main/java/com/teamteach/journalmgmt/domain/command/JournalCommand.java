package com.teamteach.journalmgmt.domain.command;

import lombok.Data;
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
    private String name;
    private String journalYear;
    private String info;

    public JournalCommand(UserSignupInfo userSignupInfo) {
        this.ownerId = userSignupInfo.getOwnerId();
        this.journalType = userSignupInfo.getProfiletype();
        this.name = userSignupInfo.getFname() + " " + userSignupInfo.getLname();
    }
}