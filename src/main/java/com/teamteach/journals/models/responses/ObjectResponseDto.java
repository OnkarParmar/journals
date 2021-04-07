package com.teamteach.journals.models.responses;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ObjectResponseDto {
    private final boolean success;
    private final String message;
    private Object object;

    @Builder
    public ObjectResponseDto(boolean success, String message, Object object) {
        this.success = success;
        this.message = message;
        this.object = object;
    }
}
