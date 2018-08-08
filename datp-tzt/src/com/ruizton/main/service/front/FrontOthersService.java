package com.ruizton.main.service.front;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FaboutDAO;
import com.ruizton.main.dao.FarticleDAO;
import com.ruizton.main.dao.FarticletypeDAO;
import com.ruizton.main.dao.FbankinDAO;
import com.ruizton.main.dao.FcapitaloperationDAO;
import com.ruizton.main.dao.FentrustlogDAO;
import com.ruizton.main.dao.FfriendlinkDAO;
import com.ruizton.main.dao.FintrolinfoDAO;
import com.ruizton.main.dao.FmessageDAO;
import com.ruizton.main.dao.FperiodDAO;
import com.ruizton.main.dao.FscoreDAO;
import com.ruizton.main.dao.FuserDAO;
import com.ruizton.main.dao.FvirtualcointypeDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.model.Fabout;
import com.ruizton.main.model.Farticle;
import com.ruizton.main.model.Farticletype;
import com.ruizton.main.model.Fbankin;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Ffriendlink;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Fperiod;
import com.ruizton.main.model.Fscore;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.util.Utils;

@Service
public class FrontOthersService {
	@Autowired
	private FfriendlinkDAO ffriendlinkDAO ;
	@Autowired
	private FarticleDAO farticleDAO ;
	@Autowired
	private FaboutDAO faboutDAO ;
	@Autowired
	private FarticletypeDAO farticletypeDAO ;
	@Autowired
	private FperiodDAO fperiodDAO ;
	@Autowired
	private FentrustlogDAO fentrustlogDAO ;
	@Autowired
	private FbankinDAO fbankinDAO ;
	@Autowired
	private FwalletDAO fwalletDAO ;
	@Autowired
	private FuserDAO fuserDao;
	@Autowired
	private FscoreDAO fscoreDAO;
	@Autowired
	private FintrolinfoDAO fintrolinfoDAO;
	@Autowired
	private SystemArgsService systemArgsService;
	@Autowired
	private FcapitaloperationDAO fcapitaloperationDAO ;
	@Autowired
	private FvirtualcointypeDAO virtualcointypeDAO;
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private FvirtualwalletDAO fvirtualwalletDAO ;
	@Autowired
	private FmessageDAO fmessageDAO;
	
	public List<Ffriendlink> findFfriendLink(int type){
		return this.ffriendlinkDAO.findByProperty("ftype",type) ;
	}
	
	public Fabout findFabout(String fid){
		return this.faboutDAO.findById(fid) ;
	}
	
	public List<Fabout> findFaboutAll(){
		return this.faboutDAO.findAll() ;
	}
	
	public void save(Fabout obj){
		this.faboutDAO.save(obj); ;
	}
	
	public List<Farticle> findFarticle(String farticletype,int firstResult,int maxResult){
		List<Farticle> farticles = this.farticleDAO.findFarticle(farticletype, firstResult, maxResult);
		return farticles ;
	}
	public int findFarticleCount(String farticletype){
		return this.farticleDAO.findFarticleCount(farticletype) ;
	}
	
	public int findFarticle(String farticletype){
		return this.farticleDAO.findFarticleCount(farticletype) ;
	}
	
	public List<Ffriendlink> findFriendLink(int type,int firstResult,int maxResult){
		return this.ffriendlinkDAO.findFriendLink(type, firstResult, maxResult) ;
	}
	
	public Farticle findFarticleById(String id){
		Farticle farticle = this.farticleDAO.findById(id) ;
		return farticle ;
	}
	
	public Farticletype findFarticleTypeById(String id){
		return this.farticletypeDAO.findById(id) ;
	}
	
	public List<Farticletype> findFarticleTypeAll(){
		return this.farticletypeDAO.findAll() ;
	}
	
	public List<Fperiod> findAllFperiod(long fromTime,String fvirtualCoinType){
		return this.fperiodDAO.findAllFperiod(fromTime, fvirtualCoinType) ;
	}
	
	public void addFperiod(Fperiod fperiod){
		this.fperiodDAO.save(fperiod) ;
	}
	public void addFperiods(List<Fperiod> fperiods){
		for (Fperiod fperiod : fperiods) {
			this.fperiodDAO.save(fperiod) ;
		}
	}
	
	public Fperiod getLastFperiod(Fvirtualcointype fvirtualcointype){
		return this.fperiodDAO.getLastFperiod(fvirtualcointype) ;
	}
	
	public Fentrustlog getLastFpeFentrustlog(String fvirtualcointype){
		return this.fentrustlogDAO.getLastFpeFentrustlog(fvirtualcointype) ;
	}
	
	
	public List<Fbankin> findFbankin(){
		return this.fbankinDAO.findByProperty("ok", 0) ;
	}
	
	public void updateAutoRechargeCny(Fbankin fbankin,Fwallet fwallet,Fcapitaloperation fcapitaloperation){
		try {
			this.fbankinDAO.attachDirty(fbankin) ;
			this.fwalletDAO.attachDirty(fwallet) ;
			this.fcapitaloperationDAO.save(fcapitaloperation) ;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
}
