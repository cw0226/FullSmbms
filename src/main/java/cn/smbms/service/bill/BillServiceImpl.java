package cn.smbms.service.bill;

import java.util.List;

import cn.smbms.dao.bill.BillDao;
import cn.smbms.pojo.Bill;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
@Service
public class BillServiceImpl implements BillService {
	@Resource
	private BillDao billDao;
	@Override
	public boolean add(Bill bill) {
		boolean flag = false;
		try {
			if(billDao.add(bill) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public List<Bill> getBillList(Bill bill) {
		List<Bill> billList = null;
		System.out.println("query productName ---- > " + bill.getProductName());
		System.out.println("query providerId ---- > " + bill.getProviderId());
		System.out.println("query isPayment ---- > " + bill.getIsPayment());

		try {
			billList = billDao.getBillList(bill);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return billList;
	}

	@Override
	public boolean deleteBillById(String delId) {
		boolean flag = false;
		try {
			if(billDao.deleteBillById(delId) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public Bill getBillById(String id) {
		Bill bill = null;
		try{
			bill = billDao.getBillById(id);
		}catch (Exception e) {
			e.printStackTrace();
			bill = null;
		}
		return bill;
	}

	@Override
	public boolean modify(Bill bill) {
		boolean flag = false;
		try {
			if(billDao.modify(bill) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

}
