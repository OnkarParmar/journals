package com.teamteach.journals.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class JournalEntryRequestDto {
    @NotBlank(message = "The text is required")
    private String[] children;
    private String text;
    private String category;
    private String mood;
}
