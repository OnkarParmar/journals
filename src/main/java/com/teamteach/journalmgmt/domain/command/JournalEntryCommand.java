package com.teamteach.journalmgmt.domain.command;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class JournalEntryCommand extends ValidatingCommand {

    private String[] children;
    private String journalId;
    private String text;
    private String categoryId;
    private String mood;
    private MultipartFile entryImage;

    protected void validateSelf() {
        super.validateSelf();
    }
}
