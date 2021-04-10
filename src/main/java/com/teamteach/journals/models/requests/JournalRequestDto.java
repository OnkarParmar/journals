package com.teamteach.journals.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Builder
@Data
@Accessors(chain = true)
public class JournalRequestDto {
    @NotBlank(message = "The title is required")
    private String title;
    private String desc;
    private String[] children;
}
