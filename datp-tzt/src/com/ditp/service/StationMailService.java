package com.ditp.service;

import java.util.List;

import com.ditp.domain.Page;
import com.ditp.entity.StationMail;
import com.ditp.entity.StationMailRead;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fvirtualcaptualoperation;

/**
 * 
 * @author liuruichen
 *
 */
public interface StationMailService {
	// 站内信分页查询
	public Page<StationMailRead> get(Integer pageindex, int pagesize, StationMailRead stationMailRead);

	public StationMailRead getAndUpdate(StationMail stationMail);

	// 未读数量
	public String getUnread(String userid);

	public void save(StationMail stationMail);

	String save(Fentrustlog fentrustlog, String sellFuserId, double buyFee, double sellFee);
	public String del(String[] ids,String fuserid);
	/**
	 * 后台管理员发送众筹 与 兑换消息
	 * @param subscription
	 * @param fadmin
	 */
	public void sendStationMail(Fsubscription subscription,Fadmin fadmin);
	/**
	 * 后台管理员发送 众筹中签消息
	 * @param fsubscriptionlog
	 */
	public void sendStationMail(Fsubscriptionlog log);
	/**
	 * 众筹解冻提醒
	 * @param lastValue
	 * @param log
	 */
	public void sendStationMail(double lastValue,Fsubscriptionlog log);
	/**
	 * 虚拟资产充值提现站内信
	 * @param fvi
	 */
	public void sendStationMail(Fvirtualcaptualoperation fvi);
	/**
	 * 默认资产充值提现站内信
	 * @param fca
	 */
	public void sendStationMail(Fcapitaloperation fca);
}
