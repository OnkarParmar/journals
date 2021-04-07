package com.teamteach.journals.models.requests;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class JournalRequestDto {
    @NotBlank(message = "The name is required")
    private String name;

    @Builder
    public JournalRequestDto(String name) {
        this.name = name;
    }
}
