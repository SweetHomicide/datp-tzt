package com.ruizton.main.service.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.Enum.MessageStatusEnum;
import com.ruizton.main.Enum.OperationlogEnum;
import com.ruizton.main.Enum.UserGradeEnum;
import com.ruizton.main.dao.FmessageDAO;
import com.ruizton.main.dao.FoperationlogDAO;
import com.ruizton.main.dao.FuserDAO;
import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Fmessage;
import com.ruizton.main.model.Foperationlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fwallet;
import com.ruizton.util.Utils;

@Service
public class OperationLogService {
	@Autowired
	private FoperationlogDAO operationlogDAO;
	@Autowired
	private FwalletDAO walletDAO;
	@Autowired
	private FmessageDAO messageDAO;
	@Autowired
	private FuserDAO userDAO;

	public Foperationlog findById(String id) {
		Foperationlog operationLog = this.operationlogDAO.findById(id);;
        if(operationLog.getFuser().getFwallet() != null){
        	operationLog.getFuser().getFwallet().getFfrozenRmb();
        }
		return operationLog;
	}

	public void saveObj(Foperationlog obj) {
		this.operationlogDAO.save(obj);
	}

	public void deleteObj(String id) {
		Foperationlog obj = this.operationlogDAO.findById(id);
		this.operationlogDAO.delete(obj);
	}

	public void updateObj(Foperationlog obj) {
		this.operationlogDAO.attachDirty(obj);
	}

	public List<Foperationlog> findByProperty(String name, Object value) {
		return this.operationlogDAO.findByProperty(name, value);
	}

	public List<Foperationlog> findAll() {
		return this.operationlogDAO.findAll();
	}

	public List<Foperationlog> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Foperationlog> all = this.operationlogDAO.list(firstResult, maxResults, filter,isFY);
		for (Foperationlog foperationlog : all) {
			foperationlog.getFuser().getFemail();
		}
		return all;
	}
	
	public boolean updateOperationLog(String operationId,Fadmin auditor) throws RuntimeException{
		//判断能否找到记录
		try {
			Foperationlog operationLog = findById(operationId);
			if(operationLog == null){
				return false;
			}else if(operationLog.getFstatus() != OperationlogEnum.SAVE){
				return false;
			}
			double amount = operationLog.getFamount();
			Fwallet wallet = operationLog.getFuser().getFwallet();
			wallet.setFtotalRmb(wallet.getFtotalRmb()+amount);
			wallet.setFlastUpdateTime(Utils.getTimestamp());
			this.walletDAO.attachDirty(wallet);
			
			operationLog.setFlastUpdateTime(Utils.getTimestamp());
			operationLog.setFstatus(OperationlogEnum.AUDIT);
			operationLog.setFkey1(auditor.getFname());
			this.operationlogDAO.attachDirty(operationLog);
			
			String title = "管理员向您充值"+amount+"人民币,请注意查收";
			Fmessage msg = new Fmessage();
			msg.setFcreateTime(Utils.getTimestamp());
			msg.setFcontent(title);
			msg.setFreceiver(operationLog.getFuser());
			msg.setFcreator(auditor);
			msg.setFtitle(title);
			msg.setFstatus(MessageStatusEnum.NOREAD_VALUE);
			this.messageDAO.save(msg);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		
		return true;
	}

}