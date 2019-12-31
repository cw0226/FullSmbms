package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.MutipartFile;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Transactional
@RequestMapping("/user")
public class UserController extends BaseController {
    //注入对象
    @Resource
    private UserService userService;
    @Resource
    RoleService roleService;

    @RequestMapping(value = "/login")
    public String login(){
        return "login";
    }
    /**
     * 用户登录
     */
    @RequestMapping(value = "/doLogin",method = RequestMethod.POST)
    public String doLogin(ServletRequest request,HttpSession session, String userCode, String userPassword){
        //调用service方法，进行用户匹配
        User user = userService.login(userCode,userPassword);
        if(null != user){//登录成功
            //放入session
            session.setAttribute(Constants.USER_SESSION, user);
            //跳转到frame控制器
            return "redirect:frame";
        }else{
            //页面跳转（login.jsp）带出提示信息--转发
            request.setAttribute("error", "用户名或密码不正确");
            System.out.println(request.getAttribute("error"));
            return "login";
        }
    }
    /**
     * 登录成功后跳转到frame,失败跳回login
     */
    @RequestMapping("/frame")
    public String frame(HttpSession session){
        if (session.getAttribute(Constants.USER_SESSION)==null){
            return "login";
        }else{
            return "frame";
        }
    }


    /**
     * 注销
     * @param request
     * @return
     */
    @RequestMapping("/loginOut")
    public String loginOut(HttpServletRequest request){
        //清除session
        request.getSession().removeAttribute(Constants.USER_SESSION);
        return "login";
    }


    /**
     * 查询用户列表
     * @param queryname
     * @param queryUserRole
     * @param pageIndex
     * @param request
     * @return
     */
    @RequestMapping("/query")
    public String query(String queryname,String queryUserRole,String pageIndex,HttpServletRequest request){
        //queryUserName=queryname  temp=queryUserRole pageIndex= pageIndex
        //查询用户列表
        int queryUserRoles = 0;
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;
        if(queryname == null){
            queryname = "";
        }
        if(queryUserRole != null && !queryUserRole.equals("")){
            queryUserRoles = Integer.parseInt(queryUserRole);
        }

        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(NumberFormatException e){
                return "error";
            }
        }
        //总数量（表）
        int totalCount	= userService.getUserCount(queryname,queryUserRoles);
        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }


        userList = userService.getUserList(queryname,queryUserRoles,currentPageNo, pageSize);
        request.setAttribute("userList", userList);
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        request.setAttribute("roleList", roleList);
        request.setAttribute("queryUserName", queryname);
        request.setAttribute("queryUserRole", queryUserRoles);
        request.setAttribute("totalPageCount", totalPageCount);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPageNo", currentPageNo);
        return "userlist";
    }

    /**
     * 添加用户跳转页面
     */
    @RequestMapping("/addUser")
    public String addUser(){
        return "useradd";
    }

    /**
     * 添加用户
     * @return
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(HttpServletRequest request,User user, @RequestParam(value = "idFileUpload",required = false) MultipartFile attach,
                      @RequestParam(value = "workFileUpload",required = false) MultipartFile workAttach){
        String[] suffixs = {"jsp","png","jpeg","pneg"};
        String idPicPath = null;
        String workPicPath = null;
        //String path = "E:\\ideaWorkspaces\\FullSmbms\\src\\main\\webapp\\statics\\photo";
        String path = "\\usr\\local\\tomcat\\apache-tomcat-9.0.29\\webapps\\smbms\\statics\\photo";
        //证件照上传
        if(!attach.isEmpty()){
            //读取文件后缀
            String suffix = FilenameUtils.getExtension(attach.getOriginalFilename());
            if(attach.getSize() > 500000){//文件不得大于500KB
                request.setAttribute("idFileError","文件大小不得大于500KB");
                return "useradd";
            }else if(MutipartFile.suffixExactness(suffix,suffixs)){
                //修改文件名
                String newFileName = MutipartFile.getNewFileName(suffix);
                //保存文件
                if(!MutipartFile.keepFile(attach,path,newFileName)){
                    request.setAttribute("idFileError","上传失败！");
                    return "useradd";
                }
                idPicPath = newFileName;
            }else{
                request.setAttribute("idFileError","上传图片格式不正确！");
                return "useradd";
            }
        }
        //工作证上传
        if(!workAttach.isEmpty()){
            //读取文件后缀
            String suffix = FilenameUtils.getExtension(workAttach.getOriginalFilename());
            if(attach.getSize() > 500000){//文件不得大于500KB
                request.setAttribute("workFileError","文件大小不得大于500KB");
                return "useradd";
            }else if(MutipartFile.suffixExactness(suffix,suffixs)){
                //修改文件名
                String newFileName = MutipartFile.getNewFileName(suffix);
                //保存文件
                if(!MutipartFile.keepFile(workAttach,path,newFileName)){
                    request.setAttribute("workFileError","上传失败！");
                    return "useradd";
                }
                workPicPath  = newFileName;
            }else{
                request.setAttribute("workFileError","上传图片格式不正确！");
                return "useradd";
            }
        }
        user.setIdPicPath(idPicPath);
        user.setWorkPicPath(workPicPath);
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        if(userService.add(user)){
            return "redirect:query";
        }else{
            return "useradd";
        }
    }
    /**
     * 获取角色列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getRoleList",produces = "application/json;charset=UTF-8")
    public String getRoleList(){
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        return JSONArray.toJSONString(roleList);
    }

    /**
     * 判断用户编号是否存在
     * @return
     */
    @ResponseBody
    @RequestMapping("/userCodeExist")
    public String userCodeExist(String userCode) {
        //判断用户账号是否可用
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(userCode)) {
            //userCode == null || userCode.equals("")
            resultMap.put("userCode", "exist");
        } else {
            User user = userService.selectUserCodeExist(userCode);
            if (null != user) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "notexist");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * 根据id删除用户
     */
    @ResponseBody
    @RequestMapping(value = "/delUser",produces = "application/json")
    public String delUser(String uid){
        Integer delId = 0;
        try{
            delId = Integer.parseInt(uid);
        }catch (Exception e) {
            // TODO: handle exception
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(delId <= 0){
            resultMap.put("delResult", "notexist");
        }else{
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult", "true");
            }else{
                resultMap.put("delResult", "false");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * 根据id获取用户
     */
    @RequestMapping("/lookAndUpdate/{url}/{uid}")
    public String  getUserById(HttpServletRequest request,@PathVariable String url,@PathVariable String uid) {
        if (!StringUtils.isNullOrEmpty(uid)) {
            //调用后台方法得到user对象
            User user = userService.getUserById(uid);
            request.setAttribute("user", user);
            return url;
        }
        return null;
    }
   /* @ResponseBody
    @RequestMapping(value = "/viewUser")
    public User  getUserById(String uid) {
        if (!StringUtils.isNullOrEmpty(uid)) {
            //调用后台方法得到user对象
            return userService.getUserById(uid);
        }
        return null;
    }*/
    @RequestMapping("/modifyexe")
    public String modify(HttpServletRequest request,User user){
        user.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());
        if(userService.modify(user)){
            return "redirect:query";
        }else{
            return "usermodify";
        }
    }
    @RequestMapping("/pwd")
    public String pwdmodify(){
        return "pwdmodify";
    }

    /**
     * 密码修改
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pwdmodify",produces = "application/json;charset=UTF-8")
    public String pwdmodify(HttpServletRequest request,String oldpassword,String param){
        Object o = request.getSession().getAttribute(Constants.USER_SESSION);
        Map<String, String> resultMap = new HashMap<String, String>();

        if(null == o ){//session过期
            resultMap.put("result", "sessionerror");
        }else if(StringUtils.isNullOrEmpty(oldpassword)){//旧密码输入为空
            resultMap.put("result", "error");
        }else{
            String sessionPwd = ((User)o).getUserPassword();
            if(oldpassword.equals(sessionPwd)){
                resultMap.put("result", "true");
            }else{//旧密码输入不正确
                resultMap.put("result", "false");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * 修改密码
     * @return
     */
    @RequestMapping("savepwd")
    public String updatePwd(HttpServletRequest request,String newpassword){
        Object o = request.getSession().getAttribute(Constants.USER_SESSION);
        boolean flag = false;
        if(o != null && !StringUtils.isNullOrEmpty(newpassword)){
            flag = userService.updatePwd(((User)o).getId(),newpassword);
            if(flag){
                request.setAttribute(Constants.SYS_MESSAGE, "修改密码成功,请退出并使用新密码重新登录！");
                request.getSession().removeAttribute(Constants.USER_SESSION);//session注销
            }else{
                request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
            }
        }else{
            request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
        }
        return "pwdmodify";
    }
}
