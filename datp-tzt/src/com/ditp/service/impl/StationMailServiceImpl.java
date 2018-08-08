package com.ditp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ditp.dao.StationMailDao;
import com.ditp.domain.Page;
import com.ditp.entity.StationMail;
import com.ditp.entity.StationMailRead;
import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.SubStatusEnum;
import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.dao.FuserDAO;
import com.ruizton.main.dao.FvirtualcointypeDAO;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.util.Utils;

import cn.jpush.api.utils.StringUtils;

@Service
@Transactional
public class StationMailServiceImpl implements StationMailService {
	@Autowired
	private StationMailDao stationMailDao;
	@Autowired
	private FvirtualcointypeDAO fvirtualcointypeDAO;
	@Autowired
	private FuserDAO fuserDAO;
	/**
	 * 获取 信息列表 
	 * tbs: tb_website_stationmail
	 */
	@Override
	public Page<StationMailRead> get(Integer pageindex, int pagesize, StationMailRead stationMailRead) {
		Page page = new Page();
		String innerStr="";
		String order="";
		String filter = " where tbs.fuserid='" + stationMailRead.getFuserid() + "' and tbs.fstatus!='3'";

		// 标题模糊查询
		if (StringUtils.isNotEmpty(stationMailRead.getFtitle())) {
			if(StringUtils.isNotEmpty(stationMailRead.getFtype()))
			{
				filter += "  and tbws.ftitle like '%" + stationMailRead.getFtitle() + "%'";
			}else{
			filter += "  and tbs.ftitle like '%" + stationMailRead.getFtitle() + "%'";
			}
		}        
		
		if(StringUtils.isNotEmpty(stationMailRead.getFtype()))
		{//系统消息
			filter +=" and tbs.ftype='"+stationMailRead.getFtype()+"' ";
			
			innerStr=" inner join tb_website_stationmail tbws on tbs.fsysMailId=tbws.fid";
		}else{
			filter +=" and tbs.ftype!='00607' ";
		
		}
		List<StationMailRead> list=null;
		if(StringUtils.isNotEmpty(innerStr))
		{
			order += " order by tbws.ftime desc";//tbws读取的是系统消息时间
			String queryString = "select ifnull(tu.fNickName,'系统') as fsendUserName,tbs.fid,tbs.fstatus,tbws.* from tb_website_stationmail as tbs"
					+ " left join fuser as tu on tbs.fsendUserid=tu.fId"
					+ " "+innerStr+filter+order;
			list= stationMailDao.getBySql(pageindex, pagesize, queryString);
		}else{
			order += " order by tbs.ftime desc";//tbs是个人消息时间
			list= stationMailDao.get(pageindex, pagesize, filter+order);
		}
		page.setContent(list);
		int total = 0;// 总数
		String count = stationMailDao.getCount(innerStr+filter);
		if (StringUtils.isNotEmpty(count)) {
			total = Integer.parseInt(count);
		}
		page.setTotalElements(total);
		return page;
	}

	/**
	 * 未读数量查询
	 */
	@Override
	public String getUnread(String userid) {
		try {
			//查询有没有系统消息
			String filterSys= " where ftype='00607' and fsysMailId is null and fid not  in"
					+ "(select fsysMailId from tb_website_stationmail where fuserid='"+userid+"' and  ftype='00607')";
			//未添加系统消息的数量
			String sysCount=stationMailDao.getCount(filterSys);
			//如果存在未添加的系统消息 则给用户添加
			if(StringUtils.isNotEmpty(sysCount))
			{
				if(Integer.parseInt(sysCount)>0)
				{
					List<StationMail> list=null;
					String queryString="select * from tb_website_stationmail"+filterSys;
					list= stationMailDao.get(queryString);
					for(StationMail stationMail:list)
					{
						StationMail stationMailUser=new StationMail();//每个用户插入关联用户
			        	stationMailUser.setFtype("00607");// 系统
			        	stationMailUser.setFstatus("0");
			        	stationMailUser.setFuserid(userid);
			        	stationMailUser.setFtime(Utils.getCurTimeString());
			        	stationMailUser.setFsendUserid(stationMail.getFuserid());
			        	stationMailUser.setFsysMailId(stationMail.getFid());
			        	stationMailDao.save(stationMailUser);
					}
					
				}
			}
			String filter = " where tbs.fuserid='" + userid + "' and fstatus='0'";
			String count = stationMailDao.getCount(filter);
			return count;
		} catch (Exception e) {
			return "0";
		}
	}

	@Override
	public void save(StationMail stationMail) {
		stationMailDao.save(stationMail);
	}

	@Override
	public String save(Fentrustlog buyLog, String sellFuserId, double buyFee, double sellFee) {
		StationMail buysMail = new StationMail();
		StationMail sellMail = new StationMail();
		// 您的山东美齐股 100 已经出售成功
		// 您的山东美齐股 100 已经购买成功
		// 买方 需要知道 每个buy.getFprize()买的 买了多少个buyLog.getFcount() - buyFee 花了 多少钱
		// buyLog.getFamount()
		String Title = "";
		String Content = "";
		String defSymbol = buyLog.getDefAssSymbol();// 默认资产符号
		try {
			Title = "成功买入" + buyLog.getFvirtualcointype().getfSymbol() + " " + (buyLog.getFcount() - buyFee) + "个!";
			Content = "您以每个￥" + buyLog.getFprize() + "的价格成功买入" + buyLog.getFvirtualcointype().getfSymbol() + " "
					+ (buyLog.getFcount() - buyFee) + "个,一共花费" + defSymbol + Utils.getDouble(buyLog.getFamount(),"");
			buysMail.setFuserid(buyLog.getfus_fId());
			buysMail.setFsendUserid(null);
			buysMail.setFtitle(Title);
			buysMail.setFcontent(Content);
			buysMail.setFtype("00601");// 买入
			buysMail.setFstatus("0");
			buysMail.setFtime(Utils.getCurTimeString());
			stationMailDao.save(buysMail);
			// 卖方 每个buy.getFprize()卖的 卖了多少个 buyLog.getFcount() 一共挣了多少
			// buyLog.getFamount() - sellFee
			Title = "成功卖出" + buyLog.getFvirtualcointype().getfSymbol() + " " + buyLog.getFcount() + "个!";
			Content = "您以每个￥" + buyLog.getFprize() + "的价格成功卖出" + buyLog.getFvirtualcointype().getfSymbol() + " "
					+ buyLog.getFcount() + "个,一共收到" + defSymbol + Utils.getDouble(buyLog.getFamount()-sellFee,"");
			sellMail.setFuserid(sellFuserId);
			sellMail.setFsendUserid(null);
			sellMail.setFtitle(Title);
			sellMail.setFcontent(Content);
			sellMail.setFtype("00602");// 卖出
			sellMail.setFstatus("0");
			sellMail.setFtime(Utils.getCurTimeString());
			stationMailDao.save(sellMail);
		} catch (Exception e) {
		}
		return "true";
	}

	/**
	 * 查看详情
	 * tbs: tb_website_stationmail
	 */
	@Override
	public StationMailRead getAndUpdate(StationMail stationMail) {
		String filter = " where tbs.fuserid='" + stationMail.getFuserid() + "' and tbs.fid='" + stationMail.getFid()
				+ "' and tbs.fstatus!='3'";
		String innerStr="";
		if(StringUtils.isNotEmpty(stationMail.getFtype()))
		{//系统消息
			innerStr=" inner join tb_website_stationmail tbws on tbs.fsysMailId=tbws.fid";
		}
		List<StationMailRead> list =null;
		if(StringUtils.isNotEmpty(innerStr))
		{//如果是系统消息则需要连表查询
			String queryString = "select ifnull(tu.fNickName,'系统') as fsendUserName,tbs.fid,tbs.fstatus,tbs.fsysMailId,"
					+ "tbws.* from tb_website_stationmail as tbs"
					+ " left join fuser as tu on tbs.fsendUserid=tu.fId"
					+ " "+innerStr+filter;
			list= stationMailDao.getBySql(0, 1, queryString);
		}else{
			 list = stationMailDao.get(0, 1, filter);
		}
		StationMailRead stationMailRead = null;
		if (null != list && list.size() > 0) {
			stationMailRead = list.get(0);
			if (stationMailRead.getFstatus().equals("0")) {
				stationMail.setFstatus("1");
				//如果查询系统详情则不对tiltle 和 content 进行更新
				if(StringUtils.isEmpty(innerStr))
				{
				stationMail.setFtitle(stationMailRead.getFtitle());
				stationMail.setFcontent(stationMailRead.getFcontent());
				}
				stationMail.setFtime(stationMailRead.getFtime());
				stationMail.setFsendUserid(stationMailRead.getFsendUserid());
				stationMail.setFtype(stationMailRead.getFtype());
				stationMail.setFsysMailId(stationMailRead.getFsysMailId());
				stationMailDao.save(stationMail);
			}
		}
		return stationMailRead;
	}

	@Override
	public String del(String[] ids, String fuserid) {
		String filter = " set fstatus='3' where fuserid='" + fuserid + "'";
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for (int i = 0; i < ids.length; i++) {
			if (i == 0) {
				sb.append("'" + ids[i] + "'");
			} else {
				sb.append(",'" + ids[i] + "'");
			}
		}
		sb.append(")");
		filter += " and fid in " + sb.toString();
		return stationMailDao.del(filter);
	}
	/**
	 * 后台管理员发送 众筹中签消息
	 * @param fsubscriptionlog
	 */
	public void sendStationMail(Fsubscriptionlog log)
	{
		String lastcount="";//中签份数
		String Flastqty="";//中签总数量
		String msg="众筹成功";//成功失败
		String title="";//标题
		String content="";//内容
		try {
			lastcount=log.getFlastcount()+"";
			Flastqty=log.getFlastqty()+"";
			if(log.getFstatus()==SubStatusEnum.NO)
			{
				 msg="众筹失败";
				 content="很遗憾您对"+log.getFsubscription().getFtitle()+"众筹失败了，管理员解冻后将返回金额";
			}else{
				content="恭喜您众筹成功，本次众筹中签份数"+lastcount+"，中签总数量"+Flastqty+"，请等待管理解冻后将返还金额与众筹的数量。";
			}
			title=log.getFsubscription().getFtitle()+msg+"!";
			StationMail stationMail=new StationMail();
			stationMail.setFtitle(title);
			stationMail.setFcontent(content);
			stationMail.setFtime(Utils.getCurTimeString());
			stationMail.setFtype("00603");// 众筹
			stationMail.setFstatus("0");
			stationMail.setFuserid(log.getFuser().getFid());
			stationMail.setFsendUserid(null);
			stationMailDao.save(stationMail);
		} catch (Exception e) {
		}
	}
	/**
	 * 众筹解冻提醒
	 * @param lastValue 需要返回的交易额
	 * @param lastqty 众筹数量
	 * @param log
	 */
	public void sendStationMail(double lastValue,Fsubscriptionlog log)
	{
		String symbol="￥";//支付资产符号
		String symbolCost="";//众筹资产符号
		String title="";//标题
		String content="";//内容
		if(log.getFsubscription().getFvirtualcointypeCost() != null)
		{
			symbol=log.getFsubscription().getFvirtualcointypeCost().getfSymbol();
		}
		symbolCost=log.getFsubscription().getFvirtualcointype().getfSymbol();
		title="您的"+log.getFsubscription().getFvirtualcointype().getFname()+"的众筹已解冻，请查收！";
		content="返还金额"+symbol+Utils.getDouble(lastValue,"")+"，得到"+log.getFsubscription().getFvirtualcointype().getFname()
				+symbolCost+log.getFlastqty()+"!";
		StationMail stationMail=new StationMail();
		stationMail.setFtitle(title);
		stationMail.setFcontent(content);
		stationMail.setFtime(Utils.getCurTimeString());
		stationMail.setFtype("00603");// 众筹
		stationMail.setFstatus("0");
		stationMail.setFuserid(log.getFuser().getFid());
		stationMail.setFsendUserid(null);
		stationMailDao.save(stationMail);
		
	}
     /**
      * 后台管理员发送众筹 与 兑换消息
      */
	public void sendStationMail(Fsubscription subscription,Fadmin fadmin)
	{
		String fbuyCountStr=Utils.getDouble(subscription.getFbuyCount(),"");//每人最大众筹份数
		String fminbuyCountStr=Utils.getDouble(subscription.getFminbuyCount(),"");//每人最小众筹份数
		String fbuyTimesStr=subscription.getFbuyTimes()+"";//每人最多众筹次数
		
		
		if(fbuyCountStr.equals("0"))
		{
			fbuyCountStr="不限";
		}
		if(fbuyTimesStr.equals("0"))
		{
			fbuyTimesStr="不限";
		}
		if(fminbuyCountStr.equals("0"))
		{
			fminbuyCountStr="不限";
		}
		String costName="人民币";//支付资产名称
		String symbol="￥";//支付资产符号
		if(null!=subscription.getFvirtualcointypeCost())
		{
			costName=subscription.getFvirtualcointypeCost().getFname();
			symbol=subscription.getFvirtualcointypeCost().getfSymbol();
		}else{/*
		// 获取默认兑换rmb种类
		List<Fvirtualcointype> findByParam = this.fvirtualcointypeDAO.findByParam(0, 0, " where fisDefAsset=1",
				false, "Fvirtualcointype");
		boolean flags = findByParam.size() != 0;

		if (flags) {
			symbol=findByParam.get(0).getfSymbol();
			costName=findByParam.get(0).getFname();
		} 
		*/}
		String title="";
		String content="";
		if(subscription.getFtype()==SubscriptionTypeEnum.COIN)
		{
			title=subscription.getFvirtualcointype().getFname()+"开放兑换啦！";
			content="本次"+subscription.getFvirtualcointype().getFname()+"兑换"
					    + "支付方式"+costName+"，"
						+ "兑换总数量"+Utils.getDouble(subscription.getFtotal(),"")+","
					    + "兑换价格 "+symbol+" "+subscription.getFprice()+"，"
						+ "每人最大兑换数量"+fbuyCountStr+"，"
					    + "每人最多兑换次数"+fbuyTimesStr+"，"
					    + "开始时间"+Utils.dateFormat(subscription.getFbeginTime())+"，"
					    + "结束时间"+Utils.dateFormat(subscription.getFendTime())+"。";
		}else{
		  title=subscription.getFvirtualcointype().getFname()+"开放众筹啦！";
	      content="本次"+subscription.getFvirtualcointype().getFname()+"众筹"
				+ "总份数"+Utils.getDouble(subscription.getFtotal(),"")+","
			    + "支付方式"+costName+"，"
			    + "每份价格 "+symbol+" "+Utils.getDouble(subscription.getFprice(),"")+"，"
				+ "每份"+Utils.getDouble(subscription.getFtotalqty(),"")+"个，"
				+ "每人最大众筹份数"+fbuyCountStr+"，"
			    + "每人最小众筹份数"+fminbuyCountStr+"，"
			    + "每人最多众筹次数"+fbuyTimesStr +"，"
			    + "开始时间"+Utils.dateFormat(subscription.getFbeginTime())+"，"
			    + "结束时间"+Utils.dateFormat(subscription.getFendTime())+"。";
		}
		StationMail stationMail=new StationMail();//管理员信息
		stationMail.setFtitle(title);
		stationMail.setFcontent(content);
		stationMail.setFtime(Utils.getCurTimeString());
		stationMail.setFtype("00607");// 系统
		stationMail.setFstatus("0");
		stationMail.setFuserid(fadmin.getFid());
		stationMail.setFsendUserid(fadmin.getFid());
		stationMailDao.save(stationMail);
        /*List<Fuser> listFuser=fuserDAO.findAll();
        if(null!=listFuser&&listFuser.size()>0)
        {
        	for(Fuser fu:listFuser){
        	StationMail stationMailUser=new StationMail();//每个用户插入关联用户
        	stationMailUser.setFtype("00607");// 系统
        	stationMailUser.setFstatus("0");
        	stationMailUser.setFuserid(fu.getFid());
        	stationMailUser.setFtime(Utils.getCurTimeString());
        	stationMailUser.setFsendUserid(fadmin.getFid());
        	stationMailUser.setFsysMailId(stationMail.getFid());
        	stationMailDao.save(stationMailUser);
        	}
        }*/
	}
     /**
     * 充值 提现 虚拟资产 提醒
     */
	@Override
	public void sendStationMail(Fvirtualcaptualoperation fvi) {
		// TODO Auto-generated method stub
		try {
			StationMail stationMail=new StationMail();
			String title="";
			String content="";
			if(fvi.getFtype()==1)
			{//充值
				title=fvi.getFvirtualcointype().getFname()+Utils.getDouble(fvi.getFamount(),"")+"个"+"充值成功！";
				content=fvi.getFvirtualcointype().getFname()+"充值成功，充值数量"+Utils.getDouble(fvi.getFamount(),"")+"!";
				stationMail.setFtype("00605");// 充值资产
			}else{
			//提取
				title=fvi.getFvirtualcointype().getFname()+Utils.getDouble(fvi.getFamount(),"")+"个"+"提取成功！";
				content=fvi.getFvirtualcointype().getFname()+"提取成功，提取数量"+Utils.getDouble(fvi.getFamount(),"")+"!";
				stationMail.setFtype("00606");// 提现资产
			}

			stationMail.setFtitle(title);
			stationMail.setFcontent(content);
			stationMail.setFtime(Utils.getCurTimeString());
			stationMail.setFstatus("0");
			stationMail.setFuserid(fvi.getFuser().getFid());
			stationMail.setFsendUserid("");
			stationMailDao.save(stationMail);
			
		} catch (Exception e) {
		}
	}
   /**
   * 默认资产充值站内信
    */
	@Override
	public void sendStationMail(Fcapitaloperation fca) {
		try {
			StationMail stationMail=new StationMail();
	
			String title="";
			String content="";
			//充值
			if(fca.getFtype()==1)
			{
			if(null!=fca.getFviType())
			{
	
				title=fca.getFviType().getFname()+Utils.getDouble(fca.getFtotalBTC(),"")+"个"+"已经充值成功！";
				content="您的"+title;
				
			}else{
				title="人民币"+Utils.getDouble(fca.getFamount(),"")+"已经充值成功!";
				content="您的"+title;
			}
			stationMail.setFtype("00608");// 充值
			}else{
				if(null!=fca.getFviType())
				{
		
					title=fca.getFviType().getFname()+Utils.getDouble(fca.getFtotalBTC(),"")+"个"+"已经提现成功！";
					content="您的"+title;
					
				}else{
					title="人民币"+Utils.getDouble(fca.getFamount(),"")+"已经提现成功!";
					content="您的"+title;
				}
				stationMail.setFtype("00609");// 提现
			}
			stationMail.setFtitle(title);
			stationMail.setFcontent(content);
			stationMail.setFtime(Utils.getCurTimeString());
			stationMail.setFstatus("0");
			stationMail.setFuserid(fca.getFuser().getFid());
			stationMail.setFsendUserid("");
			stationMailDao.save(stationMail);
			
		} catch (Exception e) {
		}
		
	}

}
