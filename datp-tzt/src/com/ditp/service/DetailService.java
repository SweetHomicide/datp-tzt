package com.ditp.service;

import java.util.List;

import com.ditp.entity.Detail;

public interface DetailService {
	/*
	 * 根据理财产品id查询详情
	 */
	List<Detail> getByfinaId(String fid);

}
