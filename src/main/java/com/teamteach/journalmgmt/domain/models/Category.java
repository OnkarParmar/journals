package com.teamteach.journalmgmt.domain.models;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    protected String categoryId;
    private String title;
    private String colour;   
}