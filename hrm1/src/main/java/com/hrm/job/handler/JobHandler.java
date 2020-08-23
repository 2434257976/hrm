package com.hrm.job.handler;

import com.hrm.commons.beans.Job;
import com.hrm.job.service.IJobService;
import com.hrm.utils.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/job")
public class JobHandler {

    @Autowired
    IJobService jobService;

    @RequestMapping("/findJob")
    public String findJob(@RequestParam(defaultValue = "1") int pageIndex, String name, Model model){
        PageModel pageModel=new PageModel();
        pageModel.setPageIndex(pageIndex);
        List<Job> jobs= jobService.findJob(name,pageModel);
        /*for(Job j: jobs){
            System.out.print(j);
        }*/
        int recordCount=jobService.findJobCount(name);
        pageModel.setRecordCount(recordCount);
        model.addAttribute("pageModel",pageModel);
        model.addAttribute("jobs",jobs);
        model.addAttribute("name",name);
        return  "/jsp/job/job.jsp";
    }

    @RequestMapping("/findJobById")
    public String findJobById(int id,Model model,int pageIndex){
        Job job=jobService.findJobById(id);
        model.addAttribute("job",job);
        model.addAttribute("pageIndex",pageIndex);
        return "/jsp/job/showUpdateJob.jsp";

    }

    @RequestMapping("/modifyJob")
    @ResponseBody
    public  String modifyJob(Job job){
        //System.out.print(job);
        int rows=jobService.modifyJob(job);
        if(rows>0){
            return "OK";
        }else {
            return "FAIL";
        }

    }

    @RequestMapping("/removeJob")
    @ResponseBody
    public String removeJob(Integer [] ids){
        try {
            int rows=jobService.removeJob(ids);
            if(rows==ids.length){
                return "OK";
            }else{
                return "FAIL";
            }
        }catch (DataIntegrityViolationException e){
            return "ERROR";
        }
    }

    @RequestMapping("/addJob")
    @ResponseBody
    public Object addJob(Job job){
        int rows=jobService.addJob(job);
        if(rows>0){
            PageModel pageModel=new PageModel();
            int recordCount=jobService.findJobCount(null);//传入null 查询全部记录信息
            pageModel.setRecordCount(recordCount);
            pageModel.getTotalSize();
            return pageModel.getTotalSize();
        }else {
            return "FAIL";
        }
    }

}
