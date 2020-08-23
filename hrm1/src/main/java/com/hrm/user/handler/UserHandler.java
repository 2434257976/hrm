package com.hrm.user.handler;

import com.hrm.commons.beans.User;
import com.hrm.user.service.IUserService;
import com.hrm.utils.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserHandler {

    @Autowired
    private IUserService userService;

    @RequestMapping("/login")
    public String login(User user, HttpSession session, Model model){
        User login_user = userService.selectUserByLoginUser(user);
        /*System.out.println(login_user);*/

        if(login_user != null){
            /*找到对应密码的账号*/
            session.setAttribute("login_user",login_user);

            return "/jsp/main.jsp";
        }else {
            /*登陆不成功，跳回首页*/
            model.addAttribute("login_error","用户名或密码错误，请重新登录！");
            return "/index.jsp";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session,Model model){
        session.removeAttribute("login_user");
        model.addAttribute("login_error","退出成功，请重新登陆！");
        return "/index.jsp";
    }


    /*用户查询*/
    @RequestMapping("/findUser")
    public String findUser(@RequestParam(defaultValue = "1") int pageIndex, User user, Model model){
        PageModel pageModel = new PageModel();
        /*给当前页码赋值*/
        pageModel.setPageIndex(pageIndex);
        int recordCount = userService.findUserCount(user);
        pageModel.setRecordCount(recordCount);
        System.out.println("111用户状态"+user.getStatus());
        /*没有加字符编码过滤器导致输入的查询条件姓名在数据库中对应不上*/
        System.out.println("222用户名"+user.getUsername());
        List<User> users = userService.findUser(user,pageModel);
        model.addAttribute("pageModel",pageModel);
        model.addAttribute("users",users);
        model.addAttribute("user",user);
        for(User u:users){
            System.out.println(u);
        }
        return "/jsp/user/user.jsp";
    }
    /*用户修改，现根据用户id查找用户将结果返回修改页面*/
    @RequestMapping("/modifyUser")
    public String modifyUser(User user,Model model, String flag,int pageIndex){
        if(flag == null){
            user = userService.findUserById(user.getId());
            model.addAttribute("user",user);
            model.addAttribute("pageIndex",pageIndex);
            return "/jsp/user/showUpdateUser.jsp";
        }else {
            int rows = userService.modifyUser(user);
            if(rows > 0){
                /*修改成功*/
                /*重定向到页面，
                * 修改过程中user的信息一直保存在model域中，导致修改完跳转到查询信息页面显示的是查询请求model域中的信息
                * model域相当于request域，在同一个请求下起作用，return默认的请求方式是请求转发
                * 这里加redirect相当于重定向*/
                return "redirect:/user/findUser?pageIndex="+pageIndex;
            }else{
                model.addAttribute("fail","用户信息修改失败！");
                return "/jsp/fail.jsp";
            }
        }
    }

    /*添加用户*/
    @RequestMapping("/addUser")
    public String addUser(User user,Model model){
       int rows = userService.addUser(user);
       if(rows > 0){
           /*使页面跳转到添加的那一页，即最后一页*/
           PageModel pagemodel = new PageModel();
           int recordCount = userService.findUserCount(null);
           pagemodel.setRecordCount(recordCount);
           /*添加成功*/
           return "redirect:/user/findUser?pageIndex="+pagemodel.getTotalSize();
       }else{
           model.addAttribute("fail","用户信息添加失败");
           return "/jsp/fail.jsp";
       }
    }

    /*用户删除*/
    @RequestMapping("/removeUser")
    public String removeUser(Integer[] ids,Model model,HttpSession session){
        /*获取当前登录用户*/
        User login_user = (User) session.getAttribute("login_user");
        for(Integer id:ids){
            if(login_user.getId() == id){
                model.addAttribute("fail","不能删除当前登录用户！");
                return "/jsp/fail.jsp";
            }
        }
        try{
            int rows = userService.removeUser(ids);
            if(rows == ids.length){
                /*删除成功，重新进行查询*/
                return "/user/findUser";
            }else{
                model.addAttribute("fail","用户删除成功！");
                return "/jsp/fail.jsp";
            }
        }catch (DataIntegrityViolationException e){
            model.addAttribute("fail","当前用户发布有公告或文档，请删除公告或文档后再删除用户！");
            return "/jsp/fail.jsp";
        }
    }

    @RequestMapping("/checkLoginname")
    @ResponseBody
    public String checkLoginname(String loginname){
       User  user=userService.findLoginname(loginname);  //查找用户名
        if(user!=null){
            return "EXIST";
        }else{
            return "OK";
        }

    }


}
