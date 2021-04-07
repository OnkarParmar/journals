package com.teamteach.journals.exceptions;

import com.teamteach.journals.models.responses.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ObjectResponseDto> resourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        ObjectResponseDto object = ObjectResponseDto.builder()
					.success(false)
					.message(ex.getMessage())
					.build();

        return ResponseEntity.ok(object);
    }
}
