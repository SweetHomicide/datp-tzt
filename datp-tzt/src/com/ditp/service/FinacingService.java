package com.ditp.service;

import java.text.ParseException;
import java.util.List;

import com.ditp.entity.Finacing;
import com.ditp.entity.FinacingRead;
import com.ruizton.main.model.Fvirtualwallet;

public interface FinacingService {
	/*
	 * 获取理财列表
	 */

	List<FinacingRead> get(Integer integer);
	
	List<FinacingRead> get(Integer integer,String filter);
	/**
	 * 根据条件获取理财list
	 * @param filter
	 * @return
	 */
	boolean autoClear() throws ParseException;
	/*
	 * 保存理财
	 */
	void save(Finacing finacing);
	/*
	 * 根据id查找实体类
	 */
	Finacing getById(String fid);
	/*
	 * 根据id删除实体类
	 */
	void del(String fids);

	/**
	 * 首页推荐理财产品
	 * @param flag
	 * @return
	 */
	List<Finacing> getRecommendProduct(int flag);
	/**
	 * 理财产品列表展示
	 * @param pageSize 
	 * @param firstResult
	 * @param maxResults
	 * @param filter
	 * @param b
	 * @return
	 */
	List<FinacingRead> toList(int currentPage, int pageSize);
	
	FinacingRead getByFid(String fid);
	/**
	 * 查询总条数
	 * @return
	 */
	int findTount();
	Fvirtualwallet findVirtualWallet(String fid, String fvitypeId);

}
