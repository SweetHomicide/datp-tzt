package com.ditp.service;

import java.util.List;

import com.ditp.entity.Dict;

public interface DictService {

	List<Dict> get();

	List<Dict> getByPid(String pid);

	
}
