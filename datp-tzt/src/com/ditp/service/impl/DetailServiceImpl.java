package com.ditp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ditp.dao.DetailDao;
import com.ditp.entity.Detail;
import com.ditp.service.DetailService;
@Service
@Transactional
public class DetailServiceImpl implements DetailService {
	
	@Autowired
	private DetailDao detailDao;
	
	@Override
	public List<Detail> getByfinaId(String fid) {
		List<Detail> result = detailDao.getByfinaId(fid);
		return result;
	}

}
