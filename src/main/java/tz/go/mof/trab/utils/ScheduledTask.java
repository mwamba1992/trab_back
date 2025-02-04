package tz.go.mof.trab.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.ss.extractor.ExcelExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tz.go.mof.trab.dto.NoticeListAppeal;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.repositories.*;


/**
 * @author Joel M Gaitan
 * <p>
 * This Class manage all scheduled Tasks for all of out application
 */

@Component
public class ScheduledTask {


    @Autowired
    TratAppealsRepository tratAppealsRepository;

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    AppealsRepository appealsRepository;

    @Autowired
    YearlyCasesRepository yearlyCasesRepository;

    @Autowired
    AppealsSummaryRepository appealsSummaryRepository;

    @Autowired
    ApplicationRegisterRepository applicationRegisterRepository;


    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    GlobalMethods globalMethods;



    @Scheduled(cron = "0 0 8 * * MON-FRI")
    //@Scheduled(fixedRate = 86400000)
    public void savingTransactionsToAgeReportAnalysis() {

        // Create a SimpleDateFormat instance with the desired format
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        List<Appeals> apps = appealsRepository.findDecided();


        // Use a map to group appeals by judge's email
        Map<String, StringBuilder> judgeEmailMap = new HashMap<>();
        Map<String, Integer> judgeCountMap = new HashMap<>();
        Map<String, StringBuilder> supervisorSummaryMap = new HashMap<>();

        for (Appeals app : apps) {
            Date today = new Date();

            if (today.after(app.getDateOfTheLastOrder())) {
                String judgeEmail = app.getSummons().getJud().getEmail();
                String judgeName = app.getSummons().getJud().getName();

                // Append details for each judge
                judgeEmailMap.computeIfAbsent(judgeEmail, k -> new StringBuilder())
                        .append("Appeals for Hon: ").append(judgeName).append("\n")
                        .append("On Notice:\n")
                        .append("Appeal No: ").append(app.getAppealNo())
                        .append(" (Filed on: ").append(formatter.format(app.getDateOfFilling()))
                        .append(", Date Of Expected Decision: ").append(formatter.format(app.getDateOfTheLastOrder()))
                        .append(")\n\n");

                // Count pending appeals for the judge
                judgeCountMap.put(judgeName, judgeCountMap.getOrDefault(judgeName, 0) + 1);

                // Prepare summary for supervisor per judge
                supervisorSummaryMap.computeIfAbsent(judgeName, k -> new StringBuilder())
                        .append("Hon: ").append(judgeName).append("\n")
                        .append("Pending Appeal No: ").append(app.getAppealNo())
                        .append(" (Filed on: ").append(formatter.format(app.getDateOfFilling()))
                        .append(", Date Of Expected Decision: ").append(formatter.format(app.getDateOfTheLastOrder()))
                        .append(")\n\n");
            }
        }

        // Send emails to each judge
        for (Map.Entry<String, StringBuilder> entry : judgeEmailMap.entrySet()) {
            String judgeEmail = entry.getKey();
            StringBuilder messageBuilder = entry.getValue();

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("registry@trab.go.tz");
            msg.setTo(judgeEmail);
            msg.setSubject("JUDGEMENT ON NOTICE REMINDER");
            msg.setText(messageBuilder.toString().trim());

            javaMailSender.send(msg);
        }


        System.out.println("####  chairman email sent #######");
        System.out.println(supervisorSummaryMap);

        // List of supervisor email addresses
        List<String> supervisorEmails = Arrays.asList(
                "bahati.moshi@trab.go.tz",
                "sekela.mwabukusi@trab.go.tz",
                "upendo.gowele@trab.go.tz",
                "registry@trab.go.tz",
                "joel.gaitan@hazina.go.tz"); // Add more emails as needed

        // Send a summary email to each supervisor
        for (String supervisorEmail : supervisorEmails) {
            try {
                SimpleMailMessage supervisorMsg = new SimpleMailMessage();
                supervisorMsg.setFrom("registry@trab.go.tz");
                supervisorMsg.setTo(supervisorEmail);
                supervisorMsg.setSubject("Pending Appeals Summary");

                StringBuilder summaryBuilder = new StringBuilder();
                for (Map.Entry<String, StringBuilder> entry : supervisorSummaryMap.entrySet()) {
                    String judgeName = entry.getKey();
                    int pendingCount = judgeCountMap.get(judgeName);
                    summaryBuilder.append(judgeName).append(" (Total Pending: ").append(pendingCount).append("):\n")
                            .append(entry.getValue().toString().trim()).append("\n\n\n");
                }

                supervisorMsg.setText(summaryBuilder.toString().trim());
                javaMailSender.send(supervisorMsg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    @Scheduled(fixedRate = 1800000)
    public void updateYearlyCases(){
        try {
            System.out.println("######## inside updating new cases ##########");
            String[] status = {"new", "decided", "pending"};
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            month++;

            Calendar c = Calendar.getInstance();   // this takes current date
            Calendar d = Calendar.getInstance();

            System.out.println("#### year #### " + year);
            System.out.println("###### month #### " + month);


            c.set(Calendar.DAY_OF_MONTH, 1);
            d.set(Calendar.DAY_OF_MONTH, 31);


            for(String st:status){
                if(st.equals("new")){
                    YearlyCases yearlyCases = new YearlyCases();

                    int janCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "01-" + "31"));
                    int febCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "02-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "02-" + "31"));
                    int marchCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "03-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "03-" + "31"));
                    int aprilCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "04-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "04-" + "31"));
                    int mayCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "05-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "05-" + "31"));
                    int junCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "06-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "06-" + "31"));
                    int julCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "07-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "07-" + "31"));
                    int augCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "08-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "08-" + "31"));
                    int sepCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "09-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "09-" + "31"));
                    int octCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "10-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "10-" + "31"));
                    int novCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "11-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "11-" + "31"));
                    int decCollection = appealsRepository.findNewAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "12-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "12-" + "31"));


                    yearlyCases.setId("new");
                    yearlyCases.setJan(janCollection);
                    yearlyCases.setFeb(febCollection);
                    yearlyCases.setMar( marchCollection);
                    yearlyCases.setApr(aprilCollection);
                    yearlyCases.setMay(mayCollection);
                    yearlyCases.setJun(junCollection);
                    yearlyCases.setJul(julCollection);
                    yearlyCases.setAug(augCollection);
                    yearlyCases.setSep(sepCollection);
                    yearlyCases.setOct(octCollection);
                    yearlyCases.setNov(novCollection);
                    yearlyCases.setDece(decCollection);

                    if (yearlyCasesRepository.findById("new").isPresent()) {
                        YearlyCases yearlyCases1 = yearlyCasesRepository.findById("new").get();
                        TrabHelper.copyNonNullProperties(yearlyCases, yearlyCases1);
                        yearlyCasesRepository.save(yearlyCases1);
                    } else {
                        yearlyCasesRepository.save(yearlyCases);
                    }
                }

                if(st.equals("decided")){
                    YearlyCases yearlyCases = new YearlyCases();

                    int janCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "01-" + "31"));
                    int febCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "02-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "02-" + "31"));
                    int marchCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "03-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "03-" + "31"));
                    int aprilCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "04-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "04-" + "31"));
                    int mayCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "05-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "05-" + "31"));
                    int junCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "06-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "06-" + "31"));
                    int julCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "07-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "07-" + "31"));
                    int augCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "08-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "08-" + "31"));
                    int sepCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "09-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "09-" + "31"));
                    int octCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "10-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "10-" + "31"));
                    int novCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "11-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "11-" + "31"));
                    int decCollection = appealsRepository.findDecidedAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "12-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "12-" + "31"));


                    yearlyCases.setId("decided");
                    yearlyCases.setJan(janCollection);
                    yearlyCases.setFeb(febCollection);
                    yearlyCases.setMar( marchCollection);
                    yearlyCases.setApr(aprilCollection);
                    yearlyCases.setMay(mayCollection);
                    yearlyCases.setJun(junCollection);
                    yearlyCases.setJul(julCollection);
                    yearlyCases.setAug(augCollection);
                    yearlyCases.setSep(sepCollection);
                    yearlyCases.setOct(octCollection);
                    yearlyCases.setNov(novCollection);
                    yearlyCases.setDece(decCollection);

                    if (yearlyCasesRepository.findById("decided").isPresent()) {
                        YearlyCases yearlyCases1 = yearlyCasesRepository.findById("decided").get();
                        TrabHelper.copyNonNullProperties(yearlyCases, yearlyCases1);
                        yearlyCasesRepository.save(yearlyCases1);
                    } else {
                        yearlyCasesRepository.save(yearlyCases);
                    }
                }

                if(st.equals("pending")){
                    YearlyCases yearlyCases = new YearlyCases();


                    int janCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "01-" + "31"));
                    int febCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "02-" + "31"));
                    int marchCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "03-" + "31"));
                    int aprilCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "04-" + "31"));
                    int mayCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "05-" + "31"));
                    int junCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "06-" + "31"));
                    int julCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "07-" + "31"));
                    int augCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "08-" + "31"));
                    int sepCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "09-" + "31"));
                    int octCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "10-" + "31"));
                    int novCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "11-" + "31"));
                    int decCollection = appealsRepository.findPendingAppealsOnDateRange(formatter.parse(Integer.toString(year) + "-" + "01-" + "01"),
                            formatter.parse(Integer.toString(year) + "-" + "12-" + "31"));


                    yearlyCases.setId("pending");
                    yearlyCases.setJan(month>=1?janCollection:0);
                    yearlyCases.setFeb(month>=2?febCollection:0);
                    yearlyCases.setMar(month>=3?marchCollection:0);
                    yearlyCases.setApr(month>=4?aprilCollection:0);
                    yearlyCases.setMay(month>=5?mayCollection:0);
                    yearlyCases.setJun(month>=6?junCollection:0);
                    yearlyCases.setJul(month>=7?julCollection:0);
                    yearlyCases.setAug(month>=8?augCollection:0);
                    yearlyCases.setSep(month>=9?sepCollection:0);
                    yearlyCases.setOct(month>=10?octCollection:0);
                    yearlyCases.setNov(month>=11?novCollection:0);
                    yearlyCases.setDece(month>=12?decCollection:0);

                    if (yearlyCasesRepository.findById("pending").isPresent()) {
                        YearlyCases yearlyCases1 = yearlyCasesRepository.findById("pending").get();
                        TrabHelper.copyNonNullProperties(yearlyCases, yearlyCases1);
                        yearlyCasesRepository.save(yearlyCases1);
                    } else {
                        yearlyCasesRepository.save(yearlyCases);
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 1800000)
    public void updateAppealsSummary(){
        System.out.println("### appeals summary running #####");
        try {
            if(appealsSummaryRepository.findById(1L).isPresent()) {
                AppealsSummary appealsSummary = appealsSummaryRepository.findById(1L).get();
                appealsSummary.setFilled((int) appealsRepository.count());
                appealsSummary.setDecided(appealsRepository.findConludedAppeal().size());
                appealsSummary.setPending(appealsRepository.findPendingForJudgement());
                appealsSummary.setFilledApplication((int)applicationRegisterRepository.count());
                appealsSummaryRepository.save(appealsSummary);
            }else{
                AppealsSummary appealsSummary = new AppealsSummary();
                appealsSummary.setId(1L);
                appealsSummary.setFilled((int) appealsRepository.count());
                appealsSummary.setDecided(appealsRepository.findConludedAppeal().size());
                appealsSummary.setPending(appealsRepository.findPendingForJudgement());
                appealsSummary.setFilledApplication((int)applicationRegisterRepository.count());
                appealsSummaryRepository.save(appealsSummary);
            }
        }catch (Exception e){
        e.printStackTrace();
        }
    }



    @Scheduled(fixedRate = 1800000)
    public void updateTratAppeal() {
        try {
            List<Object[]> noticeListAppeals = noticeRepository.findAllTratAppeals();

            for (Object[] row : noticeListAppeals) {
                BigInteger noticeId = (BigInteger) row[0]; // Notice ID
                String description = (String) row[1]; // Description

                System.out.println("Notice ID: " + noticeId + ", Description: " + description);
                String[] descriptionParts = description.split("-");
                String appealNo = descriptionParts[0];
                String taxType = descriptionParts[2];

                Appeals appeals = appealsRepository.findAppealsByAppealNoAndTax_TaxName(appealNo, taxType);

                if (appeals.getTratAppeal() == null) {
                    handleNewTratAppeal(noticeId, appeals);
                } else {
                    updateExistingTratAppeal(noticeId, appeals);
                }
            }

            System.out.println("Size: " + noticeListAppeals.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleNewTratAppeal(BigInteger noticeId, Appeals appeals) {
        TratAppeal tratAppeal = new TratAppeal();
        tratAppeal.setTratNoticeId(noticeId.longValue());

        List<Object[]> tratAppeals = noticeRepository.findTratAppealByNoticeId(noticeId.longValue());
        if (!tratAppeals.isEmpty()) {
            tratAppeal.setDecision("NEW");
            tratAppeal.setAppealFilled(true);
        } else {
            System.out.println("Appeals not filled !!!!!!!!");
            tratAppeal.setAppealFilled(false);
        }

        TratAppeal savedTratAppeal = tratAppealsRepository.save(tratAppeal);
        appeals.setTratAppeal(savedTratAppeal);
        appealsRepository.save(appeals);
    }

    private void updateExistingTratAppeal(BigInteger noticeId, Appeals appeals) {
        TratAppeal tratAppeal = appeals.getTratAppeal();
        tratAppeal.setTratNoticeId(noticeId.longValue());

        List<Object[]> tratAppeals = noticeRepository.findTratAppealByNoticeId(noticeId.longValue());
        if (!tratAppeals.isEmpty()) {
            tratAppeal.setAppealFilled(true);
            updateTratAppealDetails(tratAppeal, tratAppeals.get(0));
        } else {
            System.out.println("Appeals not filled !!!!!!!!");
            tratAppeal.setAppealFilled(false);
        }

        TratAppeal savedAppeal = tratAppealsRepository.save(tratAppeal);
        appeals.setTratAppeal(savedAppeal);
        appealsRepository.save(appeals);
    }

    private void updateTratAppealDetails(TratAppeal tratAppeal, Object[] object) {
        try {

            if (object[15] !=null) {
                tratAppeal.setFinished(true);
                String proceedingStatus = (String) object[22];
                tratAppeal.setStatus(proceedingStatus);
                tratAppeal.setTratAppealNo(object[1].toString());
                tratAppeal.setDecision(object[27].toString());
                tratAppeal.setDecidedBy(object[14].toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
