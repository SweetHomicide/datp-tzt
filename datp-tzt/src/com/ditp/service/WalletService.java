package com.ditp.service;

import java.util.List;

import com.ditp.entity.Wallet;
import com.ditp.entity.WalletRead;

public interface WalletService {
	
	public List<WalletRead> get(String fuserId,String currentPage, int pageSize);

	/**
	 * 查看理财产品详情
	 * @param pageSize 
	 * @param currentPage 
	 * @param w
	 * @return
	 */
	public List<WalletRead> getDetails(String ffinaId,String fuserId, String currentPage, int pageSize);

	/**
	 * 查询总条数
	 * @return
	 */
	public int findTount(String fid);

	/**
	 * 理财详情查询总条数
	 * @param fid
	 * @param ffinaId
	 * @return
	 */
	public int findTount(String fid, String ffinaId);

	/**
	 * 根据id查询活期理财余额
	 * @param fid
	 * @return
	 */
	public Wallet findById(String fid,String fuserId);
}
