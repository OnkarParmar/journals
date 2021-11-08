package com.teamteach.journalmgmt.domain.usecases;

import com.teamteach.journalmgmt.domain.command.*;
import com.teamteach.journalmgmt.domain.ports.in.*;
import com.teamteach.journalmgmt.domain.models.*;
import com.teamteach.journalmgmt.domain.ports.out.*;
import com.teamteach.journalmgmt.domain.responses.*;
import com.teamteach.journalmgmt.infra.persistence.dal.JournalDAL;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import com.lowagie.text.DocumentException;
import com.teamteach.commons.connectors.rabbit.core.IMessagingPort;
import com.teamteach.commons.security.jwt.JwtOperationsWrapperSvc;
import com.teamteach.commons.security.jwt.JwtUser;
import com.teamteach.commons.connectors.rabbit.core.IMessagingPort;

import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

@RequiredArgsConstructor
public class JournalUse implements IJournalMgmt{

    final IJournalRepository journalRepository;
    final IMessagingPort messagingPort;
    final RestTemplate restTemplate;

    @Autowired
        private MoodsService moodsService;

    @Autowired
        private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
        private JournalDAL journalDAL;

    @Autowired
        private ReportService reportService;

    @Autowired
        private ProfileService profileService;

    @Autowired
        private IJournalEntryMgmt journalEntryMgmt;

    @Autowired
        private JwtOperationsWrapperSvc jwtOperationsWrapperSvc;

    @Autowired
        private FileUploadService fileUploadService;

    @PostConstruct
        void registerWithMQ() {
            messagingPort.registerGeneralResponseListener("event.createjournal", UserSignupInfo.class, queueConsumer);
        }

    Consumer<UserSignupInfo> queueConsumer = new Consumer<UserSignupInfo>() {
        @Override
            public void accept(UserSignupInfo userSignupInfo) {
                if(userSignupInfo.getAction().equals("signup")){
                    JournalCommand journalCommand = new JournalCommand(userSignupInfo);
                    journalCommand.setJournalType("PARENT");
                    ObjectResponseDto response = savePrivate(journalCommand);
                } else if(userSignupInfo.getAction().equals("delete")){
                    ObjectResponseDto response = deleteEntriesForOwner(userSignupInfo.getOwnerId());
                }
            }
    };

    public ObjectResponseDto deleteEntriesForOwner(String ownerId) {
        journalDAL.removeJournalEntries("ownerId",ownerId);
        return ObjectResponseDto.builder()
            .success(true)
            .message("Entry deletion successful")
            .build();
    }

    @Override
        public ObjectResponseDto createJournal(JournalCommand journalCommand){
            Journal journal = null;
            if(journalCommand.getOwnerId() != null){
                HashMap<SearchKey,Object> searchCriteria = new HashMap<>();
                searchCriteria.put(new SearchKey("ownerId",true),journalCommand.getOwnerId());
                List<Journal> journals = journalDAL.getJournals(searchCriteria);
                journal = journals.isEmpty() ? null : journals.get(0);
            }
            else {
                journalCommand.setOwnerId("0");
            }

            if (journal != null && journalCommand.getJournalType().equals("PARENT")) {
                return ObjectResponseDto.builder()
                    .success(false)
                    .message("A Journal with this ownerID already exists!")
                    .object(journal)
                    .build();
            } else {
                Date date = new Date(System.currentTimeMillis());
                journal = Journal.builder()
                    .journalId(sequenceGeneratorService.generateSequence(Journal.SEQUENCE_NAME))
                    .title(journalCommand.getTitle())
                    .desc(journalCommand.getDesc())
                    .journalType(journalCommand.getJournalType())
                    .ownerId(journalCommand.getOwnerId())
                    .createdAt(date)
                    .updatedAt(date)
                    .name(journalCommand.getName())
                    .journalYear(journalCommand.getJournalYear())
                    .build();

                if(journalCommand.getJournalType().equals("Class")||journalCommand.getJournalType().equals("Student")){
                    journal.setActive(true);
                }
                journalDAL.saveJournal(journal, true);
                return ObjectResponseDto.builder()
                    .success(true)
                    .message("Journal created successfully")
                    .object(journal)
                    .build();
            }
        }

    @Override
        public ObjectResponseDto editJournal(EditJournalCommand editJournalCommand ,String journalId){
            if (editJournalCommand.getJournalType() == null) {
                return ObjectResponseDto.builder()
                                        .success(false)
                                        .message("Please provide JournalType in the requestBody")
                                        .object(editJournalCommand)
                                        .build();
            }
            HashMap<SearchKey,Object> searchCriteria = new HashMap<>();
            searchCriteria.put(new SearchKey("journalId",false),journalId);
            List<Journal> journals = journalDAL.getJournals(searchCriteria);
            Journal editModel = journals.isEmpty() ? null : journals.get(0);
            if (editModel == null) {
                return ObjectResponseDto.builder()
                                        .success(false)
                                        .message("No Journal record found with given journalId and userType")
                                        .object(editModel)
                                        .build();
            }
            if(editJournalCommand.getTitle() != null){
                editModel.setTitle(editJournalCommand.getTitle());
            }
            if(editJournalCommand.getName() != null){
                editModel.setName(editJournalCommand.getName());
            }
            if(editJournalCommand.getDesc() != null){
                editModel.setDesc(editJournalCommand.getDesc());
            }
            if(editJournalCommand.getJournalType() != null){
                editModel.setJournalType(editJournalCommand.getJournalType());
            }
            if(editJournalCommand.getJournalYear() != null){
                editModel.setJournalYear(editJournalCommand.getJournalYear());
            }
            if(editJournalCommand.isActive() == false){
                editModel.setActive(false);
            }
            if(editJournalCommand.isActive() == true){
                editModel.setActive(true);
            }

            journalDAL.saveJournal(editModel, false);
            return ObjectResponseDto.builder()
                                    .success(true)
                                    .message(editJournalCommand.getJournalType()+" profile updated successfully")
                                    .object(editModel)
                                    .build();

        }

    @Override
        public ObjectListResponseDto<JournalResponse> findAll() {
            List<JournalResponse> journalResponses = new ArrayList<>();
            List<Journal> journals = journalDAL.getJournals(null);
            for(Journal journal: journals){
                journalResponses.add(new JournalResponse(journal));
            }
            return new ObjectListResponseDto<>(
                    true, 
                    "Journal records retrieved successfully!", 
                    journalResponses);
        }

    @Override
        public ObjectResponseDto delete(String id) {
            try {
                journalDAL.removeJournal(id);
                return ObjectResponseDto.builder()
                    .success(true)
                    .message("Journal deleted successfully")
                    .build();
            } catch (RuntimeException e) {
                return ObjectResponseDto.builder()
                    .success(false)
                    .message("Journal deletion failed")
                    .build();
            }
        }

    @Override
        public ObjectListResponseDto<JournalResponse> findById(String ownerId, String accessToken) {
            ParentProfileResponseDto parentProfile = profileService.getProfile(ownerId, accessToken);
            if (parentProfile == null) {
                return new ObjectListResponseDto<>(false, "A Parent with this ownerId does not exist!", null);
            }
            String timezone = parentProfile.getTimezone();
            List<JournalResponse> journalResponses = new ArrayList<>();
            HashMap<SearchKey,Object> searchCriteria = new HashMap<>();
            searchCriteria.put(new SearchKey("ownerId",true),ownerId);
            List<Journal> journals = journalDAL.getJournals(searchCriteria);
            journals = journals.isEmpty() ? null : journals;

            if (journals == null) {
                return new ObjectListResponseDto<>(false, "A Journal with this ownerId does not exist!", null);
            } else {
                for (Journal journal : journals) {
                    JournalResponse journalResponse = new JournalResponse(journal);
                    journalResponse.setMoods(moodsService.getMoodsCount(journal.getJournalId()));
                    journalResponse.setEntryCount();
                    journalResponse.setDesc(addDescription(timezone));
                    journalResponse.setActive(journal.isActive());
                    journalResponse.setName(journal.getName());
                    journalResponse.setJournalType(journal.getJournalType());
                    journalResponses.add(journalResponse);
                }            
            }
            return new ObjectListResponseDto<>(true, "Journal records retrieved successfully!", journalResponses);
        }

    public String addDescription(String timezone){
        String des;
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        Date now = new Date(System.currentTimeMillis());
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        int hourTime = Integer.parseInt(formatter.format(now));
        int timeArea;
        if(hourTime > 3 && hourTime < 12) timeArea = 0;
        else if(hourTime >= 12 && hourTime < 17) timeArea = 1;
        else timeArea = 2;
        switch(timeArea){
            case 0 : des = "Good Morning! How are you doing today?";
                     break;
            case 1 : des = "Good Afternoon! How are you doing today?";
                     break;
            case 2 : des = "Good Evening! How are you doing today?";
                     break;
            default : des = "How are you doing today?";
                      break; 
        }
        return des;
    }

    @Override
        public ObjectResponseDto findByTitle(String title) {
            HashMap<SearchKey,Object> searchCriteria = new HashMap<>();
            searchCriteria.put(new SearchKey("title",false),title);
            List<Journal> journals = journalDAL.getJournals(searchCriteria);
            Journal journal = journals.isEmpty() ? null : journals.get(0);

            if (journal == null) {
                return ObjectResponseDto.builder()
                    .success(false)
                    .message("A Journal with this name does not exist!")
                    .build();
            } else {
                return ObjectResponseDto.builder()
                    .success(true)
                    .message("Journal record retrieved!")
                    .object(journal)
                    .build();
            }
        }

    @Override
        public ObjectResponseDto savePrivate(JournalCommand journalCommand) {
            HashMap<SearchKey,Object> searchCriteria = new HashMap<>();
            searchCriteria.put(new SearchKey("ownerId",false),"0");
            searchCriteria.put(new SearchKey("journalType",false),journalCommand.getJournalType());
            List<Journal> journals = journalDAL.getJournals(searchCriteria);
            Journal journal = journals.isEmpty() ? null : journals.get(0);

            if(journal == null) {
                return ObjectResponseDto.builder()
                    .success(false)
                    .message("No master journal found with type " + journalCommand.getJournalType())
                    .build();
            }
            journalCommand.setTitle(journal.getTitle());
            journalCommand.setDesc(journal.getDesc());
            return createJournal(journalCommand);
        }

    @Override
        public ObjectResponseDto sendReport(SendReportInfo sendReportInfo, String token) {
            String[] tokens = token.split(" ");
            JwtUser jwtUser = jwtOperationsWrapperSvc.validateToken(tokens[1]);
            String ownerId = jwtUser.getPrincipal();
            ParentProfileResponseDto parentProfile = profileService.getProfile(ownerId, token);
            if (sendReportInfo.getEmail() == null) {
                sendReportInfo.setEmail(parentProfile.getEmail());
            }
            sendReportInfo.setFname(parentProfile.getFname());
            String pdfUrl = sendReportInfo.getUrl(); //PdfRenderingService.renderPdf(token, sendReportInfo);
            if (pdfUrl != null) {
                sendReportInfo.setUrl(pdfUrl);
                sendReportInfo.setAction("sendreport");
                messagingPort.sendMessage(sendReportInfo, "event.email");
                return new ObjectResponseDto(
                        true,
                        "Journal entries report URL sent successfully!",
                        sendReportInfo);
            } else {
                return new ObjectResponseDto(
                        false,
                        "Journal entries report creation failed!",
                        null);
            }
        }


    @Override
        public ObjectResponseDto buildReport(String journalId, JournalEntrySearchCommand journalEntrySearchCommand, String token) {
            String[] tokens = token.split(" ");
            JwtUser jwtUser = jwtOperationsWrapperSvc.validateToken(tokens[1]);
            String ownerId = jwtUser.getPrincipal();
            ParentProfileResponseDto parentProfile = profileService.getProfile(ownerId, token);

            if(parentProfile==null){
                return new ObjectResponseDto(
                        false,
                        "Parent profile not found!",
                        null);
            }


            journalEntrySearchCommand.setGoalReport(true);

            String email = journalEntrySearchCommand.getEmail() != null ? journalEntrySearchCommand.getEmail() : parentProfile.getEmail();

            ObjectResponseDto searchResponse = journalEntryMgmt.searchEntries(journalEntrySearchCommand, token);

            Object object = searchResponse.getObject();    
            JournalEntryMatrixResponse journalEntryMatrixResponse = (JournalEntryMatrixResponse)object;
            List<JournalEntriesResponse> journalEntryMatrix = journalEntryMatrixResponse.getJournalEntryMatrix();
            Map<String, Category> categories = journalEntryMatrixResponse.getCategories();
            List<JournalEntryResponse> entryList = new ArrayList<>();
            JournalEntryResponse entry = null;
            for(JournalEntriesResponse matrixEntries : journalEntryMatrix){
                if(matrixEntries.getEntries() != null && !matrixEntries.getEntries().isEmpty()){
                    entry = matrixEntries.getEntries().get(0);
                    entry.setCategoryId(categories.get(entry.getCategoryId()).getColour());
                    entryList.add(entry);
                }
            }
            List<JournalEntryResponse> sortedEntries = entryList.stream().sorted(Comparator.comparing(JournalEntryResponse::getCreatedDate).reversed()).collect(Collectors.toList());        
            String fromDate = sortedEntries.get(sortedEntries.size()-1).getCreatedAt();
            String toDate = sortedEntries.get(0).getCreatedAt();
            String children = journalEntryMatrixResponse.getChildProfiles().stream().map(e -> e.getName()).collect(Collectors.joining(" | "));
            JournalEntryProfile journalEntryProfile = JournalEntryProfile.builder()
                .email(email)
                .fname(parentProfile.getFname())
                .lname(parentProfile.getLname())
                .fromDate(fromDate)
                .toDate(toDate) 
                .children(children)                                                                    
                .entryList(sortedEntries)
                .build();
            reportService.setReport(journalEntryProfile);  
            String url = null;
            int i=1;
            try {
                String fileExt = FilenameUtils.getExtension(reportService.generateReport().getName()).replaceAll("\\s", "");
                String fileName = "journal_"+journalId+"_"+i+"."+fileExt;
                url = fileUploadService.saveTeamTeachFile("reports", fileName.replaceAll("\\s", ""),Files.readAllBytes(reportService.generateReport().toPath()));
            } catch (IOException ioe) {
                return new ObjectResponseDto(
                        false,
                        "URL generation failed!",
                        null);
            } catch (DocumentException doe) {
                return new ObjectResponseDto(
                        false,
                        "URL generation failed!",
                        null);
            }
            return new ObjectResponseDto(
                    true,
                    "URL generated successfully!",
                    url);
        }

    @Override
        public ObjectListResponseDto<JournalDashboardResponse> getJournalDashboard(String accessToken){

            List<Journal> journals = journalDAL.getJournals(null);
            journals = journals.isEmpty() ? null : journals;
            List<JournalDashboardResponse> journalDashboardResponses = new ArrayList<>();
            JournalDashboardResponse journalDashboardResponse;
            List<MoodObj> moods;
            int entryCount;
            JournalEntry journalEntry;
            Date cur;
            Date cur1;
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");  
            String strDate = "it is null by default";
            String strDate2="";
            int id = 0;
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");  
            String strDate1 = formatter1.format(date);

            if (journals == null) {
                return new ObjectListResponseDto<>(false, "No journals found!", null);
            } else {
                for (Journal journal : journals) {
                    if(journal.getOwnerId() == "0") continue;
                    moods = moodsService.getMoodsCount(journal.getJournalId());
                    entryCount = moods.stream().map(x -> x.getCount()).reduce(0, Integer::sum);
                    journalEntry = journalDAL.getJournalDashboardEntries(journal.getOwnerId(), journal.getJournalId());
                    if(journalEntry != null){
                        cur = journal.getCreatedAt();
                        cur1 = journal.getUpdatedAt();
                        strDate = formatter.format(cur);
                    }
                    if(entryCount == 0) strDate = strDate2;
                    journalDashboardResponse = new JournalDashboardResponse(id++,
                                                                            journal.getOwnerId(),
                                                                            journal.getName(),
                                                                            journal.getCreatedAt()==null?" " :formatter.format(journal.getCreatedAt()), 
                                                                            entryCount,
                                                                            journal.getUpdatedAt() == null ? " ":formatter.format(journal.getUpdatedAt()));
                    journalDashboardResponses.add(journalDashboardResponse);
                }            
            }
            return new ObjectListResponseDto<JournalDashboardResponse>(true, "Journal Dashboard retrieved successfully!", journalDashboardResponses);
        }
}