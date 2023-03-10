package tz.go.mof.trab.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.models.AppealAmount;
import tz.go.mof.trab.repositories.AppealsAmountRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.ResponseCode;
import java.util.List;


@Controller
@CrossOrigin(origins = {"*"})
@RequestMapping("/appeal-amount")
public class AppealAmountController {

    @Autowired
    private AppealsAmountRepository amountRepository;

    private static final Logger logger = LoggerFactory.getLogger(AppealAmount.class);;

    @GetMapping(path = "/get-appeal-amount-by-id/{id}")
    @ResponseBody
    public ListResponse<AppealAmount> createAppeal(@PathVariable("id") String id)  {

        logger.info("###### Appeal Id: ###### " + id);
        ListResponse<AppealAmount> listResponse = new ListResponse<AppealAmount>();
        List<AppealAmount> appealAmountList = amountRepository.findAppealAmountByAppealId(Long.valueOf(id));

        listResponse.setData(appealAmountList);
        listResponse.setCode(ResponseCode.SUCCESS);
        listResponse.setDescription("Success");
        listResponse.setTotalElements(Long.valueOf(appealAmountList.size()));
        listResponse.setStatus(true);

        return listResponse;
    }
}
