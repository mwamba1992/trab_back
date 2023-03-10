package tz.go.mof.trab.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tz.go.mof.trab.models.AppealsSummary;
import tz.go.mof.trab.models.YearlyCases;
import tz.go.mof.trab.repositories.AppealsRepository;
import tz.go.mof.trab.repositories.AppealsSummaryRepository;
import tz.go.mof.trab.repositories.ApplicationRegisterRepository;
import tz.go.mof.trab.repositories.YearlyCasesRepository;


/**
 * @author Joel M Gaitan
 * <p>
 * This Class manage all scheduled Tasks for all of out application
 */

@Component
public class ScheduledTask {

    @Autowired
    AppealsRepository appealsRepository;

    @Autowired
    YearlyCasesRepository yearlyCasesRepository;

    @Autowired
    AppealsSummaryRepository appealsSummaryRepository;

    @Autowired
    ApplicationRegisterRepository applicationRegisterRepository;


    //@Scheduled(fixedRate = 100000)
//    public void savingTransactionsToAgeReportAnalysis() {
//
//
//        List<Appeals> apps = appRepository.findDecided();
//
//        System.out.println("######### Count ########" + apps.size());
//        for (Appeals app : apps) {
//
//           // if (globalMethods.checkIfIsbelowTodaysDate(app.getDateOfTheLastOrder())) {
//                //SystemUser user = userRepository.findById(Long.valueOf(app.getSummons().getJudge())).get();
//
//
//                SimpleMailMessage msg = new SimpleMailMessage();
//
//                msg.setTo(app.getSummons().getJud().getEmail());
//
//                msg.setSubject("JUDGEMENT ON NOTICE REMINDER");
//
//
//                String message = "Hon: " + app.getSummons().getJud().getName() + "\n" +
//                        "Reminder that appeal no: " + app.getAppealNo() + " of " + app.getDateOfFilling() + " has been cocluded on " + app.getConcludingDate() + " and waiting for your decision";
//
//
//                msg.setText(message);
//
//                javaMailSender.send(msg);
//
//            }
//
//        }
//
//
//        try {
//
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//
//    }


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

}
