package com.teamteach.journals.models.requests;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class EntryRequestDto {
    @NotBlank(message = "The text is required")
    private String[] children;
    private String text;
    private String category;
    private String mood;
    //from&to dates,journalID,entryID,moods&categories arrays ?

    @Builder
    public EntryRequestDto(
            String text,
            String[] children,
            String category,
            String mood ) {

        this.text = text;
        this.children = children;
        this.category = category;
        this.mood = mood;
    }
}
