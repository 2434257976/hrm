package com.hrm.employee.handler;

import com.hrm.commons.beans.Dept;
import com.hrm.commons.beans.Employee;
import com.hrm.commons.beans.Job;
import com.hrm.employee.service.IEmployeeService;
import com.hrm.utils.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeHandler {

    @Autowired
    IEmployeeService employeeService;

    //查询员工
    //使用值来单独的接收他，因为在sql语句查询需要条件判断 又可能差不到对应的数据
    @RequestMapping("/findEmployee")
    public String findEmployee(@RequestParam(defaultValue = "1") int pageIndex, Integer dept_id,Integer job_id ,Employee employee, Model model){

        if(job_id!=null){
            Job job=new Job();
            job.setId(job_id);
            employee.setJob(job);
        }
        if(dept_id!=null){
            Dept dept=new Dept();
            dept.setId(dept_id);
            employee.setDept(dept);
        }

        System.out.println("搜索条件："+employee);

        //职位下拉列表信息
        List<Job> jobs=employeeService.findAllJob();
        //部门下拉列表信息
        List<Dept> depts=employeeService.findAllDept();

        //查询总记录数
        int recordCount=employeeService.findEmployeeCount(employee);

        PageModel pageModel=new PageModel();
        pageModel.setPageIndex(pageIndex);
        pageModel.setPageSize(2);
        pageModel.setRecordCount(recordCount);

        //员工的分页查询和搜索
        List<Employee> employees=employeeService.findEmployee(employee,pageModel);
        model.addAttribute("jobs",jobs);
        model.addAttribute("depts",depts);
        model.addAttribute("employees",employees);
        model.addAttribute("pageModel",pageModel);
        model.addAttribute("employee",employee);
        return "/jsp/employee/employee.jsp";
    }

    //员工修改 按id查询要修改的员工
    @RequestMapping("/findEmployeeById")
    public String findEmployeeById(int id,int pageIndex,Model model){
        List<Job> jobs=employeeService.findAllJob();
        List<Dept> depts=employeeService.findAllDept();
        Employee employee= employeeService.findEmployeeById(id);
        model.addAttribute("employee",employee);
        model.addAttribute("jobs",jobs);
        model.addAttribute("depts",depts);
        model.addAttribute("pageIndex",pageIndex);
        return "/jsp/employee/showUpdateEmployee.jsp";
    }

    @RequestMapping("/modifyEmployee")
    @ResponseBody
    public String modifyEmployee(Employee employee){
       int rows=employeeService.modifyEmployee(employee);
       if(rows>0){
           return "OK";
       }else {
           return "FAIL";
       }

    }

    //员工删除
    @RequestMapping("/removeEmployee")
    @ResponseBody
    public String removeEmployee(Integer [] ids){
      /* for(int id:ids){
           System.out.println(id);
       }*/

      int rows=employeeService.removeEmployee(ids);
      if(rows==ids.length){
          return "OK";
      }
      else {
          return "FAIL";
      }
    }

    /*添加员工前查找到所有职位和部门*/
    @RequestMapping("/toAddEmployee")
    public String toAddEmployee(Model model){
        /*不使用session害怕下一次进行修改时候，他还存在，或者数据冲突*/
        List<Job> jobs=employeeService.findAllJob();
        List<Dept> depts=employeeService.findAllDept();
        model.addAttribute("depts",depts);
        model.addAttribute("jobs",jobs);
        return "/jsp/employee/showAddEmployee.jsp";
    }

    @RequestMapping("/addEmployee")
    @ResponseBody
    public Object addEmployee(Employee employee){

        int rows=employeeService.addEmployee(employee);
        if(rows>0){
            /*在添加完成后查询总记录数，跳转到最后一页*/
            int recordCount=employeeService.findEmployeeCount(null);
            PageModel pageModel=new PageModel();
            pageModel.setRecordCount(recordCount);
            pageModel.setPageSize(2);
            return pageModel.getTotalSize();  //总页码，最后一页
        }else {
           return  "FAIL";
        }
    }
}
