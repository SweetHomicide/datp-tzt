package com.ditp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ditp.dao.DictDAO;
import com.ditp.entity.Dict;
import com.ditp.service.DictService;
@Service
@Transactional
public class DictServiceImpl implements DictService{
	@Autowired
	private DictDAO dictDao;
	@Override
	public List<Dict> get() {
		List<Dict> list = null;
		try {
			list = dictDao.get();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	@Override
	public List<Dict> getByPid(String pid) {
		List<Dict> list = null;
		try {
			list = dictDao.getByPid(pid);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	
}
