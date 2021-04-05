package com.teamteach.journals.domains;

import java.util.Date;
import com.teamteach.journals.domains.enums.JournalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Data
public class Journal {
    private String id, journalerName, type, title, detail;
    private JournalStatus status = JournalStatus.CREATED;
    private Date date , lastUpdate = date = new Date();
}
