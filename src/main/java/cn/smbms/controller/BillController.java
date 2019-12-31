package cn.smbms.controller;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.bill.BillServiceImpl;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/bill")
public class BillController {
    @Resource
    private ProviderService providerService;
    @Resource
    private BillService billService;
    /**
     * 订单管理
     * @return
     */
    @RequestMapping("/query")
    public String query(HttpServletRequest request,String queryProductName,String queryProviderId,String queryIsPayment){
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList("","");
        request.setAttribute("providerList", providerList);

        if(StringUtils.isNullOrEmpty(queryProductName)){
            queryProductName = "";
        }

        List<Bill> billList = new ArrayList<Bill>();
        Bill bill = new Bill();
        if(StringUtils.isNullOrEmpty(queryIsPayment)){
            bill.setIsPayment(0);
        }else{
            bill.setIsPayment(Integer.parseInt(queryIsPayment));
        }

        if(StringUtils.isNullOrEmpty(queryProviderId)){
            bill.setProviderId(0);
        }else{
            bill.setProviderId(Integer.parseInt(queryProviderId));
        }
        bill.setProductName(queryProductName);
        billList = billService.getBillList(bill);
        request.setAttribute("billList", billList);
        request.setAttribute("queryProductName", queryProductName);
        request.setAttribute("queryProviderId", queryProviderId);
        request.setAttribute("queryIsPayment", queryIsPayment);
        return "billlist";
    }

    /**
     * 跳转添加订单页面
     * @return
     */
    @RequestMapping("/addOF")
    public String addOF(){
        return "billadd";
    }

    /**
     * 添加订单
     * @return
     */
    @RequestMapping("/add")
    public String add(HttpServletRequest request,Bill bill){
        bill.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setCreationDate(new Date());
        boolean flag = false;
        flag = billService.add(bill);
        System.out.println("add flag -- > " + flag);
        if(flag){
            return "redirect:query";
        }else{
            return "billadd";
        }
    }

    /**
     * 获取供应商列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/provideList",produces = "application/json;charset=UTF-8")
    public String getProviderlist(){
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList("","");

        return JSONArray.toJSONString(providerList);
    }


    /**
     * @return
     */
    @ResponseBody
    @RequestMapping("/delBill")
    public String delBill(String billid){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(billid)){
            boolean flag = billService.deleteBillById(billid);
            if(flag){//删除成功
                resultMap.put("delResult", "true");
            }else{//删除失败
                resultMap.put("delResult", "false");
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * 根据id获取订单
     * @param url
     * @return
     */
    @RequestMapping("/billById/{url}/{billid}")
    public String getBillById(@PathVariable String url,@PathVariable String billid, Model model){
        if(!StringUtils.isNullOrEmpty(billid)){
            Bill bill = null;
            bill = billService.getBillById(billid);
            model.addAttribute("bill", bill);
            return url;
        }
        return null;
    }

    /**
     * 修改订单
     * @param bill
     * @return
     */
    @RequestMapping(value = "modifysave",method = RequestMethod.POST)
    public String modify(Bill bill){
        bill.setModifyDate(new Date());
        boolean flag = false;
        flag = billService.modify(bill);
        if(flag){
            return "redirect:query";
        }else{
            return "billmodify";
        }
    }
}
