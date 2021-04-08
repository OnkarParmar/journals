package com.teamteach.journals.models.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category {
    private String title;
    private String colour;
    private String id;
}
