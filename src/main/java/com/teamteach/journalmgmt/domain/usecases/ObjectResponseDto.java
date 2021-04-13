package com.teamteach.journalmgmt.domain.usecases;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ObjectResponseDto {
    private final boolean success;
    private final String message;
    private Object object;

    public ObjectResponseDto(){
        this.success = false;
        this.message = "";
    }

    @Builder
    public ObjectResponseDto(boolean success, String message, Object object) {
        this.success = success;
        this.message = message;
        this.object = object;
    }
}