package com.teamteach.journalmgmt.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseModel {
    protected Date createdAt;

    protected Date updatedAt;
}
