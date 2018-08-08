package com.ruizton.main.auto;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ditp.redis.RedisLock;
import com.ditp.redis.RedisLockl;
import com.ditp.service.RedisService;
import com.ruizton.main.Enum.EntrustPlanStatusEnum;
import com.ruizton.main.Enum.EntrustPlanTypeEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.comm.KeyValues;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fentrustbean;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fentrustplan;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Comm;
import com.ruizton.util.Utils;

import net.sf.json.JSONObject;


//实时数据
public class RealTimeData {

	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private LatestKlinePeroid latestKlinePeroid;
	@Autowired
	private RedisService redisService;
	@Autowired
	private RedisLockl redisLock;

	// 短信黑名单
	private Map<String, Integer> black = new HashMap<String, Integer>();

	public boolean black(String ip, Integer type) {
		synchronized (this.black) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String key = ip + "_" + type + "_" + sdf.format(Utils.getTimestamp());
			Integer count = this.black.get(key);
			if (count == null) {
				count = 0;
			}
			System.out.println(count);
			if (count > 20) {
				return false;
			}
			this.black.put(key, count + 1);
			return true;
		}
	}

	// app登陆session记录
	private static Map<String, Long> appSessionMap = new HashMap<String, Long>();

	public synchronized String putAppSession(HttpSession session, Fuser fuser) {
		String loginToken = session.getId() + "_" + Utils.getTimestamp().getTime() + "_" + fuser.getFid();
		this.appSessionMap.put(loginToken, Utils.getTimestamp().getTime());
		return loginToken;
	}

	public boolean isAppLogin(String key, boolean update) {
		Long l = this.appSessionMap.get(key);
		if (l == null) {
			return false;
		} else {
			/*
			 * Timestamp time = new Timestamp(l) ;
			 * if(Utils.getTimestamp().getTime() - time.getTime() <30*3600*1000L
			 * ){ if(update==true){ this.appSessionMap.put(key,
			 * Utils.getTimestamp().getTime()) ; } return true ; }else{ return
			 * false ; }
			 */
			return true;
		}
	}

	public Fuser getAppFuser(String key) {
		Fuser fuser = null;
		try {
			if (key != null) {
				String split[] = key.split("_");
				if (split.length == 3) {
					fuser = this.frontUserService.findById(split[2]);
				}
			}
		} catch (NumberFormatException e) {
		}

		return fuser;

	}

	@Autowired
	private FrontTradeService frontTradeService;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService;
	@Autowired
	private KlinePeriodData klinePeriodData;
	private static boolean m_is_init = false;

	public static boolean getMisInit() {
		return m_is_init;
	}

	Comparator<Fentrustlog> timeComparator = new Comparator<Fentrustlog>() {
		public int compare(Fentrustlog o1, Fentrustlog o2) {
			boolean flag = false;
			if (o1.getFid() != null) {
				flag = o1.getFid().equals(o2.getFid());
			} else if (o1.getFid() == null) {
				o1.setFid("");
			} else if (o2.getFid() == null) {
				o2.setFid("");
			}
			if (flag) {
				return 0;
			}
			int ret = o2.getFcreateTime().compareTo(o1.getFcreateTime());
			if (ret == 0) {
				return o1.getFid().compareTo(o2.getFid());
			} else {
				return ret;
			}
		}
	};

	Comparator<Fentrust> prizeComparatorASC = new Comparator<Fentrust>() {
		public int compare(Fentrust o1, Fentrust o2) {
			/*
			 * boolean flag = false; if(o1.getFid() != null){ flag =
			 * o1.getFid().equals(o2.getFid()); }else if(o1.getFid() == null){
			 * o1.setFid(""); } else if(o2.getFid() == null){ o2.setFid(""); }
			 * if(flag){ return 0 ; } int ret =
			 * o1.getFprize().compareTo(o2.getFprize()) ; if(ret==0){ return
			 * o1.getFid().compareTo(o2.getFid()) ; }else{ return ret ; }
			 */
			int ret = Double.valueOf(o1.getFprize()).compareTo(o2.getFprize());
			if (ret == 0 && StringUtils.isNotBlank(o1.getFid()) && StringUtils.isNotBlank(o2.getFid())) {
				ret = o1.getFcreateTime().compareTo(o2.getFcreateTime());
				if (ret == 0) {
					ret = o1.getFid().compareTo(o2.getFid());
				}
			}
			return ret;
		}
	};
	Comparator<Fentrust> prizeComparatorDESC = new Comparator<Fentrust>() {
		public int compare(Fentrust o1, Fentrust o2) {
			/*
			 * boolean flag = false; if(o1.getFid() !=null){ flag=
			 * o1.getFid().equals(o2.getFid()); }else if(o1.getFid() == null){
			 * o1.setFid(""); } else if(o2.getFid() == null){ o2.setFid(""); }
			 * if(flag){ return 0 ; } int ret =
			 * o2.getFprize().compareTo(o1.getFprize()) ; if(ret==0){
			 * 
			 * return o1.getFid().compareTo(o2.getFid()) ;
			 * 
			 * 
			 * }else{ return ret ; }
			 */
			int ret = Double.valueOf(o2.getFprize()).compareTo(o1.getFprize());
			if (ret == 0 && StringUtils.isNotBlank(o1.getFid()) && StringUtils.isNotBlank(o2.getFid())) {
				ret = o1.getFcreateTime().compareTo(o2.getFcreateTime());
				if (ret == 0) {
					ret = o1.getFid().compareTo(o2.getFid());
				}
			}
			return ret;

		}
	};

	Comparator<Fentrustplan> planPrizeComparatorASC = new Comparator<Fentrustplan>() {
		public int compare(Fentrustplan o1, Fentrustplan o2) {
			/*
			 * boolean flag = false; if(o1.getFid() != null){ flag =
			 * o1.getFid().equals(o2.getFid()); }else if(o1.getFid() == null){
			 * o1.setFid(""); } else if(o2.getFid() == null){ o2.setFid(""); }
			 * if(flag){ return 0 ; } int ret =
			 * o1.getFprize().compareTo(o2.getFprize()) ; if(ret==0){ return
			 * o1.getFid().compareTo(o2.getFid()) ; }else{ return ret ; }
			 */
			int ret = o1.getFprize().compareTo(o2.getFprize());
			if (ret == 0 && StringUtils.isNotBlank(o1.getFid()) && StringUtils.isNotBlank(o2.getFid())) {
				ret = o1.getFcreateTime().compareTo(o2.getFcreateTime());
				if (ret == 0) {
					ret = o1.getFid().compareTo(o2.getFid());
				}
			}
			return ret;
		}
	};
	Comparator<Fentrustplan> planPrizeComparatorDESC = new Comparator<Fentrustplan>() {
		public int compare(Fentrustplan o1, Fentrustplan o2) {
			/*
			 * boolean flag = false; if( o1.getFid() != null){ flag =
			 * o1.getFid().equals(o2.getFid()); }else if(o1.getFid() == null){
			 * o1.setFid(""); } else if(o2.getFid() == null){ o2.setFid(""); }
			 * if(flag){ return 0 ; } int ret =
			 * o2.getFprize().compareTo(o1.getFprize()) ; if(ret==0){ return
			 * o1.getFid().compareTo(o2.getFid()) ; }else{ return ret ; }
			 */
			int ret = o2.getFprize().compareTo(o1.getFprize());
			if (ret == 0 && StringUtils.isNotBlank(o1.getFid()) && StringUtils.isNotBlank(o2.getFid())) {
				ret = o1.getFcreateTime().compareTo(o2.getFcreateTime());
				if (ret == 0) {
					ret = o1.getFid().compareTo(o2.getFid());
				}
			}
			return ret;
		}
	};

	private Map<String, Boolean> refreshBuyDepthData = new HashMap<String, Boolean>();
	private Map<String, Boolean> refreshSellDepthData = new HashMap<String, Boolean>();

	// 成功完全成交的订单
	private Map<String, TreeSet<Fentrustlog>> entrustSuccessMap = new HashMap<String, TreeSet<Fentrustlog>>();
	// 委托买入订单（币种id==>订单）
	private Map<String, TreeSet<Fentrust>> buyDepthMap = new HashMap<String, TreeSet<Fentrust>>();
	private Map<String, TreeSet<Fentrust>> entrustBuyMap = new HashMap<String, TreeSet<Fentrust>>();
	private Map<String, TreeSet<Fentrust>> entrustLimitBuyMap = new HashMap<String, TreeSet<Fentrust>>();
	// 委托卖出订单
	private Map<String, TreeSet<Fentrust>> sellDepthMap = new HashMap<String, TreeSet<Fentrust>>();
	private Map<String, TreeSet<Fentrust>> entrustSellMap = new HashMap<String, TreeSet<Fentrust>>();
	private Map<String, TreeSet<Fentrust>> entrustLimitSellMap = new HashMap<String, TreeSet<Fentrust>>();
	// 计划委托买入
	private Map<String, TreeSet<Fentrustplan>> entrustPlanBuyMap = new HashMap<String, TreeSet<Fentrustplan>>();
	// 计划委托卖出
	private Map<String, TreeSet<Fentrustplan>> entrustPlanSellMap = new HashMap<String, TreeSet<Fentrustplan>>();
	// 最新成交价
	private Map<String, Double> latestDealPrize = new HashMap<String, Double>();// 最新成交价

	private void changeRefreshBuyDepthData(String id, Boolean flag) {
		synchronized (refreshBuyDepthData) {
			refreshBuyDepthData.put(id, flag);
		}
	}

	private void changeRefreshSellDepthData(String id, Boolean flag) {
		synchronized (refreshSellDepthData) {
			refreshSellDepthData.put(id, flag);
		}
	}

	public void generateDepthData() {
		Object[] ids = refreshBuyDepthData.keySet().toArray();
		for (int i = 0; i < ids.length; i++) {
			/*
			 * Boolean value = refreshBuyDepthData.get(ids[i]) ; if(value!=null
			 * && value.booleanValue()==true){
			 * changeRefreshBuyDepthData((Integer)ids[i],false) ;
			 * 
			 * synchronized (buyDepthMap) { generateBuyDepth((Integer)ids[i]) ;
			 * }
			 * 
			 * }
			 */
			synchronized (buyDepthMap) {
				try {
					generateBuyDepth((String) ids[i]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		ids = refreshSellDepthData.keySet().toArray();
		for (int i = 0; i < ids.length; i++) {
			/*
			 * Boolean value = refreshSellDepthData.get(ids[i]) ; if(value!=null
			 * && value.booleanValue()==true){
			 * changeRefreshSellDepthData((Integer)ids[i],false) ;
			 * 
			 * synchronized (sellDepthMap) { generateSellDepth((Integer)ids[i])
			 * ; }
			 * 
			 * }
			 */
			synchronized (sellDepthMap) {
				try {
					generateSellDepth((String) ids[i]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void generateBuyDepth(String id) {
		
		TreeSet<Fentrust> fentrusts = new TreeSet<Fentrust>(prizeComparatorDESC);
		Object[] objs = this.getEntrustBuyMap(id).toArray();
		Map<String, KeyValues> map = new HashMap<String, KeyValues>();
		for (Object obj : objs) {
			if (obj == null) {
				continue;
			}
			Fentrust fentrust = (Fentrust) obj;
			String key = String.valueOf(fentrust.getFprize());
			KeyValues keyValues = map.get(key);
			if (keyValues == null) {
				keyValues = new KeyValues();
				keyValues.setKey(fentrust.getFprize());
				keyValues.setValue(fentrust.getFleftCount());
			} else {
				keyValues.setValue((Double) keyValues.getValue() + fentrust.getFleftCount());
			}
			map.put(key, keyValues);
		}

		for (Map.Entry<String, KeyValues> entry : map.entrySet()) {
			Fentrust fentrust = new Fentrust();
			fentrust.setFprize((Double) entry.getValue().getKey());
			fentrust.setFleftCount((Double) entry.getValue().getValue());
			fentrusts.add(fentrust);
		}
		if (Comm.getISREDIS()) {
			String json="";
			for (Fentrust f : fentrusts) {
				json="{\"fprize\": \""+f.getFprize()+"\",\"fleftCount\": \""+f.getFleftCount()+"\"}";
				//redisService.removeHashByitem(id+"buyDepthMap", String.valueOf(f.getFprize()));
				redisService.hset("buyDepthMap"+id, String.valueOf(f.getFprize()), json);
			}
		} else {
			this.buyDepthMap.put(id, fentrusts);
		}
	}

	private void generateSellDepth(String id) {
		TreeSet<Fentrust> fentrusts = new TreeSet<Fentrust>(prizeComparatorASC);

		Object[] objs = this.getEntrustSellMap(id).toArray();
		Map<String, KeyValues> map = new HashMap<String, KeyValues>();
		for (Object obj : objs) {
			if (obj == null) {
				continue;
			}
			Fentrust fentrust = (Fentrust) obj;
			String key = String.valueOf(fentrust.getFprize());
			KeyValues keyValues = map.get(key);
			if (keyValues == null) {
				keyValues = new KeyValues();
				keyValues.setKey(fentrust.getFprize());
				keyValues.setValue(fentrust.getFleftCount());
			} else {
				keyValues.setValue((Double) keyValues.getValue() + fentrust.getFleftCount());
			}
			map.put(key, keyValues);
		}

		for (Map.Entry<String, KeyValues> entry : map.entrySet()) {
			Fentrust fentrust = new Fentrust();
			fentrust.setFprize((Double) entry.getValue().getKey());
			fentrust.setFleftCount((Double) entry.getValue().getValue());
			fentrusts.add(fentrust);
		}
		if (Comm.getISREDIS()) {
			String json="";
			for (Fentrust f : fentrusts) {
				json="{\"fprize\": \""+f.getFprize()+"\",\"fleftCount\": \""+f.getFleftCount()+"\"}";
				//redisService.removeHashByitem(id+"sellDepthMap", String.valueOf(f.getFprize()));
				redisService.hset("sellDepthMap"+id, String.valueOf(f.getFprize()), json);
			}
		} else {
			this.sellDepthMap.put(id, fentrusts);
		}
	}

	public double getLatestDealPrize(String id) {
		Double prize = null;
		if (Comm.getISREDIS()) {
			String redisPrize = redisService.get("latestDealPrize"+id);
			if (redisPrize != null && !redisPrize.equals("")) {
				prize = Double.parseDouble(redisPrize);
			}
		} else {
			prize = latestDealPrize.get(id);
		}
		if (prize == null) {
			return this.virtualCoinService.findById(id).getFprice();
		} else {
			return prize;
		}
	}

	public double getLowestSellPrize(String id) {
		if (Comm.getISREDIS()) {
			Set<String> m = redisService.hkeys("sellDepthMap"+id);
			if (null!=m&&m.size()>0) {
				return Double.valueOf(Collections.min(m));
			}
			return this.getLatestDealPrize(id);
		}else{
			TreeSet<Fentrust> fentrusts = this.getEntrustSellMap(id);
			if (fentrusts == null || fentrusts.size() == 0) {
				return this.getLatestDealPrize(id);
			} else {
				return fentrusts.first().getFprize();
			}
		}
	}

	public double getHighestBuyPrize(String id) {
		if (Comm.getISREDIS()) {
			Set<String> m = redisService.hkeys("buyDepthMap"+id);
			if (null!=m&&m.size()>0) {
				String highestBuyPrize = Collections.max(m);
				return Double.valueOf(highestBuyPrize);
			}
			return this.getLatestDealPrize(id);
		}else{
			TreeSet<Fentrust> fentrusts = this.getEntrustBuyMap(id);
			if (fentrusts == null || fentrusts.size() == 0) {
				return this.getLatestDealPrize(id);
			} else {
				return fentrusts.first().getFprize();
			}
		}
	}

	public void init() {
		try {
			// List<Fentrust> f = (List<Fentrust>)
			// redisService.getList("402880c95a4ab0e3015a59e9a55142bdentrustBuy",
			// 0, 3, new Fentrust());
			// long startTime=System.currentTimeMillis();
			readData();
			// 执行方法
			// long endTime=System.currentTimeMillis();
			// float excTime=(float)(endTime-startTime)/1000;
			// System.out.println("++++");
			// System.out.println("RealTimeData 执行时间："+excTime+"s");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_is_init = true;
	}

	private void readData() {

		List<Fvirtualcointype> list = this.frontVirtualCoinService
				.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal);

		for (Fvirtualcointype fvirtualcointype : list) {

			// success log
			// 获取最近24小时的成交记录
			/*
			 * List<Fentrustlog> fentrustlogs =
			 * this.frontTradeService.findLatestSuccessDeal24(fvirtualcointype.
			 * getFid() , 24) ;
			 */

			// 获取最后N条成交记录

			List<Fentrustlog> fentrustlogs = this.frontTradeService.findLatestSuccessDeal(fvirtualcointype.getFid(),
					Comm.getMAX_NUMBERS());

			if (fentrustlogs != null) {
				/*if (Comm.getISREDIS()) {
					redisService.delList(fvirtualcointype.getFid(), 1, 0);
				}*/
				for (Fentrustlog fentrustlog : fentrustlogs) {
					this.addEntrustSuccessMap(fvirtualcointype.getFid(), fentrustlog);
				}
			}

			// 最新成交价
			Fentrustlog latestDeal = this.frontTradeService.findLatestDeal(fvirtualcointype.getFid());
			if (latestDeal != null) {
				if (!Comm.getISREDIS()) {
					latestDealPrize.put(fvirtualcointype.getFid(), latestDeal.getFprize());
				} else {
					redisService.saveKey("latestDealPrize"+fvirtualcointype.getFid(),
							Double.toString((latestDeal.getFprize())));
				}
			}

			// 委托买入
			List<Fentrust> fentrusts = this.frontTradeService.findAllGoingFentrust(fvirtualcointype.getFid(),
					EntrustTypeEnum.BUY, false);
			/*if (Comm.getISREDIS()) {
				redisService.delList(fvirtualcointype.getFid()+"entrustBuy", 1, 0);
			}*/ 
			if (fentrusts != null) {
				for (Fentrust fentrust : fentrusts) {
					this.addEntrustBuyMap(fvirtualcointype.getFid(), fentrust);
				}
			}
			// 委托买入
			fentrusts = this.frontTradeService.findAllGoingFentrust(fvirtualcointype.getFid(), EntrustTypeEnum.BUY,
					true);
			for (Fentrust fentrust : fentrusts) {
				this.addEntrustLimitBuyMap(fvirtualcointype.getFid(), fentrust);
			}
			// 委托卖出
			fentrusts = this.frontTradeService.findAllGoingFentrust(fvirtualcointype.getFid(), EntrustTypeEnum.SELL,
					false);
			/*if (Comm.getISREDIS()) {
				redisService.delList(fvirtualcointype.getFid()+"entrustSell", 1, 0);
			}*/ 
			if (fentrusts != null) {
				for (Fentrust f : fentrusts) {
					this.addEntrustSellMap(fvirtualcointype.getFid(), f);
				}
			}
			// 委托卖出
			fentrusts = this.frontTradeService.findAllGoingFentrust(fvirtualcointype.getFid(), EntrustTypeEnum.SELL,
					true);
			for (Fentrust fentrust : fentrusts) {
				this.addEntrustLimitSellMap(fvirtualcointype.getFid(), fentrust);
			}
			// 计划买入
			List<Fentrustplan> fentrustplans = this.frontTradeService.findEntrustPlan(EntrustPlanTypeEnum.BUY,
					new int[] { EntrustPlanStatusEnum.Not_entrust });
			for (Fentrustplan fentrustplan : fentrustplans) {
				this.addEntrustPlanBuyMap(fvirtualcointype.getFid(), fentrustplan);
			}
			// 计划卖出
			fentrustplans = this.frontTradeService.findEntrustPlan(EntrustPlanTypeEnum.SELL,
					new int[] { EntrustPlanStatusEnum.Not_entrust });
			for (Fentrustplan fentrustplan : fentrustplans) {
				this.addEntrustPlanSellMap(fvirtualcointype.getFid(), fentrustplan);
			}
		}
		;

	}

	public TreeSet<Fentrustlog> getEntrustSuccessMap(String id) {
		TreeSet<Fentrustlog> fentrusts = null;
		if (Comm.getISREDIS()) {
			fentrusts = new TreeSet<Fentrustlog>(this.timeComparator);
			List<Fentrustlog> listFent = (List<Fentrustlog>) redisService.getList(id, 0, 100, new Fentrustlog());
			for (Fentrustlog fent : listFent) {
				fentrusts.add(fent);
			}
		} else {
			fentrusts = entrustSuccessMap.get(id);
		}
		if (entrustSuccessMap == null) {
			fentrusts = new TreeSet<Fentrustlog>();
			return fentrusts;
		}
		if (fentrusts == null) {
			fentrusts = new TreeSet<Fentrustlog>();
		}
		return fentrusts;
	}

	public void addEntrustSuccessMap(String id, Fentrustlog fentrust) {
		synchronized (entrustSuccessMap) {
			if (fentrust.isIsactive() == false) {
				return;
			}
			if (Comm.getISREDIS()) {
				// redis
				redisService.saveSingleList(id, fentrust, false);
				// 更新最新成交价格到redis latestDealPrize
				redisService.saveKey("latestDealPrize"+id, Double.toString((fentrust.getFprize())));

			} else {
				TreeSet<Fentrustlog> fentrusts = entrustSuccessMap.get(id);
				if (fentrusts == null) {
					fentrusts = new TreeSet<Fentrustlog>(this.timeComparator);
				}
				if (fentrusts.contains(fentrust)) {
					fentrusts.remove(fentrust);
				}
				fentrusts.add(fentrust);

				entrustSuccessMap.put(id, fentrusts);

				// 更新最新成交价格
				latestDealPrize.put(id, fentrust.getFprize());

				this.latestKlinePeroid.pushFentrustlog(fentrust);
			}
			/*
			 * double prize = fentrust.getFprize() ; double count =
			 * fentrust.getFcount() ; Fperiod fperiod = new Fperiod() ;
			 * fperiod.setFdi(prize) ; fperiod.setFgao(prize) ;
			 * fperiod.setFkai(prize) ; fperiod.setFliang(count) ;
			 * fperiod.setFshou(prize) ; fperiod.setFtime(Utils.getTimestamp())
			 * ;
			 * 
			 * if(m_is_init){ this.klinePeriodData.setOneMiniteData(id, fperiod)
			 * ; }
			 */
		}
	}

	public void removeEntrustSuccessMap(String id, Fentrustlog fentrust) {
		synchronized (entrustSuccessMap) {
			if (Comm.getISREDIS()) {
				//TreeSet<Fentrustlog> fentrusts = new TreeSet<Fentrustlog>(this.timeComparator);
				List<Fentrustlog> listFent = (List<Fentrustlog>) redisService.getList(id, 0, -1, new Fentrustlog());
				for (Fentrustlog fen : listFent) {
					if(fen.getFid().equals(fentrust.getFid()))
					{
						listFent.remove(fen);
						redisService.srem(id, fen);
						//System.out.println(listFent.size());
						break;
					}
				}
				//redisService.saveList(id, listFent, true);

			} else {
				TreeSet<Fentrustlog> fentrusts = entrustSuccessMap.get(id);
				if (fentrusts != null) {
					fentrusts.remove(fentrust);
				}
			}
		}
	}

	public TreeSet<Fentrust> getEntrustBuyMap(String id) {
		TreeSet<Fentrust> fentrusts = null;
		if (Comm.getISREDIS()) {
			fentrusts = new TreeSet<Fentrust>(this.prizeComparatorDESC);
			Map<String, String> map=new HashMap<String,String>();
			try {
				//redisLock.RedisLockSet("entrustBuy"+id, 10000, 20000);
				//if (redisLock.lock()) {
				 map = redisService.getHashAll("entrustBuy"+id);
				//}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				//redisLock.unlock();
			}
			Fentrust fen = null;
			for (String key : map.keySet()) {
				JSONObject f = JSONObject.fromObject(map.get(key));
				fen = new Fentrust(f);
				fentrusts.add(fen);
			}
		} else {
			fentrusts = entrustBuyMap.get(id);
		if (entrustBuyMap == null) {
			fentrusts = new TreeSet<Fentrust>();
			return fentrusts;
		}
		if (fentrusts == null) {
			fentrusts = new TreeSet<Fentrust>();
		}
		}
		return fentrusts;
	}

	public TreeSet<Fentrust> getBuyDepthMap(String id) {
		TreeSet<Fentrust> fentrusts = null;
		if (buyDepthMap == null) {
			fentrusts = new TreeSet<Fentrust>();
			return fentrusts;
		}
		if (Comm.getISREDIS()) {
			fentrusts = new TreeSet<Fentrust>(this.prizeComparatorDESC);
			Map<String, String> map = redisService.getHashAll("buyDepthMap"+id);
			Fentrust fen = null;
			for (String key : map.keySet()) {
				JSONObject f = JSONObject.fromObject(map.get(key));
				fen = new Fentrust(f,null);
				fentrusts.add(fen);
			}
		} else{
			fentrusts = this.buyDepthMap.get(id);
			if (fentrusts == null) {
				fentrusts = new TreeSet<Fentrust>();
			}
		}
		return fentrusts;
	}

	public TreeSet<Fentrust> getEntrustLimitBuyMap(String id) {
		if (entrustLimitBuyMap == null || entrustLimitBuyMap.isEmpty()) {
			TreeSet<Fentrust> fentrusts = new TreeSet<Fentrust>();
			return fentrusts;
		}
		TreeSet<Fentrust> fentrusts = entrustLimitBuyMap.get(id);
		if (fentrusts == null) {
			fentrusts = new TreeSet<Fentrust>();
		}
		return fentrusts;
	}

	public String[] getEntrustBuyMapKeys() {
		Object[] objs = entrustBuyMap.keySet().toArray();
		String[] ints = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			ints[i] = (String) objs[i];
		}
		return ints;
	}

	public String[] getEntrustLimitBuyMapKeys() {
		Object[] objs = entrustLimitBuyMap.keySet().toArray();
		String[] ints = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			ints[i] = (String) objs[i];
		}
		return ints;
	}

	public void addEntrustBuyMap(String id, Fentrust fentrust) {
		synchronized (entrustBuyMap) {
			TreeSet<Fentrust> treeSet = null;
			if (Comm.getISREDIS()) {
				Fentrustbean fenb=new Fentrustbean(fentrust);
				try {
					//redisLock.RedisLockSet("entrustBuy"+fentrust.getFid(), 10000, 20000);
					//if (redisLock.lock()) {
					redisService.hset("entrustBuy"+id, fentrust.getFid(), fenb);
					//}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					//redisLock.unlock();
				}
				
				changeRefreshBuyDepthData(id, true);
			} else {
				treeSet = entrustBuyMap.get(id);
			if (treeSet == null) {
				treeSet = new TreeSet<Fentrust>(prizeComparatorDESC);
			}
			for (Fentrust fentrust2 : treeSet) {
				if(fentrust2.getFid().equals(fentrust.getFid())){
					treeSet.remove(fentrust2);
					break;
				}
			}
			treeSet.add(fentrust);
			entrustBuyMap.put(id, treeSet);
			changeRefreshBuyDepthData(id, true);
			}
			
	}
	}

	public void addEntrustLimitBuyMap(String id, Fentrust fentrust) {
		synchronized (entrustLimitBuyMap) {
			TreeSet<Fentrust> fentrusts = entrustLimitBuyMap.get(id);
			if (fentrusts == null) {
				fentrusts = new TreeSet<Fentrust>(prizeComparatorDESC);
			}
			if (fentrusts.contains(fentrust)) {
				fentrusts.remove(fentrust);
			}
			fentrusts.add(fentrust);

			entrustLimitBuyMap.put(id, fentrusts);
		}
	}

	public void removeEntrustBuyMap(String id, Fentrust fentrust) {
		synchronized (entrustBuyMap) {
			if (Comm.getISREDIS()) {
				redisService.removeHashByitem("entrustBuy"+id, fentrust.getFid());
				redisService.removeHashByitem("buyDepthMap"+id, String.valueOf(fentrust.getFprize()));
			}else{
				TreeSet<Fentrust> treeSet = entrustBuyMap.get(id);
				for (Fentrust fentrust2 : treeSet) {
					if(fentrust2.getFid().equals(fentrust.getFid())){
						treeSet.remove(fentrust2);
						break;
					}
				}
				if (treeSet != null) {
					entrustBuyMap.put(id, treeSet);
				}
			}
			changeRefreshBuyDepthData(id, true);
		}
	}

	public void removeEntrustLimitBuyMap(String id, Fentrust fentrust) {
		synchronized (entrustLimitBuyMap) {
			TreeSet<Fentrust> treeSet = entrustLimitBuyMap.get(id);
			if (treeSet != null) {
				treeSet.remove(fentrust);
				entrustLimitBuyMap.put(id, treeSet);
			}
		}
	}

	public TreeSet<Fentrust> getEntrustSellMap(String id) {
		TreeSet<Fentrust> fentrusts = null;
		if (Comm.getISREDIS()) {
			fentrusts = new TreeSet<Fentrust>(this.prizeComparatorASC);
			Map<String,String> map=redisService.getHashAll("entrustSell"+id);
			Fentrust fen = null;
			  for (String key : map.keySet()) {
				  JSONObject object = JSONObject.fromObject(map.get(key));
				  fen=new Fentrust(object);
				  fentrusts.add(fen);
				  }
		} else {
			fentrusts = entrustSellMap.get(id);
		if (fentrusts == null) {
			fentrusts = new TreeSet<Fentrust>();
		}
		}
		return fentrusts;
	}

	public TreeSet<Fentrust> getSellDepthMap(String id) {
		TreeSet<Fentrust> fentrusts = null;
		if (sellDepthMap == null) {
			fentrusts = new TreeSet<Fentrust>();
			return fentrusts;
		}
		if (Comm.getISREDIS()) {
			fentrusts = new TreeSet<Fentrust>(this.prizeComparatorASC);
			Map<String, String> map = redisService.getHashAll("sellDepthMap"+id);
			Fentrust fen = null;
			for (String key : map.keySet()) {
				JSONObject f = JSONObject.fromObject(map.get(key));
				fen = new Fentrust(f,null);
				fentrusts.add(fen);
			}
			/*fentrusts = new TreeSet<Fentrust>(this.prizeComparatorASC);
			List<Fentrustbean> f = (List<Fentrustbean>) redisService.getList(id+"sellDepthMap", 0, -1, new Fentrustbean());
			Fentrust fen = null;
			for (Fentrustbean fentrust : f) {
				fen = new Fentrust(fentrust);
				fentrusts.add(fen);
			}*/
		} else{
			fentrusts = this.sellDepthMap.get(id);
			if (fentrusts == null) {
				fentrusts = new TreeSet<Fentrust>();
			}
		}
		return fentrusts;
	}

	public TreeSet<Fentrust> getEntrustLimitSellMap(String id) {
		if (entrustLimitSellMap == null || entrustLimitSellMap.isEmpty()) {
			TreeSet<Fentrust> fentrusts = new TreeSet<Fentrust>();
			return fentrusts;
		}
		TreeSet<Fentrust> fentrusts = entrustLimitSellMap.get(id);
		if (fentrusts == null) {
			fentrusts = new TreeSet<Fentrust>();
		}
		return fentrusts;
	}

	public TreeSet<Fentrustplan> getEntrustPlanSellMap(int id) {
		TreeSet<Fentrustplan> fentrusts = entrustPlanSellMap.get(id);
		if (fentrusts == null) {
			fentrusts = new TreeSet<Fentrustplan>();
		}
		return fentrusts;
	}

	public TreeSet<Fentrustplan> getEntrustPlanBuyMap(int id) {
		TreeSet<Fentrustplan> fentrusts = entrustPlanBuyMap.get(id);
		if (fentrusts == null) {
			fentrusts = new TreeSet<Fentrustplan>();
		}
		return fentrusts;
	}

	public String[] getEntrustSellMapKeys() {
		Object[] objs = entrustSellMap.keySet().toArray();
		String[] ints = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			ints[i] = (String) objs[i];
		}
		return ints;
	}

	public String[] getEntrustLimitSellMapKeys() {
		Object[] objs = entrustLimitSellMap.keySet().toArray();
		String[] ints = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			ints[i] = (String) objs[i];
		}
		return ints;
	}

	public String[] getEntrustPlanSellMapKeys() {
		Object[] objs = entrustPlanSellMap.keySet().toArray();
		String[] ints = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			ints[i] = (String) objs[i];
		}
		return ints;
	}

	public String[] getEntrustPlanBuyMapKeys() {
		Object[] objs = entrustPlanBuyMap.keySet().toArray();
		String[] ints = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			ints[i] = (String) objs[i];
		}
		return ints;
	}

	public void addEntrustSellMap(String id, Fentrust fentrust) {
		synchronized (entrustSellMap) {
			TreeSet<Fentrust> fentrusts = null;
			if (Comm.getISREDIS()) {
				Fentrustbean fenb=new Fentrustbean(fentrust);
				redisService.hset("entrustSell"+id, fentrust.getFid(), fenb);
				changeRefreshSellDepthData(id, true);
			} else {
				fentrusts = entrustSellMap.get(id);
			
			if (fentrusts == null) {
				fentrusts = new TreeSet<Fentrust>(prizeComparatorASC);
			}
			for (Fentrust fentrust2 : fentrusts) {
				if(fentrust2.getFid().equals(fentrust.getFid())){
					fentrusts.remove(fentrust2);
					break;
				}
			}
			fentrusts.add(fentrust);
			entrustSellMap.put(id, fentrusts);
			changeRefreshSellDepthData(id, true);
			}
		}
	}

	public void addEntrustLimitSellMap(String id, Fentrust fentrust) {
		synchronized (entrustLimitSellMap) {
			TreeSet<Fentrust> fentrusts = entrustLimitSellMap.get(id);
			if (fentrusts == null) {
				fentrusts = new TreeSet<Fentrust>(prizeComparatorASC);
			}
			if (fentrusts.contains(fentrust)) {
				fentrusts.remove(fentrust);
			}
			fentrusts.add(fentrust);

			entrustLimitSellMap.put(id, fentrusts);
		}
	}

	public void addEntrustPlanSellMap(String id, Fentrustplan fentrust) {
		synchronized (entrustPlanSellMap) {
			TreeSet<Fentrustplan> fentrusts = entrustPlanSellMap.get(id);
			if (fentrusts == null) {
				fentrusts = new TreeSet<Fentrustplan>(planPrizeComparatorASC);
			}
			if (fentrusts.contains(fentrust)) {
				fentrusts.remove(fentrust);
			}
			fentrusts.add(fentrust);

			entrustPlanSellMap.put(id, fentrusts);
		}
	}

	public void addEntrustPlanBuyMap(String id, Fentrustplan fentrust) {
		synchronized (entrustPlanBuyMap) {
			TreeSet<Fentrustplan> fentrusts = entrustPlanBuyMap.get(id);
			if (fentrusts == null) {
				fentrusts = new TreeSet<Fentrustplan>(planPrizeComparatorDESC);
			}
			if (fentrusts.contains(fentrust)) {
				fentrusts.remove(fentrust);
			}
			fentrusts.add(fentrust);

			entrustPlanBuyMap.put(id, fentrusts);
		}
	}

	public void removeEntrustSellMap(String id, Fentrust fentrust) {
		synchronized (entrustSellMap) {
			if (Comm.getISREDIS()) {
				redisService.removeHashByitem("entrustSell"+id, fentrust.getFid());
				redisService.removeHashByitem("sellDepthMap"+id, String.valueOf(fentrust.getFprize()));
			}else{
				TreeSet<Fentrust> treeSet = entrustSellMap.get(id);
				for (Fentrust fentrust2 : treeSet) {
					if(fentrust2.getFid().equals(fentrust.getFid())){
						treeSet.remove(fentrust2);
						break;
					}
				}
				entrustSellMap.put(id, treeSet);
			}
			changeRefreshSellDepthData(id, true);
		}
	}

	public void removeEntrustLimitSellMap(String id, Fentrust fentrust) {
		synchronized (entrustLimitSellMap) {
			TreeSet<Fentrust> treeSet = entrustLimitSellMap.get(id);
			if (treeSet != null) {
				treeSet.remove(fentrust);
			}
			entrustLimitSellMap.put(id, treeSet);
		}
	}

	public void removeEntrustPlanSellMap(String id, Fentrustplan fentrust) {
		synchronized (entrustPlanSellMap) {
			TreeSet<Fentrustplan> treeSet = entrustPlanSellMap.get(id);
			if (treeSet != null) {
				treeSet.remove(fentrust);
			}
			entrustPlanSellMap.put(id, treeSet);
		}
	}

	public void removeEntrustPlanBuyMap(String id, Fentrustplan fentrust) {
		synchronized (entrustPlanBuyMap) {
			TreeSet<Fentrustplan> treeSet = entrustPlanBuyMap.get(id);
			if (treeSet != null) {
				treeSet.remove(fentrust);
			}
			entrustPlanBuyMap.put(id, treeSet);
		}
	}

	// 加密
	private static final String KEY_ALGORITHM = "AES";
	private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	private static Key toKey(byte[] key) throws Exception {
		return new SecretKeySpec(key, KEY_ALGORITHM);
	}

	private static String encrypt(String data, String key) throws Exception {
		Key k = toKey(Base64.decodeBase64(key.getBytes())); // 还原密钥
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM); // 实例化Cipher对象，它用于完成实际的加密操作
		cipher.init(Cipher.ENCRYPT_MODE, k); // 初始化Cipher对象，设置为加密模式
		return new String(Base64.encodeBase64(cipher.doFinal(data.getBytes()))); // 执行加密操作。加密后的结果通常都会用Base64编码进行传输
	}

	private static String decrypt(String data, String key) throws Exception {
		Key k = toKey(Base64.decodeBase64(key.getBytes()));
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k); // 初始化Cipher对象，设置为解密模式
		return new String(cipher.doFinal(Base64.decodeBase64(data.getBytes()))); // 执行解密操作
	}

	public void clear() {
		this.buyDepthMap = null;
		this.entrustBuyMap = null;
		this.entrustLimitBuyMap = null;
		this.entrustLimitSellMap = null;
		this.sellDepthMap = null;
		this.entrustPlanBuyMap = null;
		this.entrustSuccessMap = null;
	}
}
