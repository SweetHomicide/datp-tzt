package com.ruizton.main.auto;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.ditp.service.RedisService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Comm;

public class OneDayData {
	
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private RedisService redisService;

	private Map<String, Double> lowestPrize24 = new HashMap<String, Double>() ;
	private Map<String, Double> highestPrize24 = new HashMap<String, Double>() ;
	private Map<String, Double> totalDeal24 = new HashMap<String, Double>() ;
	private Map<String, Double> start24Price = new HashMap<String, Double>() ;
	private Map<String, Double> totalRMB24 = new HashMap<String, Double>() ;
	
	public double get24Price(String id) {
		Double f=null;
		if(Comm.getISREDIS())
		{
		String fStr=redisService.get("start24Price"+id);
		if(fStr!=null&!fStr.equals(""))
		{
		f=Double.parseDouble(fStr);
		}
		}else{
		 f = this.start24Price.get(id) ;
		}
		if(f==null){
			return 0F ;
		}else{
			return f ;
		}
	}
	
	public double get24Total(String id) {
		Double f=null;
		if(Comm.getISREDIS())
		{
		String fStr=redisService.get("totalRMB24"+id);
		if(fStr!=null&!fStr.equals(""))
		{
		f=Double.parseDouble(fStr);
		}
		}else{
		 f = this.totalRMB24.get(id) ;
		}
		if(f==null){
			return 0F ;
		}else{
			return f ;
		}
	}
	
	public double getLowest(String id){
		Double f = null;
		if (Comm.getISREDIS()) {
			String lowestPrize = redisService.get("lowestPrize24"+id);
			if (lowestPrize!=null&&!lowestPrize.equals("")) {
				f=Double.parseDouble(lowestPrize);
			} 
		} else {
			f=this.lowestPrize24.get(id) ;
		}
		if(f==null){
			return 0F ;
		}else{
			return f ;
		}
	}
	public double getHighest(String id){
		Double f = null;
		if (Comm.getISREDIS()) {
			String highestPrize = redisService.get("highestPrize24"+id);
			if (highestPrize!=null&&!highestPrize.equals("")) {
				f=Double.parseDouble(highestPrize);
			} 
		} else {
			f=this.highestPrize24.get(id) ;
		}
		
		if(f==null){
			return 0F ;
		}else{
			return f ;
		}
	}
	public double getTotal(String id){
		Double f=null;
		if(Comm.getISREDIS())
		{
		String fStr=redisService.get("totalDeal24"+id);
		if(fStr!=null&&!fStr.equals(""))
		{
		f=Double.parseDouble(fStr);
		}
		}else{
		 f = this.totalDeal24.get(id) ;
		}
		if(f==null){
			return 0F ;
		}else{
			return f ;
		}
	}
	
	public synchronized void putLowest(String id,double f){
		if (!Comm.getISREDIS()) {
			this.lowestPrize24.put(id, f) ;
		} else {
			redisService.saveKey("lowestPrize24"+id, Double.toString(f));
		}
	}
	
	public synchronized void putHighest(String id,double f){
		if (!Comm.getISREDIS()) {
			this.highestPrize24.put(id, f) ;
		} else {
			redisService.saveKey("highestPrize24"+id, Double.toString(f));
		}
	}
	
	public synchronized void putTotal(String id,double f){
		if(Comm.getISREDIS())
		{
			redisService.saveKey("totalDeal24"+id, Double.toString(f));
		}else{
		this.totalDeal24.put(id, f) ;
		}
	}
	public synchronized void put24Price(String id,double f){
		if(Comm.getISREDIS())
		{
			redisService.saveKey("start24Price"+id, Double.toString(f));
		}else{
		this.start24Price.put(id, f) ;
		}
	}
	public synchronized void put24Total(String id,double f){
		if(Comm.getISREDIS())
		{
			redisService.saveKey("totalRMB24"+id, Double.toString(f));
		}else{
		this.totalRMB24.put(id, f) ;
		}
	}
	
	
}
