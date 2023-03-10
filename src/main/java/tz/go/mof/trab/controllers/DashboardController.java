package tz.go.mof.trab.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.models.YearlyCases;
import tz.go.mof.trab.repositories.*;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private PaymentRepository paymentRepository;


    @Autowired
    private YearlyCasesRepository yearlyCasesRepository;

    @Autowired
    private AppealsSummaryRepository appealsSummaryRepository;


    @GetMapping(path = "/appeals-count")
    @ResponseBody
    public Response<Map<String,String>> getAppealsCount(){

        int count = appealsSummaryRepository.findById(1L).get().getFilled();
        Map<String, String> map = new HashMap<>();
        map.put("count", String.valueOf(count));
        Response<Map<String,String>> response = new Response<Map<String, String>>();
        response.setData(map);
        response.setCode(ResponseCode.SUCCESS);
        response.setDescription("Success");
        response.setStatus(true);
        return response;
    }

    @GetMapping(path = "/application-count")
    @ResponseBody
    public Response<Map<String,String>> getApplicationCount() {
        int count = appealsSummaryRepository.findById(1L).get().getFilledApplication();
        Map<String, String> map = new HashMap<>();
        map.put("count", String.valueOf(count));
        Response<Map<String,String>> response = new Response<>();
        response.setData(map);
        response.setCode(ResponseCode.SUCCESS);
        response.setDescription("Success");
        response.setStatus(true);
        return response;
    }

    @GetMapping(path = "/appeal-closed-count")
    @ResponseBody
    public Response<Map<String,String>> getAppealCloseCount() {
        int count = appealsSummaryRepository.findById(1L).get().getDecided();
        Map<String, String> map = new HashMap<>();
        map.put("count", String.valueOf(count));
        Response<Map<String,String>> response = new Response<>();
        response.setData(map);
        response.setCode(ResponseCode.SUCCESS);
        response.setDescription("Success");
        response.setStatus(true);
        return response;
    }


    @GetMapping(path = "/appeal-pending-count")
    @ResponseBody
    public Response<Map<String,String>> getPendingAppeals() {
        int count = appealsSummaryRepository.findById(1L).get().getPending();
        Map<String, String> map = new HashMap<>();
        map.put("count", String.valueOf(count));
        Response<Map<String,String>> response = new Response<>();
        response.setData(map);
        response.setCode(ResponseCode.SUCCESS);
        response.setDescription("Success");
        response.setStatus(true);
        return response;
    }

    @GetMapping(path = "/payment-count")
    @ResponseBody
    public Response<Map<String,String>> getPaymentCount()  {
        long count = paymentRepository.count();
        Map<String, String> map = new HashMap<>();
        map.put("count", String.valueOf(count));
        Response<Map<String,String>> response = new Response<>();
        response.setData(map);
        response.setCode(ResponseCode.SUCCESS);
        response.setDescription("Success");
        response.setStatus(true);
        return response;
    }

    @GetMapping(path =  "/case-summary")
    @ResponseBody
    public  List<List<Object>>  getCaseSummary(){
        List<Object> data;
        List<Object> finishList = new ArrayList<>();
        List<List<Object>> lists = new ArrayList<>();
                
        List<YearlyCases> yearlyCases  = yearlyCasesRepository.findAll();
        
        for(YearlyCases cases: yearlyCases){
            data = new ArrayList<>();
            data.add(cases.getJan());
            data.add(cases.getFeb());
            data.add(cases.getMar());
            data.add(cases.getApr());
            data.add(cases.getMay());
            data.add(cases.getJun());
            data.add(cases.getJul());
            data.add(cases.getAug());
            data.add(cases.getSep());
            data.add(cases.getOct());
            data.add(cases.getNov());
            data.add(cases.getDece());


            Map<String, Object> map = new HashMap<>();
            map.put(cases.getId().toUpperCase(), data);
            finishList.add(map);

        }
        lists.add(finishList);
        System.out.println("###### case summary ########");
        TrabHelper.print(lists);
        return lists;
    }
}
