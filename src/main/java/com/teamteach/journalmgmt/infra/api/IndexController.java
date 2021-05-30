package com.teamteach.journalmgmt.infra.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.responses.*;
import com.teamteach.journalmgmt.domain.usecases.*;
import com.teamteach.journalmgmt.infra.external.JournalEntryReportService;
import org.springframework.http.HttpHeaders;
import com.lowagie.text.DocumentException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

@Controller
public class IndexController {

    @Autowired
    private HtmlService htmlService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("/report/{journalId}")
    public ModelAndView report(@PathVariable String journalId,@RequestHeader HttpHeaders headers){
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        Query query = new Query(Criteria.where("journalId").is(journalId));
        Journal journal = mongoTemplate.findOne(query, Journal.class);
        String ownerId = "";
        if(journal != null)
        {
            ownerId = journal.getOwnerId();
        } else {
            ownerId = " ";
        }

        JournalEntrySearchCommand tempCommand = new JournalEntrySearchCommand();
        tempCommand.setOwnerId(ownerId);

        Map<String, Object> params = new HashMap<>();

        JournalEntryProfile report = htmlService.returnDataReport(journalId,tempCommand,token);

        // String fromDateStr = report.getFromDate();
        // String toDateStr = report.getToDate();
        // SimpleDateFormat formatter = null;
        // Date fromDate = null;
        // Date toDate = null;
        // formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS\'Z\'");
        // try {
        //     fromDate = formatter.parse(fromDateStr);
        //     toDate = formatter.parse(toDateStr);
        //     System.out.println(fromDate+" "+toDate);
        // } catch(ParseException e){
        //     e.printStackTrace();
        // }

        List<String> filterChildren = report.getFilterChildren();
        List<ChildProfile> children = report.getChildren();

        params.put("fname", report.getFname());
        params.put("lname", report.getLname());
        // params.put("fromDate", fromDate);
        // params.put("toDate", toDate);
        params.put("filterChildren", filterChildren);
        params.put("children", children);
        params.put("entries", report.getEntryList());

        return new ModelAndView("report", params);
    }
}