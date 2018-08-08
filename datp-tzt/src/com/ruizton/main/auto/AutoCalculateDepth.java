package com.ruizton.main.auto;

import org.springframework.beans.factory.annotation.Autowired;

//市场深度合并
public class AutoCalculateDepth {

	@Autowired
	private RealTimeData realTimeData ;
	
	public void init() {
		new Thread(new Work()).start() ;
	}
	
	class Work implements Runnable{
		public void run() {
			while(true){
				try {
					realTimeData.generateDepthData() ;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try{
					Thread.sleep(20L) ;
				}catch(Exception e){
					e.printStackTrace() ;
				}
				
			}
		}
	}

}
