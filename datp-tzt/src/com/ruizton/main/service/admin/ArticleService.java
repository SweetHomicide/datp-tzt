package com.ruizton.main.service.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FarticleDAO;
import com.ruizton.main.model.Farticle;

@Service
public class ArticleService {
	@Autowired
	private FarticleDAO farticleDao;
	@Autowired
	private HttpServletRequest request;

	public Farticle findById(String id) {
		return this.farticleDao.findById(id);
	}

	public void saveObj(Farticle obj) {
		this.farticleDao.save(obj);
	}

	public void deleteObj(String id) {
		Farticle obj = this.farticleDao.findById(id);
		this.farticleDao.delete(obj);
	}

	public void updateObj(Farticle obj) {
		this.farticleDao.attachDirty(obj);
	}

	public List<Farticle> findByProperty(String name, Object value) {
		return this.farticleDao.findByProperty(name, value);
	}

	public List<Farticle> findAll() {
		return this.farticleDao.findAll();
	}

	public List<Farticle> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Farticle> all = this.farticleDao.list(firstResult, maxResults, filter,isFY);
		return all;
	}

}