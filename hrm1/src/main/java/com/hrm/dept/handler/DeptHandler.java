package com.hrm.dept.handler;

import com.hrm.commons.beans.Dept;
import com.hrm.dept.service.IDeptService;
import com.hrm.utils.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/dept")
public class DeptHandler {

    @Autowired
    IDeptService deptService;

    //查找部门 分页显示
    @RequestMapping("/findDept")
    public String findDept(@RequestParam(defaultValue = "1") int pageIndex, String name, Model model){
        PageModel pageModel=new PageModel();
        pageModel.setPageIndex(pageIndex);
        List<Dept> depts=deptService.findDept(name,pageModel);//将pageModel传过去就可以知道当前页的大小和起始索引了

        //查询部门记录数
        int recordCount=deptService.findDeptCount(name);
        pageModel.setRecordCount(recordCount);
        model.addAttribute("pageModel",pageModel);

        model.addAttribute("depts",depts);
        model.addAttribute("name",name);//在查询时防止查询的全部信息
        for(Dept d:depts){
            System.out.print(d);
        }
        return "/jsp/dept/dept.jsp";

    }

    //查找要修改的部门信息
    @RequestMapping("/findDeptById")
    public String findDeptById(int id,Model model,int pageIndex){
        Dept dept=deptService.findDeptById(id);
        model.addAttribute("dept",dept);
        model.addAttribute("pageIndex",pageIndex);
        return "/jsp/dept/showUpdateDept.jsp";
    }

    //修改部门信息
    @RequestMapping("/modifyDept")
    @ResponseBody  //返回数据作为响应体中的数据，而不是对应的视图地址
    public String modifyDept(Dept dept){
        System.out.println(dept);
        int rows =deptService.modifyDept(dept); //rows受影响的条目数
        System.out.print(rows);
        if (rows>0){
            return "OK";
        }else {
            return "FAIL";
        }
    }

    //部门删除
    @RequestMapping("/removeDept")
    @ResponseBody
    public String removeDept(Integer [] ids){
        try{
            int rows=deptService.removeDept(ids);
            if(rows==ids.length){
                return "OK";
            }else {
                return "FAIL";
            }
        }catch (DataIntegrityViolationException e){
            return "ERROR";
        }
    }

    //添加用户
    @RequestMapping("/addDept")
    @ResponseBody  //ajax请求都需要
    public Object addDept(Dept dept){
        int rows=deptService.addDept(dept);
        if(rows>0){
            //查找全部记录数
            int recordCount=deptService.findDeptCount(null);
            //总记录数除以页面大小
            PageModel pageModel=new PageModel();
            pageModel.setRecordCount(recordCount);
            int totalSize=pageModel.getTotalSize();
            System.out.print(totalSize);
            return totalSize;
        }else {
            return "FAIL";
        }

    }
}
