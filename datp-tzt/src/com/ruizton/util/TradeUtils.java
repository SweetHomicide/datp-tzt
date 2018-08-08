package com.ruizton.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.auto.OneDayData;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Flimittrade;
import com.ruizton.main.model.Ftradehistory;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.EntrustService;
import com.ruizton.main.service.admin.LimittradeService;
import com.ruizton.main.service.admin.ScoreService;
import com.ruizton.main.service.admin.TradehistoryService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontTradeService;

public class TradeUtils {
	@Autowired
	private TradehistoryService tradehistoryService;
	@Autowired
	private RealTimeData realTimeData;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private LimittradeService limittradeService;
	@Autowired
	private ConstantMap map;
	@Autowired
	private ScoreService scoreService;
	@Autowired
	private OneDayData oneDayData;
	@Autowired
	private AdminService adminService;
	@Autowired
	private FrontTradeService frontTradeService;
	@Autowired
	private EntrustService entrustService;
	
	
	public void work() {
		List<Fvirtualcointype> all = this.virtualCoinService.findAll();
		for (Fvirtualcointype fvirtualcointype : all) {
			double price = Utils.getDouble(this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid()),fvirtualcointype.getFcount());
			Ftradehistory tradehistory = new Ftradehistory();
			tradehistory.setFdate(new Date());
			tradehistory.setFprice(price);
			tradehistory.setFtotal(this.oneDayData.get24Total(fvirtualcointype.getFid()));
			tradehistory.setFvid(fvirtualcointype.getFid());
			this.tradehistoryService.saveObj(tradehistory);
		}
		
		List<Flimittrade> trades = this.limittradeService.list(0, 0, "", false);
		for (Flimittrade flimittrade : trades) {
			Fvirtualcointype fvirtualcointype = this.virtualCoinService.findById(flimittrade.getFvirtualcointype().getFid());
			try {
				double price = Utils.getDouble(this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid()),fvirtualcointype.getFcount());
				flimittrade.setFdownprice(price);
				flimittrade.setFupprice(price);
				this.limittradeService.updateObj(flimittrade);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				//限价交易0点自动撤单
				String filter = "where (fstatus="+EntrustStatusEnum.Going+" or fstatus="+EntrustStatusEnum.PartDeal+") and fvirtualcointype.fid='"+fvirtualcointype.getFid()+"'";
				List<Fentrust> fentrust = this.entrustService.list(0, 0, filter, false);
				for (Fentrust fentrust2 : fentrust) {
					if(fentrust2.getFstatus()==EntrustStatusEnum.Going || fentrust2.getFstatus()==EntrustStatusEnum.PartDeal){
						boolean flag = false ;
						try {
							this.frontTradeService.updateCancelFentrust(fentrust2, fentrust2.getFuser()) ;
							flag = true ;
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(flag==true){
							if(fentrust2.getFentrustType()==EntrustTypeEnum.BUY){
								//买
								if(fentrust2.isFisLimit()){
									this.realTimeData.removeEntrustLimitBuyMap(fentrust2.getFvirtualcointype().getFid(), fentrust2) ;
								}else{
									this.realTimeData.removeEntrustBuyMap(fentrust2.getFvirtualcointype().getFid(), fentrust2) ;
								}
							}else{
								//卖
								if(fentrust2.isFisLimit()){
									this.realTimeData.removeEntrustLimitSellMap(fentrust2.getFvirtualcointype().getFid(), fentrust2) ;
								}else{
									this.realTimeData.removeEntrustSellMap(fentrust2.getFvirtualcointype().getFid(), fentrust2) ;
								}
								
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		//更新每天抽奖次数
//		try {
//			int times =0;
//			Flotteryrule rule = this.lotteryRuleService.findById(1);
//			times = rule.getFtimes();
//			StringBuffer sf = new StringBuffer();			
//			sf.append("update fscore set fkillQty= "+times+"\n");		
//			sf.append("where fid in( \n");
//			sf.append("select fscoreid from fuser where fisTelephoneBind=1 \n");
//			sf.append(" and fpostRealValidate=1 and fhasRealValidate=1 and  \n");
//			sf.append("(fid in( \n");
//			sf.append("select FUs_fId from fcapitaloperation where ftype=1 and fStatus=3 \n");
//			sf.append(") or fid in(SELECT fuser from fpaycode where fstatus=2) or fid in(SELECT fuid from foperationlog where fstatus=2)) \n");
//			sf.append(") \n");
//			this.scoreService.updateData(sf.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
		{
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String key = sdf.format(c.getTime());
			String xx = "where DATE_FORMAT(fdate,'%Y-%m-%d') ='"+key+"'";
			List<Ftradehistory> ftradehistorys = this.tradehistoryService.list(0, 0, xx, false);
			map.put("tradehistory", ftradehistorys);
		}
		
//		Map<String,Double> coinsTotalMap = new HashMap<String,Double>();
//		List<Ftradehistory> ftradehistory7D = new ArrayList<Ftradehistory>();
//		for (Fvirtualcointype fvirtualcointype : all) {
//			{
//				Calendar c = Calendar.getInstance();
//				c.setTime(new Date());
//				c.add(Calendar.DAY_OF_MONTH, -7);
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				String key = sdf.format(c.getTime());
//				String xx = "where DATE_FORMAT(fdate,'%Y-%m-%d') ='"+key+"' and fvid="+fvirtualcointype.getFid();
//				List<Ftradehistory> ftradehistorys = this.tradehistoryService.list(0, 1, xx, true);
//				if(ftradehistorys != null && ftradehistorys.size() >0){
//					ftradehistory7D.add(ftradehistorys.get(0));
//				}
//			}
////			{
////				Calendar c = Calendar.getInstance();
////				c.setTime(new Date());
////				c.add(Calendar.DAY_OF_MONTH, -15);
////				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
////				String key = sdf.format(c.getTime());
////				String xx = "select sum(ftotal) from Ftradehistory where DATE_FORMAT(fdate,'%Y-%m-%d') >='"+key+"' and fvid="+fvirtualcointype.getFid();
////				coinsTotalMap.put(fvirtualcointype.getfShortName(), this.adminService.getSQLValue(xx));
////			}
//		}
//		map.put("ftradehistory7D", ftradehistory7D);
//		map.put("coinsTotalMap", coinsTotalMap);
		
	}
}