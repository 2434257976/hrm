package com.hrm.notice.handler;

import com.hrm.commons.beans.Notice;
import com.hrm.commons.beans.User;
import com.hrm.notice.service.INoticeService;
import com.hrm.utils.PageModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/notice")
public class NoticeHandler {

    @Resource
    INoticeService noticeService;

    @RequestMapping("/findNotice")
    public String findNotice(@RequestParam(defaultValue = "1") int pageIndex,Notice notice,Model model){
        PageModel pageModel = new PageModel();
        pageModel.setPageIndex(pageIndex);
        List<Notice> notices = noticeService.findNotice(notice,pageModel);
        for(Notice n:notices){
            System.out.println(n);
        }
        model.addAttribute("notices",notices);
        model.addAttribute("pageModel",pageModel);
        int recordCount = noticeService.findNoticeCount(notice);
        model.addAttribute("notice",notice);
        pageModel.setRecordCount(recordCount);
        System.out.println("公告记录数："+recordCount);
        return "/jsp/notice/notice.jsp";
    }
    @RequestMapping("/modifyNotice")
    public String modifyNotice(Notice notice,Model model,String flag,int pageIndex){
        if(flag == null){
            notice = noticeService.findNoticeById(notice.getId());
            model.addAttribute("notice",notice);
            model.addAttribute("pageIndex",pageIndex);
            return "/jsp/notice/showUpdateNotice.jsp";
        }else{
            int rows = noticeService.modifyNotice(notice);
            if(rows > 0){
                return "redirect:/notice/findNotice?pageIndex="+pageIndex;
            }else{
                model.addAttribute("fail","公告信息修改失败！");
                return "/jsp/fail.jsp";
            }
        }
    }
    @RequestMapping("/previewNotice")
    public String previewNotice(int id,Model model){
        Notice notice = noticeService.findNoticeById(id);
        model.addAttribute("notice",notice);
        return "/jsp/notice/previewNotice.jsp";
    }
    @RequestMapping("/removeNotice")
    public String removeNotice(Integer[] ids,Model model){
        int rows = noticeService.removeNotice(ids);
        if(rows == ids.length){
            return "redirect:/notice/findNotice";
        }else{
            model.addAttribute("fail","删除公告失败！");
            return "/jsp/fail.jsp";
        }
    }
    @RequestMapping("/addNotice")
    public String addNotice(Notice notice, Model model, HttpSession session){
        //获取当前登录用户,也就是执行添加公告的用户
        User login_user = (User) session.getAttribute("login_user");
        notice.setUser(login_user);
        int rows = noticeService.addNotice(notice);
        if(rows > 0){
            PageModel pagemodel = new PageModel();
            int recordCount = noticeService.findNoticeCount(null);
            pagemodel.setRecordCount(recordCount);
            System.out.println("总记录数"+recordCount);
            return "redirect:/notice/findNotice?pageIndex="+pagemodel.getTotalSize();
        }else{
            model.addAttribute("fail","公告信息添加失败！");
            return "/jsp/fail.jsp";
        }
    }
}
