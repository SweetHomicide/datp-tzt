package com.ditp.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ditp.dao.WalletDao;
import com.ditp.entity.Wallet;
import com.ditp.entity.WalletRead;
import com.ditp.service.WalletService;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {
	@Autowired
	private WalletDao walletDao;

	@Override
	public List<WalletRead> get(String fuserId,String currentPage,int pageSize) {
		int firstResult = (Integer.valueOf(currentPage)-1)*pageSize;
		return walletDao.get(firstResult,pageSize,fuserId);
	}
	
	@Override
	public List<WalletRead> getDetails(String ffinaId,String fuserId,String currentPage, int pageSize) {
		int firstResult = (Integer.valueOf(currentPage)-1)*pageSize;
		return walletDao.getDetails(firstResult,pageSize,ffinaId,fuserId);//理财产品ID和用户ID
	}
	
	@Override
	public int findTount(String fid) {
		return walletDao.findTount(fid);
	}

	@Override
	public int findTount(String fid, String ffinaId) {
		return walletDao.findTount(fid,ffinaId);
	}
	
	@Override
	public Wallet findById(String fid,String fuserId) {
		return walletDao.findById(fid,fuserId);
	}
	
}
