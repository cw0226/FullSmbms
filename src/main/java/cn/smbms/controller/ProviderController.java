package cn.smbms.controller;

import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
import cn.smbms.tools.Constants;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/provider")
public class ProviderController {
    @Resource
    private ProviderService providerService;
    /**
     * 供应商管理
     * @return
     */
    @RequestMapping("/query")
    public String query(Model model, String queryProName, String queryProCode){
        if(StringUtils.isNullOrEmpty(queryProName)){
            queryProName = "";
        }
        if(StringUtils.isNullOrEmpty(queryProCode)){
            queryProCode = "";
        }
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList(queryProName,queryProCode);
        model.addAttribute("providerList", providerList);
        model.addAttribute("queryProName", queryProName);
        model.addAttribute("queryProCode", queryProCode);
        return "providerlist";
    }

    /**
     * 跳转到添加页面
     * @return
     */
    @RequestMapping("/userAdd")
    public String userAdd(){
        return "provideradd";
    }

    /**
     * 添加
     * @return
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(Provider provider){
        provider.setCreationDate(new Date());
        boolean flag = false;
        flag = providerService.add(provider);
        if(flag){
            return "redirect:query";
        }else{
            return "provideradd";
        }
    }
    @RequestMapping("/providerById/{url}/{proid}")
    public String lookAndUpdate(Model model, @PathVariable String url,@PathVariable String proid){
        if(!StringUtils.isNullOrEmpty(proid)){
            Provider provider = null;
            provider = providerService.getProviderById(proid);
            model.addAttribute("provider", provider);
            return url;
        }
        return null;
    }

    /**
     * 删除供应商
     * @return
     */
    @RequestMapping(value = "/delProvider")
    public String delProvider(String proid){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(proid)){
            int flag = providerService.deleteProviderById(proid);
            if(flag == 0){//删除成功
                resultMap.put("delResult", "true");
            }else if(flag == -1){//删除失败
                resultMap.put("delResult", "false");
            }else if(flag > 0){//该供应商下有订单，不能删除，返回订单数
                resultMap.put("delResult", String.valueOf(flag));
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * 修改供应商
     * @return
     */
    @RequestMapping(value = "/modifysave",method = RequestMethod.POST)
    public String modify(Provider provider){
        provider.setModifyDate(new Date());
        boolean flag = false;
        flag = providerService.modify(provider);
        if(flag){
            return "redirect:query";
        }else{
            return "providermodify";
        }
    }
}
