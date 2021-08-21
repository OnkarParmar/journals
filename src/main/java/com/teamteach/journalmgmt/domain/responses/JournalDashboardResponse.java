package com.teamteach.journalmgmt.domain.responses;

import lombok.Builder;
import lombok.Data;

@Data
public class JournalDashboardResponse {
    private int id;
    private String ownerId;
    private String name;
    private String lastLogin;
    private int entryCount;
    private String lastEntry;

    @Builder
    public JournalDashboardResponse(int id,
                                    String ownerId,
                                    String name,
                                    String lastLogin,
                                    int entryCount,
                                    String lastEntry) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.lastLogin = lastLogin;
        this.entryCount = entryCount;
        this.lastEntry = lastEntry;
    }
}