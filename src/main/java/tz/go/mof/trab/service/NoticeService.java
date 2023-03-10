package tz.go.mof.trab.service;


import tz.go.mof.trab.models.Notice;
import tz.go.mof.trab.utils.Response;
import java.util.Map;


public interface NoticeService {

    Response<Notice>  createNotice(Map<String, String> req);

    Response<Notice> editNotice(Map<String, String> req);

    Notice findNoticeByNoticeNo(String noticeNo);


}
