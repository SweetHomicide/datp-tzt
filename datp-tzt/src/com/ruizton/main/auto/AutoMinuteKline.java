package com.ruizton.main.auto;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fperiod;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.front.FrontOthersService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.main.service.front.UtilsService;
import com.ruizton.util.Utils;

//one time per 1min
public class AutoMinuteKline extends TimerTask {

	@Autowired
	private KlinePeriodData klinePeriodData ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private FrontOthersService frontOthersService ;
	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private SystemArgsService systemArgsService ;
	@Autowired
	private UtilsService utilsService ;
	
	private boolean isFirstInit = true ;
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		synchronized (this) {
			
			try {
				if(KlinePeriodData.addFlags == false ){
					return ;
				}
				
				List<Fvirtualcointype> fvirtualcointypes = this.frontVirtualCoinService.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal) ;
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm") ;
				long now = simpleDateFormat.parse(simpleDateFormat.format(Utils.getTimestamp())).getTime() - 60*1000L ;
				long begin = now - 60*1000L ;
				long end = now - 1 ;
				
				//没打开交易，不用生成行情图
//				if(!openTrade(new Timestamp(now)) && !isFirstInit){
//					return ;
//				}
				isFirstInit = false ;
					
				for (int i = 0; i < fvirtualcointypes.size(); i++) {
					try{
						Fvirtualcointype fvirtualcointype = fvirtualcointypes.get(i) ;
						//直接从数据库算
						List<Fperiod> fperiods = getNewFperiod(fvirtualcointype) ;
						if(fperiods.size()>0){
							this.frontOthersService.addFperiods(fperiods) ;
							for (Fperiod fperiod : fperiods) {
								this.klinePeriodData.addFperiod(fvirtualcointypes.get(i).getFid(), 0, fperiod ) ;
							}
						}
						this.klinePeriodData.generateJson(fvirtualcointypes.get(i).getFid()) ;
						this.klinePeriodData.generateIndexJson(fvirtualcointypes.get(i).getFid()) ;
					}catch(Exception e){
						e.printStackTrace() ;
					}
				}
				this.klinePeriodData.lastUpdateTime = new Timestamp(now) ;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	
	public List<Fperiod> getNewFperiod(Fvirtualcointype fvirtualcointype) throws Exception {
		List<Fperiod> lastFperiods = utilsService.list(0, 1, " where fvirtualcointype.fid='"+fvirtualcointype.getFid()+"' order by ftime desc ", true, Fperiod.class) ;
		Fperiod lastFperiod = null;
		Timestamp lastTime = new Timestamp(Utils.getTimestamp().getTime()-60*1000L) ;
		if(lastFperiods.size() == 1 ){
			lastFperiod = lastFperiods.get(0)  ;
			lastTime = new Timestamp(lastFperiod.getFtime().getTime()+60*1000L) ;
		}
		
		Timestamp now = Utils.getTimestamp() ;
		List<Fentrustlog> fentrustlogs = new ArrayList<Fentrustlog>() ;
		if(lastFperiod == null ){
			fentrustlogs = this.utilsService.list(0, 0, " where fvirtualcointype.fid='"+fvirtualcointype.getFid()+"' and isactive=1 order by fcreateTime asc ", false, Fentrustlog.class) ;
		}else{
			fentrustlogs = this.utilsService.list(0, 0, " where fvirtualcointype.fid='"+fvirtualcointype.getFid()+"' and isactive=1 and fcreateTime>=?  order by fcreateTime asc ", false, Fentrustlog.class,lastTime) ;
		}
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm") ;
		TreeMap<String, Fperiod> map = new TreeMap<String, Fperiod>(new Comparator<String>() {

			public int compare(String o1, String o2) {
				try {
					return sdf.parse(o1).compareTo(sdf.parse(o2)) ;
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return 0 ;
			}
		}) ;
		while(true ){
			if(lastTime.getTime()<now.getTime()&&
					sdf.format(lastTime).equals(sdf.format(now))==false 
					){
				map.put(sdf.format(lastTime), null) ;
				lastTime = new Timestamp(lastTime.getTime()+60*1000L) ;
			}else{
				break;
			}
		}
		
		//计算高开低收量
		for (int i = 0; i < fentrustlogs.size(); i++) {
			Fentrustlog fentrustlog = fentrustlogs.get(i) ;
			Timestamp time = fentrustlog.getFcreateTime() ;
			double price = fentrustlog.getFprize() ;
			double count = fentrustlog.getFcount() ;
			
			String times = sdf.format(time) ;
			Fperiod fperiod = map.get(times) ;
			if(fperiod == null ){
				fperiod = new Fperiod() ;
				fperiod.setFdi(price) ;
				fperiod.setFgao(price) ;
				fperiod.setFkai(price) ;
				fperiod.setFliang(count) ;
				fperiod.setFshou(price) ;
				fperiod.setFtime(new Timestamp(sdf.parse(times).getTime())) ;
				fperiod.setFvirtualcointype(fvirtualcointype) ;
			}else{
				if(fperiod.getFdi()>price){
					fperiod.setFdi(price) ;
				}
				
				if(fperiod.getFgao()<price){
					fperiod.setFgao(price) ;
				}
				
				fperiod.setFliang(fperiod.getFliang()+count) ;
				fperiod.setFshou(price) ;
			}
			map.put(times, fperiod) ;
			
		}

		List<Fperiod> rets = new ArrayList<Fperiod>() ;
		//没有发生交易，用当前价格代替
		for (Map.Entry<String, Fperiod> entry : map.entrySet()) {
			String key = entry.getKey() ;
			Fperiod value = entry.getValue() ;
			if(value == null ){
				value = new Fperiod() ;
				double price = this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid())  ;
				value.setFdi(price) ;
				value.setFgao(price) ;
				value.setFkai(price) ;
				value.setFliang(0) ;
				value.setFshou(price) ;
				value.setFtime(new Timestamp(sdf.parse(key).getTime())) ;
				value.setFvirtualcointype(fvirtualcointype) ;
			}
			
			rets.add(value) ;
		}
		
		return rets ;
	}
	
	private boolean openTrade(Timestamp now){
		try {
			int nows = Integer.parseInt(new SimpleDateFormat("HH").format(now)) ;
			
			boolean flag = true ;
			String value = this.systemArgsService.getValue("openTrade") ;
			int min = Integer.parseInt(value.trim().split("-")[0]) ;
			int max = Integer.parseInt(value.trim().split("-")[1]) ;
			
			if(min == 0 && max == 24){
				return false;
			}
			if(min == 24 && max == 0){
				return true;
			}
			
			if(min<=max){
				if(nows>=min && nows<=max){
					flag = false ;
				}
			}
			
			if(max<min){
				if(!(nows>max && nows<min)){
					flag = false ;
				}
			}
			
			return flag ;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false ;
	}
	

	
	
	
	

}
