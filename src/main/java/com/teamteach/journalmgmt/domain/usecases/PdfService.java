package com.teamteach.journalmgmt.domain.usecases;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;
import com.teamteach.journalmgmt.domain.responses.*;

import com.teamteach.journalmgmt.domain.models.JournalEntryProfile;
import com.teamteach.journalmgmt.infra.external.JournalEntryReportService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Service
public class PdfService {

    private static final String PDF_RESOURCES = "/static/";
    private SpringTemplateEngine templateEngine;

    @Autowired
    private JournalEntryReportService journalEntriesReportService;

    @Autowired
    public PdfService(JournalEntryReportService journalEntriesReportService, SpringTemplateEngine templateEngine) {
        this.journalEntriesReportService = journalEntriesReportService;
        this.templateEngine = templateEngine;
    }

    public File generatePdf() throws IOException, DocumentException {
        Context context = getContext();
        String html = loadAndFillTemplate(context);
        System.out.println(html);
        return renderPdf(html);
    }


    private File renderPdf(String html) throws IOException, DocumentException {
        File file = File.createTempFile("report", ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 20);
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        file.deleteOnExit();
        return file;
    }

    private Context getContext() {
        Context context = new Context();
        Map<String, Object> model = new HashMap<>();
        JournalEntryProfile report = journalEntriesReportService.getReport();
        //List<JournalEntryResponse> entries = (List<JournalEntryResponse>)(report.getJournalEntryMatrix().get(1));

        model.put("fname", report.getFname());
        model.put("lname", report.getLname());
        model.put("entries", report.getJournalEntryMatrix());

        context.setVariables(model);
        return context;
    }

    private String loadAndFillTemplate(Context context) {
        return templateEngine.process("entries-report", context);
    }
}