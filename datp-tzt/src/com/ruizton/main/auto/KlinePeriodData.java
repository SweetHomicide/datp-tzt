package com.ruizton.main.auto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.ditp.service.RedisService;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.model.Fperiod;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.front.FrontOthersService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

public class KlinePeriodData {
	public static boolean addFlags = false ;
	
	@Autowired
	private FrontOthersService frontOthersService ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private RedisService redisService;
	
	public static Timestamp lastUpdateTime = Utils.getTimestamp() ;
	
	private Map<String, String[]> json = new HashMap<String, String[]>() ;
	private Map<String, String[]> indexJson = new HashMap<String, String[]>() ;

	private Map<String, TreeSet<Fperiod>> oneMiniteData = new HashMap<String, TreeSet<Fperiod>>() ;
	
	private Map<String, Map<Integer, TreeSet<Fperiod>>> periodMap = new HashMap<String, Map<Integer,TreeSet<Fperiod>>>() ;
	private Map<String, Map<Integer, TreeSet<Fperiod>>> containerMap = new HashMap<String, Map<Integer,TreeSet<Fperiod>>>() ;
	private long[] timeStep = 
			new long[]{
			1,//1h
			3,//3d
			5,//5d
			15,
			30,
			1*60,//30d
			2*60,
			4*60,
			6*60,
			12*60,
			1*24*60,
			3*24*60,
			7*24*60,
	} ;
	
	Integer[] keys = null ;
	
	public void init(){
		readData() ;
	}
	
	
	
	private void readData(){

		keys = new Integer[13] ;
		for (int i = 0; i < 13; i++) {
			keys[i] = i ;
		}
		long now = Utils.getTimestamp().getTime() ;
		
		final List<Fvirtualcointype> fvirtualCoinTypes = this.frontVirtualCoinService.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal) ;
		for (int i = 0; i < fvirtualCoinTypes.size(); i++) {
			Fvirtualcointype fvirtualCoinType = fvirtualCoinTypes.get(i) ;
			
			Map<Integer, TreeSet<Fperiod>> subMap = new HashMap<Integer, TreeSet<Fperiod>>() ;
			for (int j = 0; j < keys.length; j++) {
				Integer key = keys[j] ;
				TreeSet<Fperiod> fperiodSet = new TreeSet<Fperiod>(this.comparator) ;
				subMap.put(key, fperiodSet) ;
			}
			periodMap.put(fvirtualCoinType.getFid(), subMap) ;
			
			Map<Integer, TreeSet<Fperiod>> subMap2 = new HashMap<Integer, TreeSet<Fperiod>>() ;
			for (int j = 0; j < keys.length; j++) {
				Integer key = keys[j] ;
				TreeSet<Fperiod> fperiodSet = new TreeSet<Fperiod>(this.comparator) ;
				subMap2.put(key, fperiodSet) ;
			}
			containerMap.put(fvirtualCoinType.getFid(), subMap2) ;
			
			//oneMiniteData
			TreeSet<Fperiod> fperiodSets2 = new TreeSet<Fperiod>(this.comparator) ;
			this.oneMiniteData.put(fvirtualCoinType.getFid(), fperiodSets2) ;
			
			//json
			String[] jsonStrings = new String[keys.length] ;
			json.put(fvirtualCoinType.getFid(), jsonStrings) ;
			//index json
			String[] indexJsonStrings = new String[keys.length] ;
			indexJson.put(fvirtualCoinType.getFid(), indexJsonStrings) ;
		}
		
		//read from database
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < fvirtualCoinTypes.size(); i++) {
					Fvirtualcointype fvirtualCoinType = fvirtualCoinTypes.get(i) ;
					//List<Fperiod> fperiods = this.frontOthersService.findAllFperiod(now-30 * 24 * 60 * 60 *1000L, fvirtualCoinType.getFid()) ;
					List<Fperiod> fperiods = frontOthersService.findAllFperiod(0L, fvirtualCoinType.getFid()) ;
					for (int j=0;j<fperiods.size();j++) {
						Fperiod fperiod = fperiods.get(j) ;
						addFperiod(fvirtualCoinType.getFid(),keys[0], fperiod) ;
					}
				}
				addFlags = true ;
			}
		}).start() ;

	}
	
	Timestamp startTime = null ; 
	public synchronized void addFperiod(String id,int key,Fperiod fperiod){
//		System.out.println(key+"==================");
			/*Map<Integer, TreeSet<Fperiod>> tmap = this.periodMap.get(id) ;
			TreeSet<Fperiod> fperiods = tmap.get(key) ;
			fperiods.add(fperiod) ;
			//每种数据的数量是60*24，超出部分剔除掉
			while(fperiods.size()>60*24){
				fperiods.remove(fperiods.first()) ;
			}
			
			tmap.put(key, fperiods) ;
			this.periodMap.put(id, tmap) ;
			
			//加入待计算容器
			if(key<keys.length-1){
				this.addFperiodContainer(id, key, fperiod) ;
			}*/
		
		
		
			//只接受KEY=1
		
			try {
				if(startTime == null ){//00:01
					startTime = new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(fperiod.getFtime())).getTime()) ;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("K线图启动失败，请重启！");
			}
			
			
			Map<Integer, TreeSet<Fperiod>> tmap = this.periodMap.get(id) ;
			
			for (int i = 0; i < this.timeStep.length; i++) {
				TreeSet<Fperiod> fperiods = null ;
				if(tmap != null){
					fperiods = tmap.get(i) ;
				}else{
					tmap = new HashMap<Integer,TreeSet<Fperiod>>(); 
				}
				
				long step = this.timeStep[i] ;

				Fperiod last = null ;
				if(fperiods != null && fperiods.size()>0){
					last = fperiods.last() ;
				}else{
					fperiods = new TreeSet<Fperiod>(this.comparator) ;
				}
				
				Timestamp openNew = isOpenNewPeriod(step, fperiod,last) ;
				
				
				Fperiod addPeriod = null ;
//				if(i==1 && openNew!=null){
//					System.out.println(openNew+"==================================");
//				}
				if(openNew!=null ){
					addPeriod = new Fperiod() ;
					addPeriod.setFtime(openNew) ;
					
					addPeriod.setFkai(fperiod.getFkai()) ;
					addPeriod.setFgao(fperiod.getFgao()) ;
					addPeriod.setFdi(fperiod.getFdi()) ;
					addPeriod.setFshou(fperiod.getFshou()) ;
					addPeriod.setFliang(fperiod.getFliang()) ;
				}else{
					addPeriod = last ;
					

					addPeriod.setFgao(fperiod.getFgao()>addPeriod.getFgao()?fperiod.getFgao():addPeriod.getFgao()) ;
					addPeriod.setFdi(fperiod.getFdi()>addPeriod.getFdi()?addPeriod.getFdi():fperiod.getFdi()) ;
					addPeriod.setFshou(fperiod.getFkai()) ;
					addPeriod.setFliang(addPeriod.getFliang()+fperiod.getFliang()) ;
				}
				
				fperiods.add(addPeriod) ;
				tmap.put(i, fperiods) ;
				
			}
			this.periodMap.put(id, tmap) ;
	}
	
	private Timestamp isOpenNewPeriod(long step,Fperiod fperiod,Fperiod last) {//返回下个阶段的时间戳
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm") ;
			Timestamp ftime = new Timestamp(sdf1.parse(sdf1.format(fperiod.getFtime())).getTime()) ;
			
			long minus = (ftime.getTime()-startTime.getTime())/(60*1000L) ;
			Timestamp nowTime = new Timestamp(startTime.getTime() + minus/step*step*60*1000L ) ;//新的时间
			
			if(last == null 
					|| (minus%step==0&&last.getFtime().getTime()!=nowTime.getTime()) 
					|| (minus%step!=0&&last.getFtime().getTime()!=nowTime.getTime()) ){
				return new Timestamp(startTime.getTime() + minus/step*step*60*1000L ) ;
			}else{
				return null ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	/*private synchronized void addFperiodContainer(int id,int key,Fperiod fperiod){
		Map<Integer, TreeSet<Fperiod>> tmap = this.containerMap.get(id) ;
		TreeSet<Fperiod> fperiods = tmap.get(key) ;
		fperiods.add(fperiod) ;
		if(key<keys.length-1){
			//不是最后一个
			long timestep1 = this.timeStep[key] ;
			long timestep2 = this.timeStep[key+1] ;
			long times = timestep2 / timestep1 ;
			if(fperiods.size()>=times){
				Fperiod calRet = this.calculate(fperiods) ;
				this.addFperiod(id, key+1, calRet) ;
				fperiods.clear() ;
			}
			
		}
		
		tmap.put(key, fperiods) ;
		this.containerMap.put(id, tmap) ;
	}*/
	
	/*private Fperiod calculate(TreeSet<Fperiod> fperiods){
			double fkai=0F;
			double fgao=0F;
			double fdi=0F;
			double fshou=0F;
			double fliang=0F;
			Timestamp ftime;
			Fperiod fperiod = new Fperiod() ;
			
			fkai = fperiods.first().getFkai() ;
			fshou = fperiods.last().getFshou() ;
			ftime = fperiods.first().getFtime() ;
			for (Fperiod f : fperiods) {
				fgao = fgao < f.getFgao()?f.getFgao():fgao ;
				if(fdi==0F){
					fdi = f.getFdi() ;
				}else{
					fdi = fdi > f.getFdi() ? f.getFdi() : fdi ;
				}
				fliang += f.getFliang() ;
			}
			
			fperiod.setFkai(fkai) ;
			fperiod.setFgao(fgao) ;
			fperiod.setFdi(fdi) ;
			fperiod.setFshou(fshou) ;
			fperiod.setFliang(fliang) ;
			fperiod.setFtime(ftime) ;
			
			return fperiod ;
	}*/
	
	private Comparator<Fperiod> comparator = new Comparator<Fperiod>() {

		public int compare(Fperiod o1, Fperiod o2) {
			return o1.getFtime().compareTo(o2.getFtime()) ;
		}
	};
	/*private Comparator comparator = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			Fperiod f1 = (Fperiod)o1;
			Fperiod f2 = (Fperiod)o1;
			// TODO Auto-generated method stub
			return f1.getFtime().compareTo(f2.getFtime());
		}
	};*/
	
	/*public void setOneMiniteData(int id,Fperiod fperiod){
		synchronized (oneMiniteData) {
			TreeSet<Fperiod> fperiods = this.oneMiniteData.get(id) ;
			fperiods.add(fperiod) ;
			this.oneMiniteData.put(id, fperiods) ;
		}
	}*/
	/*
	public Fperiod getOneMiniteData(Fvirtualcointype fvirtualcointype,long begin,long end){
		synchronized (oneMiniteData) {
			boolean needCreateNewPeriod = false ;
			int id = fvirtualcointype.getFid() ;
			Fperiod lastFperiod = this.frontOthersService.getLastFperiod(fvirtualcointype) ;
			if(lastFperiod!=null){
				long lastTime = lastFperiod.getFtime().getTime() ;
				if(lastTime<begin){
					//把漏掉的行情补上
					while(true){
						lastTime += 60*1000L ;
						if(lastTime<begin){
							Fperiod fperiod = new Fperiod() ;
							fperiod.setFgao(lastFperiod.getFshou()) ;
							fperiod.setFkai(lastFperiod.getFshou()) ;
							fperiod.setFdi(lastFperiod.getFshou()) ;
							fperiod.setFshou(lastFperiod.getFshou()) ;
							fperiod.setFliang(0D) ;
							fperiod.setFtime(new Timestamp(lastTime)) ;
							fperiod.setFvirtualcointype(fvirtualcointype) ;
							this.frontOthersService.addFperiod(fperiod) ;
							lastFperiod = fperiod ;
						}else{
							break ;
						}
					}
					
					needCreateNewPeriod = true ;
				}
			}else{
				needCreateNewPeriod = true ;
			}
			needCreateNewPeriod = true ;
			if(needCreateNewPeriod){
				//这一分钟的行情begin
				TreeSet<Fperiod> fperiods = this.oneMiniteData.get(id) ;
				
				TreeSet<Fperiod> usefulFperiods = new TreeSet<Fperiod>(this.comparator) ;
				List<Fperiod> remove = new ArrayList<Fperiod>() ;
				for (Fperiod fperiod : fperiods) {
					long time = fperiod.getFtime().getTime() ;
					if(time>=begin && time<=end){
						usefulFperiods.add(fperiod) ;
						remove.add(fperiod) ;
					}else{
						if(time<begin){
							remove.add(fperiod) ;
						}
					}
				}
				
				Fperiod fperiod = new Fperiod() ;
				double latest = 0D;
				if(lastFperiod==null){
					latest = this.realTimeData.getLatestDealPrize(id) ;
				}else{
					latest = lastFperiod.getFshou() ;
				}
				if(usefulFperiods.size()==0){
					
					fperiod.setFgao(latest) ;
					fperiod.setFkai(latest) ;
					fperiod.setFdi(latest) ;
					fperiod.setFshou(latest) ;
					fperiod.setFliang(0F) ;
					fperiod.setFtime(new Timestamp(begin)) ;
				}else{
					fperiod = this.calculate(usefulFperiods) ;
					fperiod.setFtime(new Timestamp(begin)) ;
				}
				
				for (Fperiod fperiod2 : remove) {
					fperiods.remove(fperiod2) ;
				}
				this.oneMiniteData.put(id, fperiods) ;
				return fperiod ;
				//这一分钟的行情end
			}else{
				return null ;
			}
			
		}
	}*/
	
	public String getJsonString(String id,int key){
			synchronized (this.json) {
				try {
					if(Comm.getISREDIS())
					{
						return redisService.get("Strjson"+id+key);
					}
					else{
					return json.get(id)[key] ;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return "[]";
				}
			}
	}
	
	public synchronized void setJsonString(String id,int key,String jsonString){
		synchronized (this.json) {
			if(Comm.getISREDIS())
			{
				redisService.saveKey("Strjson"+id+key, jsonString);
			}else{
			String[] strings = this.json.get(id) ;
			if(strings == null){
				strings = new String[this.keys.length] ;
			}
			strings[key] = jsonString ;
			json.put(id, strings) ;
			}
		}
		
	}
	
	public synchronized void generateJson(String id){
		Map<Integer,TreeSet<Fperiod>> map = this.periodMap.get(id) ;
		if(map == null){
			return;
		}
		for (Map.Entry<Integer, TreeSet<Fperiod>> entry : map.entrySet()) {
			TreeSet<Fperiod> fperiods = entry.getValue();
			StringBuffer stringBuffer = new StringBuffer() ;
			stringBuffer.append("[") ;
			int index = 0 ;
//			int before = fperiods.size() - 50 ;
//			before = before<0?0:before ;
			for (Fperiod fperiod : fperiods) {
				index++ ;
//				if(index<before){
//					continue ;
//				}
			//	stringBuffer.append("["+(fperiod.getFtime().getTime()/1000)+",0,0,"+fperiod.getFkai()+","+fperiod.getFshou()+","+fperiod.getFgao()+","+fperiod.getFdi()+","+fperiod.getFliang()+"]") ;
				stringBuffer.append("["+(fperiod.getFtime().getTime())
						+","+new BigDecimal(fperiod.getFkai()).setScale(6, BigDecimal.ROUND_HALF_UP)
						+","+new BigDecimal(fperiod.getFgao()).setScale(6, BigDecimal.ROUND_HALF_UP)
						+","+new BigDecimal(fperiod.getFdi()).setScale(6, BigDecimal.ROUND_HALF_UP)
						+","+new BigDecimal(fperiod.getFshou()).setScale(6, BigDecimal.ROUND_HALF_UP)
						+","+new BigDecimal(fperiod.getFliang()).setScale(4, BigDecimal.ROUND_HALF_UP)+"]") ;
				if(index!=fperiods.size()){
					stringBuffer.append(",") ;
				}
			}
			stringBuffer.append("]") ;
			this.setJsonString(id, entry.getKey(), stringBuffer.toString()) ;
		}
	}
	
	//index json
	public String getIndexJsonString(String id,int key){
		if(Comm.getISREDIS())
		{
			String jsonString=redisService.get("StrindexJson"+id+key);
			if(jsonString!=null)
			{
			return jsonString.toString();
			}else{
				return null;
			}
		}else{
		return indexJson.get(id)[key] ;
		}
	}
	

	public synchronized void setIndexJsonString(String id,int key,String indexJsonString){
		synchronized (this.indexJson) {
			if(Comm.getISREDIS())
			{
				redisService.saveKey("StrindexJson"+id+key, indexJsonString);
			}else{
				String[] strings = this.indexJson.get(id) ;
			if(strings == null){
				strings = new String[this.keys.length] ;
			}
        	strings[key] = indexJsonString ;
			
			indexJson.put(id, strings) ;
			}
		}
	}
	
	public synchronized void generateIndexJson(String id){
		Map<Integer,TreeSet<Fperiod>> map = this.periodMap.get(id) ;
		if(map == null){
			return;
		}
		for (Map.Entry<Integer, TreeSet<Fperiod>> entry : map.entrySet()) {
			TreeSet<Fperiod> fperiods = entry.getValue();
			StringBuffer stringBuffer = new StringBuffer() ;
			stringBuffer.append("[") ;
			int index = 0 ;
			int before = fperiods.size() - 50 ;
			before = before<0?0:before ;
			for (Fperiod fperiod : fperiods) {
				index++ ;
				if(index<before){
					continue ;
				}
				stringBuffer.append("["+(fperiod.getFtime().getTime()/1000)+",0,0,"+fperiod.getFkai()+","+fperiod.getFshou()+","+fperiod.getFgao()+","+fperiod.getFdi()+","+fperiod.getFliang()+"]") ;
				if(index!=fperiods.size()){
					stringBuffer.append(",") ;
				}
			}
			stringBuffer.append("]") ;
			this.setIndexJsonString(id, entry.getKey(), stringBuffer.toString()) ;
		}
		
	}
}
