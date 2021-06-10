package com.teamteach.journalmgmt.infra.external;

import com.teamteach.commons.connectors.rabbit.core.IMessagingPort;
import com.teamteach.journalmgmt.domain.models.SendReportInfo;
import com.teamteach.journalmgmt.domain.ports.out.IJournalEntryReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@Data
@RequiredArgsConstructor
public class JournalEntryReportService implements IJournalEntryReportService {

    final IMessagingPort messagingPort;

    @Value("${exchange.signup}")
    String signupExchange;

    @Override
    public void sendJournalEntryReportEvent(SendReportInfo sendReportInfo, String q) {
        messagingPort.sendMessage(signupExchange, q , sendReportInfo);
    }
}