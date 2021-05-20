package com.teamteach.journalmgmt.infra.external;

import com.teamteach.commons.connectors.rabbit.core.IMessagingPort;
import com.teamteach.journalmgmt.domain.models.JournalEntryProfile;
import com.teamteach.journalmgmt.domain.ports.out.IJournalEntryReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Data;

import java.util.Set;

@Component
@Data
@RequiredArgsConstructor
public class JournalEntryReportService implements IJournalEntryReportService {

    final IMessagingPort messagingPort;
    private JournalEntryProfile report;

    @Value("${exchange.signup}")
    String signupExchange;

    @Override
    public void sendJournalEntryReportEvent(JournalEntryProfile journalEntryProfile, String q) {
        messagingPort.sendMessage(signupExchange, q , journalEntryProfile);
    }

    // @Override
    // public JournalEntryProfile getReport(){
    //     return journalEntryProfile;
    // }
}