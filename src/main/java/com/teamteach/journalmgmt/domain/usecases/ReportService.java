package com.teamteach.journalmgmt.domain.usecases;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.Data;

import com.teamteach.journalmgmt.domain.models.JournalEntryProfile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.*;  

@Service
@Data
public class ReportService {

    private SpringTemplateEngine templateEngine;

    private JournalEntryProfile report;

    @Autowired
    public ReportService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public File generateReport() throws IOException, DocumentException {
        Context context = getContext();
        String html = loadAndFillTemplate(context);
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

        List<String> filterChildren = report.getFilterChildren();

        model.put("fname", report.getFname());
        model.put("lname", report.getLname());
        model.put("fromDate", fromDateStr);
        model.put("toDate", toDateStr);
        model.put("filterChildren", filterChildren);
        model.put("children", report.getChildren());
        model.put("entries", report.getEntryList());
        model.put("relation",(report.getRelation() == null || report.getRelation().equals(""))  ? "Parent" : report.getRelation());

        context.setVariables(model);
        return context;
    }

    private String loadAndFillTemplate(Context context) {
        return templateEngine.process("index", context);
    }
}