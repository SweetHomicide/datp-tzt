package com.ditp.service;

import java.util.List;
import com.ditp.entity.ProfitLogRead;

public interface ProfitlogService {
	/**
	 * 
	 * @return
	 */
	List<ProfitLogRead> search(ProfitLogRead tradeLog,String currentPage,int pageSize);

	/**
	 * 查询总条数
	 * @param tradeLog
	 * @return
	 */
	int findCount(ProfitLogRead tradeLog);
}
