package com.ruizton.main.service.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.MessageStatusEnum;
import com.ruizton.main.dao.FcapitaloperationDAO;
import com.ruizton.main.dao.FintrolinfoDAO;
import com.ruizton.main.dao.FmessageDAO;
import com.ruizton.main.dao.FscoreDAO;
import com.ruizton.main.dao.FsystemargsDAO;
import com.ruizton.main.dao.FuserDAO;
import com.ruizton.main.dao.FusersettingDAO;
import com.ruizton.main.dao.FvirtualcointypeDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Fmessage;
import com.ruizton.main.model.Fscore;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.Utils;

@Service
public class CapitaloperationService {
	@Autowired
	private FcapitaloperationDAO fcapitaloperationDAO;
	@Autowired
	private FwalletDAO walletDAO;
	@Autowired
	private FmessageDAO messageDAO;
	@Autowired
	private FsystemargsDAO systemargsDAO;
	@Autowired
	private FusersettingDAO usersettingDAO;
	@Autowired
	private FintrolinfoDAO fintrolinfoDAO;
	@Autowired
	private FuserDAO fuserDao;
	@Autowired
	private FscoreDAO fscoreDAO;
	@Autowired
	private FvirtualcointypeDAO virtualcointypeDAO;
	@Autowired
	private SystemArgsService systemArgsService;
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private FvirtualwalletDAO fvirtualwalletDAO ;
	@Autowired
	private FmessageDAO fmessageDAO;
	@Autowired
	private StationMailService stationMailService;

	public Fcapitaloperation findById(String id) {
		Fcapitaloperation fcapitaloperation = this.fcapitaloperationDAO.findById(id);
		if(fcapitaloperation.getFuser().getFwallet() != null){
			fcapitaloperation.getFuser().getFwallet().getFfrozenRmb();
		}
		return fcapitaloperation;
	}

	public void saveObj(Fcapitaloperation obj) {
		this.fcapitaloperationDAO.save(obj);
	}

	public void deleteObj(String id) {
		Fcapitaloperation obj = this.fcapitaloperationDAO.findById(id);
		this.fcapitaloperationDAO.delete(obj);
	}

	public void updateObj(Fcapitaloperation obj) {
		this.fcapitaloperationDAO.attachDirty(obj);
	}

	public List<Fcapitaloperation> findByProperty(String name, Object value) {
		return this.fcapitaloperationDAO.findByProperty(name, value);
	}

	public List<Fcapitaloperation> findAll() {
		return this.fcapitaloperationDAO.findAll();
	}

	public List<Fcapitaloperation> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Fcapitaloperation> all = this.fcapitaloperationDAO.list(firstResult, maxResults, filter,isFY);
		for (Fcapitaloperation fcapitaloperation : all) {
			fcapitaloperation.getFuser().getFemail() ;
			if(fcapitaloperation.getfAuditee_id() != null){
				fcapitaloperation.getfAuditee_id().getFname();
			}
		}
		return all;
	}

	public void updateCapital(Fcapitaloperation capitaloperation,Fwallet wallet,boolean isRecharge, Fvirtualwallet fvirtualwallet) throws RuntimeException {
		try {
			this.fcapitaloperationDAO.attachDirty(capitaloperation);
			if (fvirtualwallet!=null) {
				fvirtualwalletDAO.attachDirty(fvirtualwallet);
			} else {
				this.walletDAO.attachDirty(wallet);
			}
			if(capitaloperation.getFstatus()==3)
			{
			stationMailService.sendStationMail(capitaloperation);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public Map getTotalAmount(int type,String fstatus,boolean isToday) {
		return this.fcapitaloperationDAO.getTotalAmount(type, fstatus,isToday);
	}
	
	public Map getTotalAmountIn(int type,String fstatus,boolean isToday) {
		return this.fcapitaloperationDAO.getTotalAmountIn(type, fstatus,isToday);
	}
	
	public List getTotalGroup(String filter) {
		return this.fcapitaloperationDAO.getTotalGroup(filter);
	}
	
	public List getTotalAmountForReport(String filter) {
		return this.fcapitaloperationDAO.getTotalAmountForReport(filter);
	}
	
	public List getTotalOperationlog(String filter) {
		return this.fcapitaloperationDAO.getTotalOperationlog(filter);
	}
	
	public Map getTotalAmount(int type,String fstatus,boolean isToday,boolean isFee) {
		return this.fcapitaloperationDAO.getTotalAmount(type, fstatus,isToday,isFee);
	}
}