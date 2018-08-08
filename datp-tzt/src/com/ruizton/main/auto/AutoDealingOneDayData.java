package com.ruizton.main.auto;

import java.util.List;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Utils;

public class AutoDealingOneDayData {

	@Autowired
	private OneDayData oneDayData;
	@Autowired
	private RealTimeData realTimeData;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService;

	public void init() {
		new Thread(new Work()).start();
	}

	class Work implements Runnable {

		public void run() {
			while (true) {
				try {
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					long time = Utils.getTimestamp().getTime();
					List<Fvirtualcointype> fvirtualcointypes = frontVirtualCoinService
							.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal);
					for (Fvirtualcointype fvirtualcointype : fvirtualcointypes) {
						double totalDeal = 0F;
						double lowest = 0F;
						double highest = 0F;
						double start24Price = 0F;
						double total24 = 0F;
						TreeSet<Fentrustlog> fentrusts = realTimeData
								.getEntrustSuccessMap(fvirtualcointype.getFid());
						Object[] objs = fentrusts.toArray();
						for (int i = 0; i < objs.length; i++) {
							Fentrustlog ent = (Fentrustlog) objs[i];
//							String date = sdf.format(ent.getFcreateTime());
//							String now = sdf.format(new Date());
							if (time - ent.getFcreateTime().getTime() > 24 * 60 * 60 * 1000L) {
								realTimeData.removeEntrustSuccessMap(
										fvirtualcointype.getFid(), ent);
							} else {
								if(i == objs.length-1){
									start24Price = ent.getFprize();
								}
								total24 = total24+ent.getFamount();
								totalDeal += ent.getFcount();
								double prize = ent.getFprize();

								lowest = (lowest > prize || lowest == 0F) ? prize
										: lowest;
								highest = (highest < prize) ? prize : highest;
							}

						}
						oneDayData.put24Total(fvirtualcointype.getFid(), total24);
						oneDayData.put24Price(fvirtualcointype.getFid(), start24Price);
						oneDayData.putHighest(fvirtualcointype.getFid(), highest);
						oneDayData.putLowest(fvirtualcointype.getFid(), lowest);
						oneDayData.putTotal(fvirtualcointype.getFid(), totalDeal);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
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
