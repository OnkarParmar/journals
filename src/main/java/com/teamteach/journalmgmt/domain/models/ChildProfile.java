package com.teamteach.journalmgmt.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChildProfile implements Cloneable {
    private String name;
    private String profileId;
    private String info;
    private String birthYear;
    private String profileImage;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ChildProfile clone = null;
        try
        {
            clone = (ChildProfile) super.clone();
        } 
        catch (CloneNotSupportedException e) 
        {
            throw new RuntimeException(e);
        }
        return clone;
    }
}
