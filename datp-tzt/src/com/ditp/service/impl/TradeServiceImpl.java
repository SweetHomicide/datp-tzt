package com.ditp.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.internal.util.StringUtils;
import com.ditp.dao.FinacingDao;
import com.ditp.dao.TradeDao;
import com.ditp.dao.WalletDao;
import com.ditp.domain.Pager;
import com.ditp.entity.Finacing;
import com.ditp.entity.TradeLog;
import com.ditp.entity.TradeLogRead;
import com.ditp.entity.Wallet;
import com.ditp.service.TradeService;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.util.Comm;
import com.ruizton.util.Utils;
@Service
@Transactional
public class TradeServiceImpl implements TradeService {

	@Autowired
	private TradeDao tradeDao;
	@Autowired
	private FinacingDao finnacingDao;
	@Autowired
	private FvirtualwalletDAO fvirtualwalletDAO ;
	@Autowired
	private WalletDao walletDao;
	@Autowired
	private HttpServletRequest request;
	@Override
	public String save(TradeLog tradeLog, Fuser fuser, String pwd) {
		
		String result = "";
		
		//购买金额
		double famount = tradeLog.getFamount();
		Fvirtualwallet fvirtualwallet = null;
		//Wallet w = null;
		try {
			String ffinaId = tradeLog.getFfinaId();
			if("".equals(ffinaId)){
				result="请选择理财产品";
				return result;
			}else{
				Finacing finacing = finnacingDao.getById(ffinaId);
				//w = walletDao.findById(ffinaId, fuser.getFid());
				double fminAmount = finacing.getFminAmount();
				if(famount<fminAmount){
					result="购买金额不能小于最小金额"+fminAmount;
					return result;
				}
				double fmaxAmount = finacing.getFmaxAmount();
				if(famount>fmaxAmount){
					result="购买金额不能大于最大金额"+fmaxAmount;
					return result;
				}
				String fvitypeId = finacing.getFvitypeId();
				fvirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(fuser.getFid(), fvitypeId);
				//可用虚拟资产
				double total = fvirtualwallet.getFtotal();
				if(total<famount){
					result="购买金额不能大于可用金额";
					return result;
				}
			}
			//判断交易密码
			try {
				if(!fuser.getFtradePassword().equals(Utils.MD5(pwd))){
					result="交易密码不正确";
					return result;
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Finacing finac=null;
			Wallet wallet=null;
			finac=finnacingDao.getById(ffinaId);
			if(null!=finac)
			{
				//灵活存取
			if(finac.getFtype().equals("00101"))
			{//查询是否已经购买过本理财产品
				wallet=walletDao.findById(ffinaId, fuser.getFid());
				//不存在则新增一个
			if(null==wallet)
			{
				wallet =new Wallet();
				wallet.setFfinaId(ffinaId);
				wallet.setFuserId( fuser.getFid());
				wallet.setFtotal(famount);
				wallet.setFcreateTime(Utils.getCurTimeString());
				wallet.setFfrozen(0);
			}else{
				//存在则进行更新
				wallet.setFtotal(wallet.getFtotal()+famount);
			}
				
			}else  //固定结算
				if(finac.getFtype().equals("00102"))
			{
					wallet =new Wallet();
					wallet.setFfinaId(ffinaId);
					wallet.setFuserId( fuser.getFid());
					wallet.setFtotal(famount);
					wallet.setFcreateTime(Utils.getCurTimeString());
					wallet.setFfrozen(0);				
			}
			}
			wallet.setFlastUpdateTime(Utils.getCurTimeString());	
			walletDao.Save(wallet);//理财钱包保存
			tradeLog.setFcreateTime(Utils.getCurTimeString());
			tradeLog.setFlastUpdateTime(Utils.getCurTimeString());
			tradeLog.setFuserId(fuser.getFid());
			tradeLog.setFtype("00301");
			tradeLog.setFfinaWalletid(wallet.getFid());
			tradeDao.save(tradeLog);
			fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()-famount);
			this.fvirtualwalletDAO.save(fvirtualwallet);
			result="1";
		} catch (Exception e) {
			result=""+e.getMessage();
		}
		return result;
	}
	@Override
	public List<TradeLogRead> search(TradeLogRead tradeLog,String currentPage,int pageSize) {
		if(currentPage==null){
			currentPage="1";
		}
		String filter=" 1=1";
		int firstResult = (Integer.valueOf(currentPage)-1)*pageSize;
		if(StringUtils.areNotEmpty(tradeLog.getFfinaWalletid())&&!"null".equals(tradeLog.getFfinaWalletid()))
		{
			filter+=" and ffinaWalletid='"+tradeLog.getFfinaWalletid()+"'";
		}
		if(StringUtils.areNotEmpty(tradeLog.getFtype())&&!"null".equals(tradeLog.getFtype()))
		{
			filter+=" and tbp.ftype='"+tradeLog.getFtype()+"'";
		}
		if(StringUtils.areNotEmpty(tradeLog.getBeginTime())&&!"null".equals(tradeLog.getBeginTime()))
		{
			filter+=" and tbp.fcreateTime>='"+tradeLog.getBeginTime()+"'";
		}
		if(StringUtils.areNotEmpty(tradeLog.getEndTime())&&!"null".equals(tradeLog.getEndTime()))
		{
			filter+=" and tbp.fcreateTime<='"+tradeLog.getEndTime()+" 23:59:59'";
		}
		if(StringUtils.areNotEmpty(tradeLog.getFinaName())&&!"null".equals(tradeLog.getFinaName()))
		{
			filter+=" and tbf.fname like'%"+tradeLog.getFinaName()+"%'";
		}	
		if(StringUtils.areNotEmpty(tradeLog.getFfinaId())&&!"null".equals(tradeLog.getFfinaId()))
		{
			filter+=" and tbp.ffinaId ='"+tradeLog.getFfinaId()+"'";
		}	
		if(StringUtils.areNotEmpty(tradeLog.getFuserId())&&!"null".equals(tradeLog.getFuserId()))
		{
			filter+=" and tbp.fuserId ='"+tradeLog.getFuserId()+"'";
		}
		if(StringUtils.areNotEmpty(tradeLog.getFuserName())&&!"null".equals(tradeLog.getFuserName()))
		{
			filter+=" and fu.floginname like '%"+tradeLog.getFuserName()+"%'";
		}
		return 	tradeDao.search(filter, firstResult, pageSize);
	}

	
	
	@Override
	public int findCount(TradeLogRead tradeLog) {
		String filter=" 1=1";
		if(StringUtils.areNotEmpty(tradeLog.getFfinaWalletid())&&!"null".equals(tradeLog.getFfinaWalletid()))
		{
			filter+=" and ffinaWalletid='"+tradeLog.getFfinaWalletid()+"'";
		}
		if(StringUtils.areNotEmpty(tradeLog.getBeginTime())&&!"null".equals(tradeLog.getBeginTime()))
		{
			filter+=" and tbp.fcreateTime>='"+tradeLog.getBeginTime()+"'";
		}
		if(StringUtils.areNotEmpty(tradeLog.getEndTime())&&!"null".equals(tradeLog.getEndTime()))
		{
			filter+=" and tbp.fcreateTime<='"+tradeLog.getEndTime()+"'";
		}
		if(StringUtils.areNotEmpty(tradeLog.getFinaName())&&!"null".equals(tradeLog.getFinaName()))
		{
			filter+=" and tbf.fname like'%"+tradeLog.getFinaName()+"%'";
		}	
		if(StringUtils.areNotEmpty(tradeLog.getFfinaId())&&!"null".equals(tradeLog.getFfinaId()))
		{
			filter+=" and tbp.ffinaId ='"+tradeLog.getFfinaId()+"'";
		}	
		if(StringUtils.areNotEmpty(tradeLog.getFuserId())&&!"null".equals(tradeLog.getFuserId()))
		{
			filter+=" and tbp.fuserId ='"+tradeLog.getFuserId()+"'";
		}
		return tradeDao.findCount(filter);
	}
	
	@Override
	public String saveKitLog(Fuser fuser,TradeLog tradeLog, String password) {
		Fvirtualwallet fvirtualwallet = null;
		try {
			if(!fuser.getFtradePassword().equals(Utils.MD5(password))){
				String result="交易密码不正确";
				return result;
			}
			double famount = tradeLog.getFamount();//提现金额
			String ffinaId = tradeLog.getFfinaId();//理财产品ID
			String fuserId = fuser.getFid();//用户ID
			Wallet w = walletDao.findById(ffinaId, fuserId);//查询可用总余额
			if (famount>w.getFtotal()) {
				return "提现金额大于可用额度";
			}
			String fvitypeId = finnacingDao.getById(ffinaId).getFvitypeId();//获取虚拟资产ID(也就是提现到哪个虚拟资产)
			fvirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(fuserId, fvitypeId);//获取到虚拟资产钱包
			fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+famount);//+提现金额
			w.setFtotal(w.getFtotal()-famount);//-提现金额
			tradeLog.setFcreateTime(Utils.getCurTimeString());
			tradeLog.setFlastUpdateTime(Utils.getCurTimeString());
			tradeLog.setFfinaWalletid(w.getFid());
			tradeLog.setFuserId(fuserId);
			tradeLog.setFtype("00302");
			walletDao.Save(w);
			tradeDao.save(tradeLog);
			this.fvirtualwalletDAO.save(fvirtualwallet);
			return "提现成功";
		} catch (Exception e) {
			return "提现异常";
		}
	}
	
}
