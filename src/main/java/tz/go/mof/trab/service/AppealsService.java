package tz.go.mof.trab.service;

import tz.go.mof.trab.models.Appeals;
import tz.go.mof.trab.utils.Response;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AppealsService {
    Response<Appeals> createAppeal(Map<String, String> request);
    Set saveAmount(Set appealAmountSet, List<Map<String, String>> amountList);

    Response uploadAppealManually(Map<String, String> request);


    Response registerForRetrial(Map<String, String> request);

}
