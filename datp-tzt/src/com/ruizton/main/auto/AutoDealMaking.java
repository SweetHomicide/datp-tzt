package com.ruizton.main.auto;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Ffees;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.main.service.front.UtilsService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

//自动撮合
public class AutoDealMaking {
	private static final Logger log = LoggerFactory
			.getLogger(AutoDealMaking.class);
	@Autowired
	private RealTimeData realTimeData;
	@Autowired
	private FrontTradeService frontTradeService;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private UtilsService utilsService ;
	
	private HashMap fvirtualcointypeMap = new HashMap();
	
	public void init() {
		//读取手续费率
		List<Ffees> ffees = this.utilsService.list(0, 0, "", false, Ffees.class) ;
		for (Ffees fee : ffees) {
			/*rates.put(fee.getFvirtualcointype().getFid()+"_"+fee.getFlevel()+"_buy", fee.getFbuyfee()) ;
			rates.put(fee.getFvirtualcointype().getFid()+"_"+fee.getFlevel()+"_sell", fee.getFfee()) ;*/
			this.frontTradeService.putRates(fee.getFvirtualcointype().getFid()+"_buy", fee.getFbuyfee()) ;
			this.frontTradeService.putRates(fee.getFvirtualcointype().getFid()+"_sell", fee.getFfee()) ;
		}
		
		while(this.realTimeData.getMisInit() == false ){
			try {
				Thread.sleep(10L) ;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
				//e.printStackTrace();
			}
		}
		new Work().run() ;
	}

	class Work  {
		public void run() {


				try {
					String filter = "where fstatus=1 and fisShare=1";
					List<Fvirtualcointype> alls = virtualCoinService.list(0, 0, filter, false);
					// normal deal making
					if (alls != null && alls.size() >0) {
						for (final Fvirtualcointype fvirtualcointype : alls) {
							autoDealMakingThread(fvirtualcointype);
						}
					}

				} catch (Exception e) {
					// e.printStackTrace() ;
					log.error(e.getMessage());
				}
				
		}
		
		
	}
	
	public void autoDealMakingThread(final Fvirtualcointype fvirtualcointype){
		try {
			if(fvirtualcointypeMap.containsKey(fvirtualcointype.getFid())){
				return;
			}
			Thread thread = new Thread(new Runnable() {
				public void run() {
					// TODO Auto-generated method stub
					while(true ){
						try {
							try {
								limitBuyDealMaking(fvirtualcointype.getFid()) ;
							} catch (Exception e) {
								// TODO Auto-generated catch block
//								e.printStackTrace();
								log.error(e.getMessage());
							}
							try {
								limitSellDealMaking(fvirtualcointype.getFid()) ;
							} catch (Exception e) {
								// TODO Auto-generated catch block
//								e.printStackTrace();
								log.error(e.getMessage());
							}
							try {
								dealMaking(fvirtualcointype.getFid());
							} catch (Exception e) {
								// TODO Auto-generated catch block
//								e.printStackTrace();
								log.error(e.getMessage());
							}
							
							try {
								limitBuySellDealMaking(fvirtualcointype.getFid());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								log.error(e.getMessage());
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							log.error(e.getMessage());
						}
						
						try {
							Thread.sleep(1L) ;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							log.error(e.getMessage());
						}
					}
					
				}
			}) ;
			thread.setPriority(Thread.MAX_PRIORITY) ;
			thread.start() ;
			fvirtualcointypeMap.put(fvirtualcointype.getFid(), "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			log.error(e.getMessage());
		}
	}

		private void limitSellDealMaking(String id) {

			boolean rehandle = false;
			Fvirtualcointype fvirtualcointype = frontVirtualCoinService
					.findFvirtualCoinById(id);

			Object[] sellLimitFentrusts = realTimeData
					.getEntrustLimitSellMap(id).toArray();
			Object[] buyFentrusts = realTimeData.getEntrustBuyMap(id)
					.toArray();
			// log.error(buyFentrusts.size()+"=xxxxxxxxxxxx="+sellFentrusts.size())
			// ;
			if (sellLimitFentrusts.length > 0 && buyFentrusts.length > 0) {

				first: for (int i = 0; i < sellLimitFentrusts.length; i++) {
					Fentrust sell = (Fentrust) sellLimitFentrusts[i];

					if (sell.getFstatus() == EntrustStatusEnum.AllDeal
							|| sell.getFstatus() == EntrustStatusEnum.Cancel) {
						realTimeData.removeEntrustLimitSellMap(id, sell);
						continue;
					}

					for (int j = 0; j < buyFentrusts.length; j++) {
						Fentrust buy = (Fentrust) buyFentrusts[j];

						if (buy.getFstatus() == EntrustStatusEnum.AllDeal
								|| buy.getFstatus() == EntrustStatusEnum.Cancel) {
							realTimeData.removeEntrustBuyMap(id, buy);
							continue;
						}

						if (buy.getFuser().getFid().equals(sell.getFuser().getFid())
								&& !Constant.TradeSelf) {
							continue;
						}

						// begin
						boolean isbuy =false;
						boolean issell =false;
						if(Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(buy.getFcreateTime())) > Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(sell.getFcreateTime()))){
							isbuy = true;
						}else if(Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(buy.getFcreateTime())) < Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(sell.getFcreateTime()))){
							issell = true;
						}else{
							issell = true;
						}

						double sellPrize = buy.getFprize();
						double sellCount = buy.getFleftCount();
						sellCount = sellCount > sell.getFleftCount() ? sell
								.getFleftCount() : sellCount;

						Fentrustlog buyFentrustlog = new Fentrustlog();
						buyFentrustlog.setFamount(sellCount * sellPrize);
						buyFentrustlog.setFcount(sellCount);
						buyFentrustlog.setFcreateTime(Utils.getTimestamp());
						buyFentrustlog.setFprize(sellPrize);
						buyFentrustlog.setIsactive(isbuy);
						buyFentrustlog.setFentrust(buy);
						buyFentrustlog.setfEntrustType(EntrustTypeEnum.BUY);
						buyFentrustlog.setFvirtualcointype(fvirtualcointype);

						Fentrustlog sellFentrustlog = new Fentrustlog();
						sellFentrustlog.setFamount(sellCount * sellPrize);
						sellFentrustlog.setFcount(sellCount);
						sellFentrustlog.setFcreateTime(Utils.getTimestamp());
						sellFentrustlog.setFprize(sellPrize);
						sellFentrustlog.setIsactive(issell);
						sellFentrustlog.setFentrust(sell);
						sellFentrustlog.setfEntrustType(EntrustTypeEnum.SELL);
						sellFentrustlog.setFvirtualcointype(fvirtualcointype);

						boolean ret = false;
						try {
							frontTradeService.updateDealMaking(buy, sell,
									buyFentrustlog, sellFentrustlog, id);
							ret = true;
						} catch (Exception e) {
							log.error(e.getMessage());
							// e.printStackTrace();
						}

						if (ret) {
							// 加入成功交易
							realTimeData.addEntrustSuccessMap(id,
									sellFentrustlog);
							// 加入成功交易
							realTimeData.addEntrustSuccessMap(id,
									buyFentrustlog);
							

							if (buy.getFstatus() == EntrustStatusEnum.Going
									|| buy.getFstatus() == EntrustStatusEnum.PartDeal) {
								if (buy.isFisLimit()) {
									realTimeData.addEntrustLimitBuyMap(id,
											buy);
								} else {
									realTimeData.addEntrustBuyMap(id, buy);
								}
							} else if (buy.getFstatus() == EntrustStatusEnum.AllDeal) {
								if (buy.isFisLimit()) {
									realTimeData.removeEntrustLimitBuyMap(
											id, buy);
								} else {
									realTimeData.removeEntrustBuyMap(id,
											buy);
								}

							}

							if (sell.getFstatus() == EntrustStatusEnum.Going
									|| sell.getFstatus() == EntrustStatusEnum.PartDeal) {
								if (sell.isFisLimit()) {
									realTimeData.addEntrustLimitSellMap(
											id, sell);
								} else {
									realTimeData.addEntrustSellMap(id,
											sell);
								}
							} else if (sell.getFstatus() == EntrustStatusEnum.AllDeal) {
								if (sell.isFisLimit()) {
									realTimeData
											.removeEntrustLimitSellMap(id, sell);
								} else {
									realTimeData.removeEntrustSellMap(id,
											sell);
								}

							}

							rehandle = true;
							
						} else {
							buy = frontTradeService.findFentrustById(buy
									.getFid());
							sell = frontTradeService.findFentrustById(sell
									.getFid());

							if (buy == null || sell == null) {
								log.error("buy or sell null;");
								continue;
							}

							if (buy.getFstatus() == EntrustStatusEnum.Going
									|| buy.getFstatus() == EntrustStatusEnum.PartDeal) {
								realTimeData.addEntrustBuyMap(id, buy);
							} else {
								realTimeData.removeEntrustBuyMap(id, buy);
							}
							if (sell.getFstatus() == EntrustStatusEnum.Going
									|| sell.getFstatus() == EntrustStatusEnum.PartDeal) {
								realTimeData.addEntrustLimitSellMap(id,
										sell);
							} else {
								realTimeData.removeEntrustLimitSellMap(id,
										sell);
							}
						}

						rehandle = true;

						break first;
						// end
					}
				}

				if (rehandle) {
					limitSellDealMaking(id);
				}
			}

		}

		private void limitBuyDealMaking(String id) {

			boolean rehandle = false;
			Fvirtualcointype fvirtualcointype = frontVirtualCoinService
					.findFvirtualCoinById(id);
			Object[] buyLimitFentrusts = realTimeData
					.getEntrustLimitBuyMap(id).toArray();
			Object[] sellFentrusts = realTimeData.getEntrustSellMap(id)
					.toArray();
			if (buyLimitFentrusts.length > 0 && sellFentrusts.length > 0) {

				first: for (int i = 0; i < buyLimitFentrusts.length; i++) {
					Fentrust buy = (Fentrust) buyLimitFentrusts[i];

					if (buy.getFstatus() == EntrustStatusEnum.AllDeal
							|| buy.getFstatus() == EntrustStatusEnum.Cancel) {
						realTimeData.removeEntrustLimitBuyMap(id, buy);
						continue;
					}

					for (int j = 0; j < sellFentrusts.length; j++) {
						Fentrust sell = (Fentrust) sellFentrusts[j];

						if (sell.getFstatus() == EntrustStatusEnum.AllDeal
								|| sell.getFstatus() == EntrustStatusEnum.Cancel) {
							realTimeData.removeEntrustSellMap(id, sell);
							continue;
						}

						if (buy.getFuser().getFid().equals(sell.getFuser().getFid())
								&& !Constant.TradeSelf) {
							continue;
						}

						// begin
						boolean isbuy =false;
						boolean issell =false;
						if(Integer.valueOf(buy.getFcreateTime().toString()) > Integer.valueOf(sell.getFcreateTime().toString())){
							isbuy = true;
						}else if(Integer.valueOf(buy.getFcreateTime().toString()) < Integer.valueOf(sell.getFcreateTime().toString())){
							issell = true;
						}else{
							issell = true;
						}

						double buyPrize = sell.getFprize();
						double buyCount = (buy.getFamount() - buy
								.getFsuccessAmount()) / buyPrize;
						buyCount = buyCount > sell.getFleftCount() ? sell
								.getFleftCount() : buyCount;

						Fentrustlog buyFentrustlog = new Fentrustlog();
						buyFentrustlog.setFamount(buyCount * buyPrize);
						buyFentrustlog.setFcount(buyCount);
						buyFentrustlog.setFcreateTime(Utils.getTimestamp());
						buyFentrustlog.setFprize(buyPrize);
						buyFentrustlog.setIsactive(isbuy);
						buyFentrustlog.setFentrust(buy);
						buyFentrustlog.setfEntrustType(EntrustTypeEnum.BUY);
						buyFentrustlog.setFvirtualcointype(fvirtualcointype);

						Fentrustlog sellFentrustlog = new Fentrustlog();
						sellFentrustlog.setFamount(buyCount * buyPrize);
						sellFentrustlog.setFcount(buyCount);
						sellFentrustlog.setFcreateTime(Utils.getTimestamp());
						sellFentrustlog.setFprize(buyPrize);
						sellFentrustlog.setIsactive(issell);
						sellFentrustlog.setFentrust(sell);
						sellFentrustlog.setfEntrustType(EntrustTypeEnum.SELL);
						sellFentrustlog.setFvirtualcointype(fvirtualcointype);

						boolean ret = false;
						try {
							frontTradeService.updateDealMaking(buy, sell,
									buyFentrustlog, sellFentrustlog, id);
							ret = true;
						} catch (Exception e) {
							// e.printStackTrace();
							log.error(e.getMessage());
						}

						if (ret) {
							// 加入成功交易
							realTimeData.addEntrustSuccessMap(id,
									sellFentrustlog);
							// 加入成功交易
							realTimeData.addEntrustSuccessMap(id,
									buyFentrustlog);
							

							if (buy.getFstatus() == EntrustStatusEnum.Going
									|| buy.getFstatus() == EntrustStatusEnum.PartDeal) {
								if (buy.isFisLimit()) {
									realTimeData.addEntrustLimitBuyMap(id,
											buy);
								} else {
									realTimeData.addEntrustBuyMap(id, buy);
								}
							} else if (buy.getFstatus() == EntrustStatusEnum.AllDeal) {
								if (buy.isFisLimit()) {
									realTimeData.removeEntrustLimitBuyMap(
											id, buy);
								} else {
									realTimeData.removeEntrustBuyMap(id,
											buy);
								}

							}

							if (sell.getFstatus() == EntrustStatusEnum.Going
									|| sell.getFstatus() == EntrustStatusEnum.PartDeal) {
								if (sell.isFisLimit()) {
									realTimeData.addEntrustLimitSellMap(
											id, sell);
								} else {
									realTimeData.addEntrustSellMap(id,
											sell);
								}
							} else if (sell.getFstatus() == EntrustStatusEnum.AllDeal) {
								if (sell.isFisLimit()) {
									realTimeData
											.removeEntrustLimitSellMap(id, sell);
								} else {
									realTimeData.removeEntrustSellMap(id,
											sell);
								}

							}

							rehandle = true;
							
						} else {
							buy = frontTradeService.findFentrustById(buy
									.getFid());
							sell = frontTradeService.findFentrustById(sell
									.getFid());

							if (buy == null || sell == null) {
								log.error("buy or sell null;");
								continue;
							}

							if (buy.getFstatus() == EntrustStatusEnum.Going
									|| buy.getFstatus() == EntrustStatusEnum.PartDeal) {
								realTimeData
										.addEntrustLimitBuyMap(id, buy);
							} else {
								realTimeData.removeEntrustLimitBuyMap(id,
										buy);
							}
							if (sell.getFstatus() == EntrustStatusEnum.Going
									|| sell.getFstatus() == EntrustStatusEnum.PartDeal) {
								realTimeData.addEntrustSellMap(id, sell);
							} else {
								realTimeData
										.removeEntrustSellMap(id, sell);
							}
						}

						rehandle = true;

						break first;
						// end
					}
				}

				if (rehandle) {
					limitBuyDealMaking(id);
				}
			}

		}

		private void dealMaking(String id) {
			boolean rehandle = false;

			Fvirtualcointype fvirtualcointype = frontVirtualCoinService
					.findFvirtualCoinById(id);
			Object[] buyFentrusts = realTimeData.getEntrustBuyMap(id)
					.toArray();
			Object[] sellFentrusts = realTimeData.getEntrustSellMap(id)
					.toArray();
//			System.out.print(".......... \n");
			if (buyFentrusts.length > 0 && sellFentrusts.length > 0) {

				first: for (int i = 0; i < buyFentrusts.length; i++) {
					Fentrust buy = (Fentrust) buyFentrusts[i];

					if (buy.getFstatus() == EntrustStatusEnum.AllDeal
							|| buy.getFstatus() == EntrustStatusEnum.Cancel) {
						realTimeData.removeEntrustBuyMap(id, buy);
						continue;
					}

					second: for (int j = 0; j < sellFentrusts.length; j++) {
						Fentrust sell = (Fentrust) sellFentrusts[j];

						if (sell.getFstatus() == EntrustStatusEnum.AllDeal
								|| sell.getFstatus() == EntrustStatusEnum.Cancel) {
							realTimeData.removeEntrustSellMap(id, sell);
							continue;
						}
						if (null!=buy.getFuser()&&null!=sell.getFuser()) {
							if (buy.getFuser().getFid().equals(sell.getFuser().getFid())
									&& !Constant.TradeSelf&&!Comm.getISDEAL_OWNBYOWN()) {
								continue;// 自己的订单，常量判断是不是自己可以和自己交易
							}
						}

						if (buy.getFprize() < sell.getFprize()) {
							continue;
						}

						/*********************************************************/
						/*********************************************************/
						// 解决当卖单进入时本可以成交，但是此时又进入卖单，导致卖单以低价成交的bug
						/*
						 * 当买单的时间比卖单新时，需要判断是否有比卖单旧的价格可以成交，
						 * 当买单比卖单旧时，需要判断是否有比买单旧的价格可以成交
						 */
						/*Timestamp buyTime = buy.getFcreateTime();
						Timestamp sellTime = sell.getFcreateTime();
						if (buyTime.getTime() >= sellTime.getTime()) {
							// 当买单的时间比卖单新时，需要判断是否有比卖单旧的价格可以成交，
							boolean hasOld = false;
							for (int x = i + 1; x < buyFentrusts.length; x++) {
								if (hasOld == true)
									continue;

								Fentrust tmp = (Fentrust) buyFentrusts[x];
								if (tmp.getFprize() >= sell.getFprize()
										&& tmp.getFcreateTime().getTime() < sell
												.getFcreateTime().getTime()) {
									hasOld = true;
								}
							}
							if (hasOld == true) {
								break second;
							}

						} else {
							// 当买单比卖单旧时，需要判断是否有比买单旧的价格可以成交
							boolean hasNew = false;
							for (int x = j + 1; x < sellFentrusts.length; x++) {
								if (hasNew == true)
									continue;

								Fentrust tmp = (Fentrust) sellFentrusts[x];
								if (buy.getFprize() >= tmp.getFprize()
										&& tmp.getFcreateTime().getTime() < buy
												.getFcreateTime().getTime()) {
									hasNew = true;
								}
							}
							if (hasNew == true) {
								continue second;
							}
						}*/
						/*********************************************************/
						/*********************************************************/

						// begin

						double buyCount = buy.getFleftCount();
						buyCount = buyCount > sell.getFleftCount() ? sell
								.getFleftCount() : buyCount;
						double buyPrize = 0d;
						if (Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(buy.getFcreateTime())) < Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(sell.getFcreateTime()))) {
							buyPrize = buy.getFprize();
						} else {
							buyPrize = sell.getFprize();
						}
						// buyPrize = buy.getFprize() ;

						// protect
						if (buyCount > buy.getFleftCount()
								|| buyCount > sell.getFleftCount()
								|| buyPrize > buy.getFprize()) {
							log.error("dealmaking error!");
							return;
						}
						
						boolean isbuy =false;
						boolean issell =false;
						if(Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(buy.getFcreateTime())) > Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(sell.getFcreateTime()))){
							isbuy = true;
						}else if(Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(buy.getFcreateTime())) < Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(sell.getFcreateTime()))){
							issell = true;
						}else{
							issell = true;
						}
						
						Fentrustlog buyFentrustlog = new Fentrustlog();
						
						buyFentrustlog.setFamount(buyCount * buyPrize);
						buyFentrustlog.setFcount(buyCount);
						buyFentrustlog.setFcreateTime(Utils.getTimestamp());
						buyFentrustlog.setFprize(buyPrize);
						buyFentrustlog.setIsactive(isbuy);
						buyFentrustlog.setFentrust(buy);
						buyFentrustlog.setfEntrustType(EntrustTypeEnum.BUY);
						buyFentrustlog.setFvirtualcointype(fvirtualcointype);

						Fentrustlog sellFentrustlog = new Fentrustlog();
						sellFentrustlog.setFamount(buyCount * buyPrize);
						sellFentrustlog.setFcount(buyCount);
						sellFentrustlog.setFcreateTime(Utils.getTimestamp());
						sellFentrustlog.setFprize(buyPrize);
						sellFentrustlog.setIsactive(issell);
						sellFentrustlog.setFentrust(sell);
						sellFentrustlog.setfEntrustType(EntrustTypeEnum.SELL);
						sellFentrustlog.setFvirtualcointype(fvirtualcointype);

						boolean ret = false;
						try {
							frontTradeService.updateDealMaking(buy, sell,
									buyFentrustlog, sellFentrustlog, id);
							ret = true;
						} catch (Exception e) {
							// e.printStackTrace();
							log.error(e.getMessage());
						}

						if (ret) {
							// 加入成功交易
							realTimeData.addEntrustSuccessMap(id,
									sellFentrustlog);
							// 加入成功交易
							realTimeData.addEntrustSuccessMap(id,
									buyFentrustlog);
							
							if (buy.getFstatus() == EntrustStatusEnum.Going
									|| buy.getFstatus() == EntrustStatusEnum.PartDeal) {
								if (buy.isFisLimit()) {
									realTimeData.addEntrustLimitBuyMap(id,
											buy);
								} else {
									realTimeData.addEntrustBuyMap(id, buy);
								}
							} else if (buy.getFstatus() == EntrustStatusEnum.AllDeal) {
								if (buy.isFisLimit()) {
									realTimeData.removeEntrustLimitBuyMap(
											id, buy);
								} else {
									realTimeData.removeEntrustBuyMap(id,
											buy);
								}

							}

							if (sell.getFstatus() == EntrustStatusEnum.Going
									|| sell.getFstatus() == EntrustStatusEnum.PartDeal) {
								if (sell.isFisLimit()) {
									realTimeData.addEntrustLimitSellMap(
											id, sell);
								} else {
									realTimeData.addEntrustSellMap(id,
											sell);
								}
							} else if (sell.getFstatus() == EntrustStatusEnum.AllDeal) {
								if (sell.isFisLimit()) {
									realTimeData
											.removeEntrustLimitSellMap(id, sell);
								} else {
									realTimeData.removeEntrustSellMap(id,
											sell);
								}

							}

							rehandle = true;
						} else {
							buy = frontTradeService.findFentrustById(buy
									.getFid());
							sell = frontTradeService.findFentrustById(sell
									.getFid());

							if (buy == null || sell == null) {
								log.error("buy or sell null;");
								continue;
							}

							if (buy.getFstatus() == EntrustStatusEnum.Going
									|| buy.getFstatus() == EntrustStatusEnum.PartDeal) {
								realTimeData.addEntrustBuyMap(id, buy);
							} else {
								realTimeData.removeEntrustBuyMap(id, buy);
							}
							if (sell.getFstatus() == EntrustStatusEnum.Going
									|| sell.getFstatus() == EntrustStatusEnum.PartDeal) {
								realTimeData.addEntrustSellMap(id, sell);
							} else {
								realTimeData
										.removeEntrustSellMap(id, sell);
							}
						}

						break first;
						// end
					}
				}
				if (rehandle) {
					dealMaking(id);
				}

			}
	}

		//市价单与市价单
		private void limitBuySellDealMaking(String id) {

			boolean rehandle = false;
			Fvirtualcointype fvirtualcointype = frontVirtualCoinService
					.findFvirtualCoinById(id);
			Object[] buyLimitFentrusts = realTimeData
					.getEntrustLimitBuyMap(id).toArray();
			Object[] sellLimitFentrusts = realTimeData.getEntrustLimitSellMap(id)
					.toArray();
			
			
			Object[] buys = realTimeData
					.getEntrustBuyMap(id).toArray();
			Object[] sells = realTimeData.getEntrustSellMap(id)
					.toArray();
			
			
			if ( buys.length==0&&sells.length==0&& buyLimitFentrusts.length > 0 && sellLimitFentrusts.length > 0) {

					Fentrust buy = (Fentrust) buyLimitFentrusts[0];
					Fentrust sell = (Fentrust) sellLimitFentrusts[0];
					

					if (buy.getFstatus() == EntrustStatusEnum.AllDeal
							|| buy.getFstatus() == EntrustStatusEnum.Cancel
							) {
						realTimeData.removeEntrustLimitBuyMap(id, buy);
						return;
					}
					if (sell.getFstatus() == EntrustStatusEnum.AllDeal
							|| sell.getFstatus() == EntrustStatusEnum.Cancel
							) {
						realTimeData.removeEntrustLimitSellMap(id, sell);
						return;
					}

					double buyPrize = this.realTimeData.getLatestDealPrize(id);
					double buyCount = (buy.getFamount() - buy
							.getFsuccessAmount()) / buyPrize;
					buyCount = buyCount > sell.getFleftCount() ? sell
							.getFleftCount() : buyCount;
					
					boolean isbuy =false;
					boolean issell =false;
					if(Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(buy.getFcreateTime())) > Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(sell.getFcreateTime()))){
						isbuy = true;
					}else if(Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(buy.getFcreateTime())) < Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(sell.getFcreateTime()))){
						issell = true;
					}else{
						issell = true;
					}

					Fentrustlog buyFentrustlog = new Fentrustlog();
					buyFentrustlog.setFamount(buyCount * buyPrize);
					buyFentrustlog.setFcount(buyCount);
					buyFentrustlog.setFcreateTime(Utils.getTimestamp());
					buyFentrustlog.setFprize(buyPrize);
					buyFentrustlog.setIsactive(isbuy);
					buyFentrustlog.setFentrust(buy);
					buyFentrustlog.setfEntrustType(EntrustTypeEnum.BUY);
					buyFentrustlog.setFvirtualcointype(fvirtualcointype);

					Fentrustlog sellFentrustlog = new Fentrustlog();
					sellFentrustlog.setFamount(buyCount * buyPrize);
					sellFentrustlog.setFcount(buyCount);
					sellFentrustlog.setFcreateTime(Utils.getTimestamp());
					sellFentrustlog.setFprize(buyPrize);
					sellFentrustlog.setIsactive(issell);
					sellFentrustlog.setFentrust(sell);
					sellFentrustlog.setfEntrustType(EntrustTypeEnum.SELL);
					sellFentrustlog.setFvirtualcointype(fvirtualcointype);

					boolean ret = false;
					try {
						frontTradeService.updateDealMaking(buy, sell,
								buyFentrustlog, sellFentrustlog, id);
						ret = true;
					} catch (Exception e) {
						// e.printStackTrace();
						log.error(e.getMessage());
					}

					if (ret) {
						// 加入成功交易
						realTimeData.addEntrustSuccessMap(id,
								sellFentrustlog);
						// 加入成功交易
						realTimeData.addEntrustSuccessMap(id,
								buyFentrustlog);
						

						if (buy.getFstatus() == EntrustStatusEnum.Going
								|| buy.getFstatus() == EntrustStatusEnum.PartDeal) {
							if (buy.isFisLimit()) {
								realTimeData.addEntrustLimitBuyMap(id,
										buy);
							} else {
								realTimeData.addEntrustBuyMap(id, buy);
							}
						} else if (buy.getFstatus() == EntrustStatusEnum.AllDeal) {
							if (buy.isFisLimit()) {
								realTimeData.removeEntrustLimitBuyMap(
										id, buy);
							} else {
								realTimeData.removeEntrustBuyMap(id,
										buy);
							}

						}

						if (sell.getFstatus() == EntrustStatusEnum.Going
								|| sell.getFstatus() == EntrustStatusEnum.PartDeal) {
							if (sell.isFisLimit()) {
								realTimeData.addEntrustLimitSellMap(
										id, sell);
							} else {
								realTimeData.addEntrustSellMap(id,
										sell);
							}
						} else if (sell.getFstatus() == EntrustStatusEnum.AllDeal) {
							if (sell.isFisLimit()) {
								realTimeData
										.removeEntrustLimitSellMap(id, sell);
							} else {
								realTimeData.removeEntrustSellMap(id,
										sell);
							}

						}

						rehandle = true;
						
					} else {
						buy = frontTradeService.findFentrustById(buy
								.getFid());
						sell = frontTradeService.findFentrustById(sell
								.getFid());

						if (buy == null || sell == null) {
							log.error("buy or sell null;");
							return ;
						}

						if (buy.getFstatus() == EntrustStatusEnum.Going
								|| buy.getFstatus() == EntrustStatusEnum.PartDeal) {
							realTimeData
									.addEntrustLimitBuyMap(id, buy);
						} else {
							realTimeData.removeEntrustLimitBuyMap(id,
									buy);
						}
						if (sell.getFstatus() == EntrustStatusEnum.Going
								|| sell.getFstatus() == EntrustStatusEnum.PartDeal) {
							realTimeData.addEntrustSellMap(id, sell);
						} else {
							realTimeData
									.removeEntrustSellMap(id, sell);
						}
					}

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
}
