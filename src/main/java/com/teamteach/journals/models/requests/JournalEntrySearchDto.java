package com.teamteach.journals.models.requests;

import com.teamteach.journals.models.entities.Moods;
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
    @NotBlank(message = "The text is required")
    private List<String> children;
    private String fromDate;
    private String toDate;
    private List<String> categories;
    private List<Moods> moods;
}
