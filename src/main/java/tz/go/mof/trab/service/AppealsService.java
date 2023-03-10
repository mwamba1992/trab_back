package tz.go.mof.trab.service;

import tz.go.mof.trab.models.Appeals;
import tz.go.mof.trab.utils.Response;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AppealsService {
    Response<Appeals> createAppeal(Map<String, String> request);
    public  Set saveAmount(Set appealAmountSet, List<Map<String, String>> amountList);

}
