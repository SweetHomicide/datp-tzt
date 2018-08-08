package com.ruizton.main.comm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ditp.service.RedisService;
import com.ditp.util.ObjectTranscoder;
import com.ruizton.main.Enum.LinkTypeEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.model.Farticle;
import com.ruizton.main.model.Ffriendlink;
import com.ruizton.main.model.Ftradehistory;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.FriendLinkService;
import com.ruizton.main.service.admin.TradehistoryService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontOthersService;
import com.ruizton.main.service.front.FrontSystemArgsService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Comm;

public class ConstantMap {
	private static final Logger log = LoggerFactory.getLogger(ConstantMap.class);
	@Autowired
	private FrontSystemArgsService frontSystemArgsService;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService;
	@Autowired
	private FriendLinkService friendLinkService;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private TradehistoryService tradehistoryService;
	@Autowired
	private FrontOthersService frontOtherService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private RedisService redisService;

	private Map<String, Object> map = new HashMap<String, Object>();

	public void init() {
		log.info("Init SystemArgs ==> ConstantMap.");
		Map<String, String> tMap = this.frontSystemArgsService.findAllMap();
		for (Map.Entry<String, String> entry : tMap.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
		log.info("Init virtualCoinType ==> ConstantMap.");
		List<Fvirtualcointype> fvirtualcointypes = this.frontVirtualCoinService
				.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal);
		if (Comm.getISREDIS()) {
			String conStr = ObjectTranscoder.serializeToString(fvirtualcointypes);
			redisService.hset("ConstantMap", "virtualCoinType", conStr);
		} else {
			map.put("virtualCoinType", fvirtualcointypes);
		}
		// Object obj=ObjectTranscoder.deserializeByString(virtualCoinType);

		{
			String filter = "where fstatus=1 and FIsWithDraw=1";
			List<Fvirtualcointype> allWithdrawCoins = this.virtualCoinService.list(0, 0, filter, false);
			if (Comm.getISREDIS()) {
				String conStr = ObjectTranscoder.serializeToString(allWithdrawCoins);
				redisService.hset("ConstantMap", "allWithdrawCoins", conStr);			
			} else {
				map.put("allWithdrawCoins", allWithdrawCoins);
			}
		}

		{
			String filter = "where ftype=" + LinkTypeEnum.QQ_VALUE + " order by forder asc";
			List<Ffriendlink> ffriendlinks = this.friendLinkService.list(0, 0, filter, false);
			if (Comm.getISREDIS()) {
				String conStr = ObjectTranscoder.serializeToString(ffriendlinks);
				redisService.hset("ConstantMap", "quns", conStr);
			} else {
				map.put("quns", ffriendlinks);
			}
		}
		if (Comm.getISREDIS()) {
			String conStr = ObjectTranscoder.serializeToString(this.frontSystemArgsService.findFwebbaseinfoById("1"));
			redisService.hset("ConstantMap", "webinfo", conStr);
		} else {
		map.put("webinfo", this.frontSystemArgsService.findFwebbaseinfoById("1"));
		}
		{
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String key = sdf.format(c.getTime());
			String xx = "where DATE_FORMAT(fdate,'%Y-%m-%d') ='" + key + "'";
			List<Ftradehistory> ftradehistorys = this.tradehistoryService.list(0, 0, xx, false);
			if (Comm.getISREDIS()) {
				String conStr = ObjectTranscoder.serializeToString(ftradehistorys);
				redisService.hset("ConstantMap", "tradehistory", conStr);
			} else {
				map.put("tradehistory", ftradehistorys);
			}
		}

		{
			List<Fvirtualcointype> all = this.virtualCoinService.findAll();
			Map<String, List> ftradehistory7D = new HashMap<String, List>();
			for (Fvirtualcointype fvirtualcointype : all) {
				List<String> day7String = Day7UpsDowns.getDays(7);
				List day7 = new ArrayList();
				for (String s : day7String) {
					String sql2 = "where DATE_FORMAT(fdate,'%Y-%m-%d') ='" + s + "' and fvid='"
							+ fvirtualcointype.getFid() + "'";
					List<Ftradehistory> ss = this.tradehistoryService.list(0, 1, sql2, true);
					if (ss != null && ss.size() > 0) {
						day7.add(ss.get(0).getFprice());
					} else {
						day7.add(0d);
					}
				}
				System.out.print(day7);
				ftradehistory7D.put(fvirtualcointype.getFid(), day7);
			}
			if (Comm.getISREDIS()) {
				String conStr = ObjectTranscoder.serializeToString(ftradehistory7D);
				redisService.hset("ConstantMap", "ftradehistory7D", conStr);
			} else {
				map.put("ftradehistory7D", ftradehistory7D);
			}
		}

		List<Farticle> farticles = this.frontOtherService.findFarticle("2", 0, 5);
		if (farticles != null && farticles.size() > 0) {
			if (Comm.getISREDIS()) {
				String conStr = ObjectTranscoder.serializeToString(farticles);
				redisService.hset("ConstantMap", "news", conStr);
			} else {
				map.put("news", farticles);
			}
		}
        put("isHiddenDeal",Comm.getISHIDDEN_DEAL());
        put("isHiddenEX",Comm.getISHIDDEN_EX());
        put("ISHIDDEN_CROWDFUNDING",Comm.getISHIDDEN_CROWDFUNDING());
	}

	public Map<String, Object> getMap() {
		if (Comm.getISREDIS()) {	
			String conStr="";
			Map<String, String>	 redMap=redisService.getHashAll("ConstantMap");
			for(String key:redMap.keySet())
			{
				conStr= redMap.get(key);
				Object obj=ObjectTranscoder.deserializeByString(conStr);
				map.put(key, obj);
			}
		} 
		return this.map;
	}

	public synchronized void put(String key, Object value) {
		log.info("ConstantMap put key:" + key + ",value:" + value + ".");
		if (Comm.getISREDIS()) {
			String conStr = ObjectTranscoder.serializeToString(value);
			redisService.hset("ConstantMap", key, conStr);
		} else {
		map.put(key, value);
		}
	}

	public Object get(String key) {
		if (Comm.getISREDIS()) {
		String conStr=	redisService.hget("ConstantMap", key);
		Object obj=ObjectTranscoder.deserializeByString(conStr);
		return obj;
		} else {
		return map.get(key);
		}
	}

	public String getString(String key) {
		if (Comm.getISREDIS()) {
		String conStr=	redisService.hget("ConstantMap", key);
		Object obj=ObjectTranscoder.deserializeByString(conStr);
		return obj.toString();
		} else {
		return (String) map.get(key);
		}
	}
}
