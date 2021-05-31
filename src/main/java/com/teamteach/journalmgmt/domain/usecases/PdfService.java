package com.teamteach.journalmgmt.domain.usecases;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.Data;

import com.teamteach.journalmgmt.domain.models.JournalEntryProfile;
import com.teamteach.journalmgmt.infra.external.JournalEntryReportService;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.*;  

@Service
@Data
public class PdfService {

    private SpringTemplateEngine templateEngine;

    private JournalEntryProfile report;

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
        //System.out.println(html);
        return renderPdf(html);
    }


    private File renderPdf(String html) throws IOException, DocumentException {
        File file = new File("report.html");

        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.deleteOnExit();
        return file;
    }

    private Context getContext() {
        Context context = new Context();
        Map<String, Object> model = new HashMap<>();

        String fromDateStr = report.getFromDate();
        String toDateStr = report.getToDate();
        SimpleDateFormat formatter = null;
        Date fromDate = null;
        Date toDate = null;
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            fromDate = formatter.parse(fromDateStr);
            toDate = formatter.parse(toDateStr);
        } catch(ParseException e){
            e.printStackTrace();
        }

        List<String> filterChildren = report.getFilterChildren();

        model.put("fname", report.getFname());
        model.put("lname", report.getLname());
        model.put("fromDate", fromDate);
        model.put("toDate", toDate);
        model.put("filterChildren", filterChildren);
        model.put("children", report.getChildren());
        model.put("entries", report.getEntryList());

        context.setVariables(model);
        return context;
    }

    private String loadAndFillTemplate(Context context) {
        return templateEngine.process("report", context);
    }
}