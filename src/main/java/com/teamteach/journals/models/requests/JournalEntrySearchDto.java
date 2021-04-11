package com.teamteach.journals.models.requests;

import com.teamteach.journals.models.entities.Category;
import com.teamteach.journals.models.entities.Mood;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class JournalEntrySearchDto {
    @NotBlank(message = "Some filter is required")
    private List<String> children;
    private String fromDate;
    private String toDate;
    private String ownerId;
    private String entryId;
    private List<Category> categories;
    private List<Mood> moods;
}
