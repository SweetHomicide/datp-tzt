package com.ruizton.main.auto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.VirtualCapitalOperationTypeEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationInStatusEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.front.FrontVirtualCoinService;

public class RechargeBtcData {
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	
	//保存最后一个交易记录 virtualCoinId-->address
	private Map<Integer, String> tradeRecordMap = new HashMap<Integer, String>() ;
	public String getLastTradeRecordMap(String key){
		return this.tradeRecordMap.get(key) ;
	}
	public void setTradeRecordMap(Integer key,String value){
		synchronized (tradeRecordMap) {
			this.tradeRecordMap.put(key, value) ;
		}
	}
	
	private Map<String, Map<String, Fvirtualcaptualoperation>> map = new HashMap<String, Map<String,Fvirtualcaptualoperation>>() ;
	
	public String[] getKeys(){
		Object[] objs = map.keySet().toArray() ;
		String[] ints = new String[objs.length] ;
		for (int i = 0; i < objs.length; i++) {
			ints[i] = (String) objs[i] ;
		}
		return ints ;
	}
	
	public String[] getSubKeys(int id){
		Map<String, Fvirtualcaptualoperation> subMap = this.map.get(id) ;
		if(subMap==null){
			subMap = new HashMap<String, Fvirtualcaptualoperation>() ;
		}
		Object[] objs = subMap.keySet().toArray() ;
		String[] rets = new String[objs.length] ;
		for (int i = 0; i < objs.length; i++) {
			rets[i] = (String)objs[i] ;
		}
		return rets ;
	}
	
	public void put(String id,Map<String, Fvirtualcaptualoperation> subMap){
		synchronized (this) {
			this.map.put(id, subMap) ;
		}
	}
	
	public Fvirtualcaptualoperation subGet(int id,String key){
		Map<String, Fvirtualcaptualoperation> subMap = this.map.get(id) ;
		if(subMap!=null){
			return subMap.get(key) ;
		}
		
		return null ;
	}
	
	public void subPut(String id,String key,Fvirtualcaptualoperation Fvirtualcaptualoperation){
		synchronized (this) {
			Map<String, Fvirtualcaptualoperation> subMap = this.map.get(id) ;
			if(subMap==null){
				subMap = new HashMap<String, Fvirtualcaptualoperation>() ;
			}
			subMap.put(key, Fvirtualcaptualoperation) ;
			this.map.put(id, subMap) ;
		}
	}
	
	public void subRemove(String id,String key){
		synchronized (this) {
			Map<String, Fvirtualcaptualoperation> subMap = this.map.get(id) ;
			if(subMap!=null){
				subMap.remove(key) ;
				this.map.put(id, subMap) ;
			}
		}
	}
	
	
	public void init(){
		//long startTime=System.currentTimeMillis();
		readData() ;
		  //执行方法
//        long endTime=System.currentTimeMillis();
//       float excTime=(float)(endTime-startTime)/1000;
//       System.out.println("++++");
//       System.out.println("RechargeBtcData 执行时间："+excTime+"s");
	}
	private void readData(){
		List<Fvirtualcointype> fvirtualcointypes = this.frontVirtualCoinService.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal) ;
		for (Fvirtualcointype fvirtualcointype : fvirtualcointypes) {
			Map<String, Fvirtualcaptualoperation> tMap = new HashMap<String, Fvirtualcaptualoperation>() ;
			String filter = "where ftype="+VirtualCapitalOperationTypeEnum.COIN_IN+" and fvirtualcointype.fid='"+fvirtualcointype.getFid()
					+"' and fstatus in ("+VirtualCapitalOperationInStatusEnum.WAIT_0+","
					+VirtualCapitalOperationInStatusEnum.WAIT_1+","+
					VirtualCapitalOperationInStatusEnum.WAIT_2+")";
			List<Fvirtualcaptualoperation> fvirtualcaptualoperations = 
					this.frontVirtualCoinService.findFvirtualcaptualoperation(0, 0, filter, false);
					
			for (Fvirtualcaptualoperation fvirtualcaptualoperation : fvirtualcaptualoperations) {
				tMap.put(fvirtualcaptualoperation.getRecharge_virtual_address(), fvirtualcaptualoperation) ;
				System.out.print(fvirtualcaptualoperation.getRecharge_virtual_address());
			}
			this.put(fvirtualcointype.getFid(), tMap) ;
		}
	}
	
	public void clear(){
		this.map = null ;
	}
}
