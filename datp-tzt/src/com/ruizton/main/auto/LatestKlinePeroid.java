package com.ruizton.main.auto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fperiod;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.front.FrontVirtualCoinService;

//K线一分钟更新一次，导致数据不及时。计算没有更新的数据，使K线实时显示。
public class LatestKlinePeroid {

	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	private Map<String, Fvirtualcointype> virs = new HashMap<String, Fvirtualcointype>() ;
	
	
	private LinkedList<Fentrustlog> fentrustlogs = new LinkedList<Fentrustlog>() ;
	
	private Map<String,Fperiod> map = new HashMap<String,Fperiod>() ;//key=id_time
	
	public void pushFentrustlog(Fentrustlog fentrustlog){
		synchronized (fentrustlogs) {
			this.fentrustlogs.add(fentrustlog) ;
		}
	}
	
	public Fentrustlog popFentrustlog(){
		synchronized (fentrustlogs) {
			if(fentrustlogs.size()==0){
				return null ;
			}
			return fentrustlogs.pop() ;
		}
	}
	
	
	public Timestamp parseTime(Timestamp timestamp) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm") ;
		return new Timestamp(sdf.parse(sdf.format(timestamp)).getTime()) ;
	}
	public String parseKey(String vid,Timestamp time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm") ;
		return vid+"_"+sdf.format(time) ;
	}
	
	public void init(){
		
		List<Fvirtualcointype> fvirtualcointypes = frontVirtualCoinService.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal) ;
		for (Fvirtualcointype fvirtualcointype : fvirtualcointypes) {
			virs.put(String.valueOf(fvirtualcointype.getFid()), fvirtualcointype) ;
		}
		
		Thread thread = new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				while(true){

					Fentrustlog fentrustlog = null ;
					while((fentrustlog=popFentrustlog())!=null){
						try {
							Fperiod fperiod = null ;
							String key = parseKey(fentrustlog.getFvirtualcointype().getFid(), fentrustlog.getFcreateTime()) ;
							fperiod = get(key) ;
							double price = fentrustlog.getFprize() ;
							if(fperiod!= null ){
								fperiod.setFdi(price<fperiod.getFdi()?price:fperiod.getFdi()) ;
								fperiod.setFgao(price>fperiod.getFgao()?price:fperiod.getFgao()) ;
								fperiod.setFshou(price) ;
								fperiod.setFliang(fperiod.getFliang()+fentrustlog.getFcount()) ;
							}else{
								fperiod = new Fperiod() ;
								fperiod.setFdi(price) ;
								fperiod.setFgao(price) ;
								fperiod.setFkai(price) ;
								fperiod.setFshou(price) ;
								fperiod.setFliang(fentrustlog.getFcount()) ;
								fperiod.setFtime(parseTime(fentrustlog.getFcreateTime())) ;
								fperiod.setFvirtualcointype(fentrustlog.getFvirtualcointype()) ;
							}
							put(key, fperiod) ;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					try {
						Thread.sleep(100L) ;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}) ;
		thread.setPriority(Thread.MAX_PRIORITY) ;
		thread.start() ;
	}
	
	private long lastPutTime = 0 ;
	public void put(String key,Fperiod fperiod){
		
		synchronized (this.map) {
			if(fperiod.getFtime().getTime()-lastPutTime>5*60*1000L){
				this.map.clear() ;
			}
			lastPutTime = fperiod.getFtime().getTime();
			this.map.put(key, fperiod) ;
		}
	}
	
	public Fperiod get(String key){
		synchronized (this.map) {
			return this.map.get(key) ;
		}
	}
}
