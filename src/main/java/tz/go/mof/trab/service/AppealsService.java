package tz.go.mof.trab.service;

import tz.go.mof.trab.dto.appeal.BacklogAppealDto;
import tz.go.mof.trab.dto.appeal.CreateAppealDto;
import tz.go.mof.trab.dto.appeal.RetrialDto;
import tz.go.mof.trab.models.AppealAmount;
import tz.go.mof.trab.models.Appeals;
import tz.go.mof.trab.utils.Response;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AppealsService {
    Response<Appeals> createAppeal(CreateAppealDto request);
    Set<AppealAmount> saveAmount(Set<AppealAmount> appealAmountSet, List<Map<String, String>> amountList);

    Response uploadAppealManually(BacklogAppealDto request);


    Response registerForRetrial(RetrialDto request);

}
