package com.teamteach.journals.models.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(chain = true)
public class ObjectListResponseDto<T> {
    private final boolean success;
    private final String message;
    private List<T> objects;

    @Builder
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ObjectListResponseDto(boolean success, String message, @JsonProperty("journals") List<T> objects) {
        this.success = success;
        this.message = message;
        this.objects = objects;
    }
}


