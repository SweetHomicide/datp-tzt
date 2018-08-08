package com.ditp.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alipay.api.internal.util.StringUtils;
import com.ditp.dao.ProfitlogDAO;
import com.ditp.entity.ProfitLogRead;
import com.ditp.service.ProfitlogService;

@Service
@Transactional
public class ProfitlogServiceImpl implements ProfitlogService{
	@Autowired
    private ProfitlogDAO profitlogDAO;

	@Override
	public List<ProfitLogRead> search(ProfitLogRead tradeLog,String currentPage,int pageSize){
		String filter = " 1=1";
		int firstResult = (Integer.valueOf(currentPage)-1)*pageSize;
		if (StringUtils.areNotEmpty(tradeLog.getFfinaWalletid())&&!"null".equals(tradeLog.getFfinaWalletid())) {
			filter += " and ffinaWalletid='" + tradeLog.getFfinaWalletid() + "'";
		}
		if (StringUtils.areNotEmpty(tradeLog.getBeginTime())&&!"null".equals(tradeLog.getBeginTime())) {
			filter += " and tbp.fcreateTime>='" + tradeLog.getBeginTime() + "'";
		}
		if (StringUtils.areNotEmpty(tradeLog.getEndTime())&&!"null".equals(tradeLog.getEndTime())) {
			filter += " and tbp.fcreateTime<='" + tradeLog.getEndTime() + "23:59:59'";
		}
		if (StringUtils.areNotEmpty(tradeLog.getFinaName())&&!"null".equals(tradeLog.getFinaName())) {
			filter += " and tbf.fname like '%" + tradeLog.getFinaName() + "%'";
		}
		if (StringUtils.areNotEmpty(tradeLog.getFfinaId())&&!"null".equals(tradeLog.getFfinaId())) {
			filter += " and tbp.ffinaId ='" + tradeLog.getFfinaId() + "'";
		}
		if (StringUtils.areNotEmpty(tradeLog.getFuserid())&&!"null".equals(tradeLog.getFuserid())) {
			filter += " and tbp.fuserId ='" + tradeLog.getFuserid() + "'";
		}
		return 	profitlogDAO.search(filter, firstResult, pageSize);
	}
	
	@Override
	public int findCount(ProfitLogRead tradeLog) {
		String filter=" 1=1";
		if (StringUtils.areNotEmpty(tradeLog.getFfinaWalletid())&&!"null".equals(tradeLog.getFfinaWalletid())) {
			filter += " and ffinaWalletid='" + tradeLog.getFfinaWalletid() + "'";
		}
		if (StringUtils.areNotEmpty(tradeLog.getBeginTime())&&!"null".equals(tradeLog.getBeginTime())) {
			filter += " and tbp.fcreateTime>='" + tradeLog.getBeginTime() + "'";
		}
		if (StringUtils.areNotEmpty(tradeLog.getEndTime())&&!"null".equals(tradeLog.getEndTime())) {
			filter += " and tbp.fcreateTime<='" + tradeLog.getEndTime() + "23:59:59'";
		}
		if (StringUtils.areNotEmpty(tradeLog.getFinaName())&&!"null".equals(tradeLog.getFinaName())) {
			filter += " and tbf.fname like '%" + tradeLog.getFinaName() + "%'";
		}
		if (StringUtils.areNotEmpty(tradeLog.getFfinaId())&&!"null".equals(tradeLog.getFfinaId())) {
			filter += " and tbp.ffinaId ='" + tradeLog.getFfinaId() + "'";
		}
		if (StringUtils.areNotEmpty(tradeLog.getFuserid())&&!"null".equals(tradeLog.getFuserid())) {
			filter += " and tbp.fuserId ='" + tradeLog.getFuserid() + "'";
		}
		return profitlogDAO.findCount(filter);
	}

}
