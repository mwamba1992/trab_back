package tz.go.mof.trab.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.bill.BillDto;
import tz.go.mof.trab.dto.bill.PortalBillRequestDto;
import tz.go.mof.trab.service.BillService;
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.TrabHelper;

import javax.validation.Valid;

@Controller
public class OtherBillController {


    private static final Logger logger = Logger.getLogger(OtherBillController.class);


    @Autowired
    GlobalMethods globalMethods;

    @Autowired
    LoggedUser loggedUser;

    @Autowired
    BillService billService;

    @RequestMapping(value = "/api/create-bill", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response<BillDto> createBill(@Valid @RequestBody PortalBillRequestDto billRequestDto) {

        logger.info("#### Request from portal ####" + billRequestDto);
        System.out.println("### request for bills ####");
        TrabHelper.print(billRequestDto);
        return billService.createBill(billRequestDto, false);


    }
}
