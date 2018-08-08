package com.ditp.service;

import java.util.List;
import com.ditp.entity.TradeLog;
import com.ditp.entity.TradeLogRead;
import com.ruizton.main.model.Fuser;

public interface TradeService {

	String save(TradeLog tradeLog, Fuser fuser, String pwd);
	/**
	 * 
	 * @return
	 */
	List<TradeLogRead> search(TradeLogRead tradeLog,String currentPage,int pageSize);
	
	/**
	 * 查询总条数
	 * @param tradeLog
	 * @return
	 */
	int findCount(TradeLogRead tradeLog);
	
	/**
	 * 
	 * @param fuser
	 * @param amout
	 * @param password
	 * @return
	 */
	String saveKitLog(Fuser fuser,TradeLog tradeLog, String password);
}
