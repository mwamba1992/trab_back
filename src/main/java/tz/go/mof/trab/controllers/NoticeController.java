package tz.go.mof.trab.controllers;

import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.service.NoticeService;
import tz.go.mof.trab.utils.GlobalMethods;
import tz.go.mof.trab.utils.Response;

@Controller
@RequestMapping("/notices")
public class NoticeController {

    private NoticeService noticeService;

    NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }


    @PostMapping(path = "/internalCreate")
    @ResponseBody
    public Response < Notice > createNotice(@RequestBody Map < String, String > req) {
        return noticeService.createNotice(req);
    }

    @PostMapping(path = "/internalEdit")
    @ResponseBody
    public Response < Notice > editNotice(@RequestBody Map < String, String > req) {
        return noticeService.editNotice(req);
    }


    @GetMapping(path = "/notice-by-number/{noticeNumber}", produces = "application/json")
    @ResponseBody
    public Notice getNoticeByNumber(@PathVariable String noticeNumber) {
        Notice notice = noticeService.findNoticeByNoticeNo(noticeNumber);
        return notice;
    }
}