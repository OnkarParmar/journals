package com.teamteach.journals.models.requests;

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
    private String text;
    private List<String> categories;
    private List<String> moods;
}
