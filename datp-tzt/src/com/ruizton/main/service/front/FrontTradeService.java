package com.ruizton.main.service.front;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.EntrustPlanStatusEnum;
import com.ruizton.main.Enum.EntrustPlanTypeEnum;
import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.dao.FentrustDAO;
import com.ruizton.main.dao.FentrustlogDAO;
import com.ruizton.main.dao.FentrustplanDAO;
import com.ruizton.main.dao.FfeesDAO;
import com.ruizton.main.dao.FintrolinfoDAO;
import com.ruizton.main.dao.FscoreDAO;
import com.ruizton.main.dao.FsubscriptionDAO;
import com.ruizton.main.dao.FsubscriptionlogDAO;
import com.ruizton.main.dao.FuserDAO;
import com.ruizton.main.dao.FvirtualcointypeDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fentrustplan;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Fscore;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.util.Comm;
import com.ruizton.util.Utils;

@Service
public class FrontTradeService {
	private static final Logger log = LoggerFactory.getLogger(FrontTradeService.class);

	@Autowired
	private FentrustDAO fentrustDAO;
	@Autowired
	private FentrustlogDAO fentrustlogDAO;
	@Autowired
	private FwalletDAO fwalletDAO;
	@Autowired
	private FvirtualcointypeDAO fvirtualcointypeDAO;
	@Autowired
	private RealTimeData realTimeData;
	@Autowired
	private FuserDAO fuserDAO;
	@Autowired
	private FvirtualwalletDAO fvirtualwalletDAO;
	@Autowired
	private FentrustplanDAO fentrustplanDAO;
	@Autowired
	private FfeesDAO ffeesDAO;
	@Autowired
	private FintrolinfoDAO fintrolinfoDAO;
	@Autowired
	private FsubscriptionDAO fsubscriptionDAO;
	@Autowired
	private FsubscriptionlogDAO fsubscriptionlogDAO;
	@Autowired
	private FscoreDAO fscoreDAO;
	@Autowired
	private FintrolinfoDAO introlinfoDAO;
	@Autowired
	private FsubscriptionDAO subscriptionDAO;
	@Autowired
	private VirtualWalletService virtualWalletService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private StationMailService stationMailService;
	// 手续费率
	private Map<String, Double> rates = new HashMap<String, Double>();

	public void putRates(String key, double value) {
		synchronized (this.rates) {
			this.rates.put(key, value);
		}
	}

	public double getRates(String vid, boolean isbuy) {
		String key = vid + "_" + (isbuy ? "buy" : "sell");
		synchronized (this.rates) {
			return this.rates.get(key);
		}
	}

	public void updateDealMaking(Fentrust buy, Fentrust sell, Fentrustlog buyLog, Fentrustlog sellLog, String id) {

		boolean flag = false;
		try {

			double buyFee = 0D;
			try {
				if (buy.isFisLimit() == false) {// limit=0
					buyFee = (buyLog.getFamount() / buy.getFamount()) * buy.getFfees();
				} else {// limit==1
					double ffeeRate = this.getRates(buy.getFvirtualcointype().getFid(), true);
					buyFee = buyLog.getFcount() * ffeeRate;
				}
			} catch (Exception e) {
			}
			if (buy.isFisLimit()) {
				buy.setFcount(buy.getFcount() + buyLog.getFcount());
				buy.setFsuccessAmount(buy.getFsuccessAmount() + (buyLog.getFamount()));
				// buy.setFleftfees(buy.getFleftfees() - buyFee);
				buy.setFfees(buy.getFfees() + buyFee);
				buy.setFlastUpdatTime(Utils.getTimestamp());
				if (buy.getFamount() - buy.getFsuccessAmount() < 0.000001D) {
					buy.setFstatus(EntrustStatusEnum.AllDeal);
				} else {
					buy.setFstatus(EntrustStatusEnum.PartDeal);
				}
				fentrustDAO.attachDirty(buy);
			} else {
				buy.setFleftCount(buy.getFleftCount() - buyLog.getFcount());
				buy.setFsuccessAmount(buy.getFsuccessAmount() + (buyLog.getFamount()));
				buy.setFlastUpdatTime(Utils.getTimestamp());
				buy.setFleftfees(buy.getFleftfees() - buyFee);
				if (buy.getFleftCount() < 0.000001D) {
					buy.setFstatus(EntrustStatusEnum.AllDeal);
				} else {
					buy.setFstatus(EntrustStatusEnum.PartDeal);
				}
				fentrustDAO.attachDirty(buy);
			}

			double sellFee = 0D;
			try {
				if (sell.isFisLimit() == false) {// limit==0
					sellFee = (buyLog.getFcount() / sell.getFcount()) * sell.getFfees();
				} else {// limit==1
					double sellRate = this.getRates(sell.getFvirtualcointype().getFid(), false);
					sellFee = sellRate * sellLog.getFamount();
				}
			} catch (Exception e) {
			}
			if (sell.isFisLimit()) {
				sell.setFsuccessAmount(sell.getFsuccessAmount() + buyLog.getFamount());
				sell.setFamount(sell.getFamount() + buyLog.getFamount());
				sell.setFleftCount(sell.getFleftCount() - buyLog.getFcount());
				// sell.setFleftfees(sell.getFleftfees() - sellFee);
				sell.setFfees(sell.getFfees() + sellFee);
				sell.setFlastUpdatTime(Utils.getTimestamp());
				if (sell.getFleftCount() < 0.000001F) {
					sell.setFstatus(EntrustStatusEnum.AllDeal);
				} else {
					sell.setFstatus(EntrustStatusEnum.PartDeal);
				}
				fentrustDAO.attachDirty(sell);
			} else {
				sell.setFleftCount(sell.getFleftCount() - buyLog.getFcount());
				sell.setFsuccessAmount(sell.getFsuccessAmount() + (sellLog.getFamount()));
				sell.setFleftfees(sell.getFleftfees() - sellFee);
				sell.setFlastUpdatTime(Utils.getTimestamp());
				if (sell.getFleftCount() < 0.000001F) {
					sell.setFstatus(EntrustStatusEnum.AllDeal);
				} else {
					sell.setFstatus(EntrustStatusEnum.PartDeal);
				}
				fentrustDAO.attachDirty(sell);
			}
			buyLog.setFfees(buyFee);
			buyLog.setFhasSubscription(false);
			buyLog.setfus_fId(buy.getFuser().getFid());
			fentrustlogDAO.save(buyLog);
			sellLog.setFfees(sellFee);
			sellLog.setFhasSubscription(false);
			sellLog.setfus_fId(sell.getFuser().getFid());
			fentrustlogDAO.save(sellLog);

			// 更新钱包
			Fwallet fbuyWallet = null;
			Fwallet fsellWallet = null;
			Fvirtualwallet fbuyVirtualwallet1 = null;
			Fvirtualwallet fsellVirtualwallet1 = null;
			// 获取默认兑换rmb种类
			List<Fvirtualcointype> findByParam = this.fvirtualcointypeDAO.findByParam(0, 0, " where fisDefAsset=1",
					false, "Fvirtualcointype");
			boolean flags = findByParam.size() != 0;
			Fsubscription fsubscription = null;
			if (flags) {
				Fvirtualcointype fvirtualcointype = findByParam.get(0);
				String filter2 = "where fisRMB=1 and ftype=" + SubscriptionTypeEnum.COIN + " and fvirtualcointype.fid='"
						+ fvirtualcointype.getFid() + "' order by fcreateTime desc";
				List<Fsubscription> list = this.subscriptionDAO.list(0, 0, filter2, false);
				fsubscription = list.get(0);
				if (null != buy.getFuser() && null != sell.getFuser()) {
					if (buy.getFuser().getFid().equals(sell.getFuser().getFid())) {
						if (Comm.getISREDIS()) {
							fbuyVirtualwallet1 = this.fvirtualwalletDAO.findVirtualWallet(buy.getFuser().getFid(),
									fvirtualcointype.getFid());
						} else {
							fbuyVirtualwallet1 = this.fvirtualwalletDAO.findVirtualWallet(buy.getFuser().getFid(),
									fvirtualcointype.getFid());
						}
						fsellVirtualwallet1 = fbuyVirtualwallet1;
					} else {
						if (Comm.getISREDIS()) {
							fbuyVirtualwallet1 = this.fvirtualwalletDAO.findVirtualWallet(buy.getFuser().getFid(),
									fvirtualcointype.getFid());
							fsellVirtualwallet1 = this.fvirtualwalletDAO.findVirtualWallet(sell.getFuser().getFid(),
									fvirtualcointype.getFid());
						} else {
							fbuyVirtualwallet1 = this.fvirtualwalletDAO.findVirtualWallet(buy.getFuser().getFid(),
									fvirtualcointype.getFid());
							fsellVirtualwallet1 = this.fvirtualwalletDAO.findVirtualWallet(sell.getFuser().getFid(),
									fvirtualcointype.getFid());

						}
					}
					fbuyVirtualwallet1.setFfrozen(
							fbuyVirtualwallet1.getFfrozen() - buyLog.getFamount() * fsubscription.getFprice());
					fbuyVirtualwallet1.setFlastUpdateTime(Utils.getTimestamp());
					this.fvirtualwalletDAO.attachDirty(fbuyVirtualwallet1);
					fsellVirtualwallet1.setFtotal(fsellVirtualwallet1.getFtotal()
							+ buyLog.getFamount() * fsubscription.getFprice() - sellFee * fsubscription.getFprice());
					fsellVirtualwallet1.setFlastUpdateTime(Utils.getTimestamp());
					this.fvirtualwalletDAO.attachDirty(fsellVirtualwallet1);
				}
			} else {
				if (null != buy.getFuser() && null != sell.getFuser()) {
					if (buy.getFuser().getFid().equals(sell.getFuser().getFid())) {
						fbuyWallet = buy.getFuser().getFwallet();
						if (Comm.getISREDIS()) {
							fbuyWallet = fwalletDAO.findById(buy.getFuser().getFwallet().getFid());
						} else {
							fbuyWallet = buy.getFuser().getFwallet();
						}
						fsellWallet = fbuyWallet;
					} else {
						if (Comm.getISREDIS()) {
							fbuyWallet = fwalletDAO.findById(buy.getFuser().getFwallet().getFid());
							fsellWallet = fwalletDAO.findById(sell.getFuser().getFwallet().getFid());
						} else {
							fbuyWallet = buy.getFuser().getFwallet();
							fsellWallet = sell.getFuser().getFwallet();
						}
					}

					fbuyWallet.setFfrozenRmb(fbuyWallet.getFfrozenRmb() - buyLog.getFamount());
					fbuyWallet.setFlastUpdateTime(Utils.getTimestamp());
					this.fwalletDAO.attachDirty(fbuyWallet);

					fsellWallet.setFtotalRmb(fsellWallet.getFtotalRmb() + buyLog.getFamount() - sellFee);
					fsellWallet.setFlastUpdateTime(Utils.getTimestamp());
					this.fwalletDAO.attachDirty(fsellWallet);

				}
			}
			// 虚拟钱包
			Fvirtualwallet fbuyVirtualwallet = null;
			Fvirtualwallet fsellVirtualwallet = null;
			if (null != buy.getFuser() && null != sell.getFuser()) {
				if (buy.getFuser().getFid().equals(sell.getFuser().getFid())) {
					fbuyVirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(buy.getFuser().getFid(),
							buy.getFvirtualcointype().getFid());
					fsellVirtualwallet = fbuyVirtualwallet;
				} else {
					fbuyVirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(buy.getFuser().getFid(),
							buy.getFvirtualcointype().getFid());
					fsellVirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(sell.getFuser().getFid(),
							sell.getFvirtualcointype().getFid());
				}

				fbuyVirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
				fbuyVirtualwallet.setFtotal(fbuyVirtualwallet.getFtotal() + buyLog.getFcount() - buyFee);
				this.fvirtualwalletDAO.attachDirty(fbuyVirtualwallet);

				fsellVirtualwallet.setFfrozen(fsellVirtualwallet.getFfrozen() - buyLog.getFcount());
				fsellVirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
				this.fvirtualwalletDAO.attachDirty(fsellVirtualwallet);
			}
			if (buy.getFstatus() == EntrustStatusEnum.AllDeal) {
				if (flags) {
					double left_amount = (buy.getFamount() - buy.getFsuccessAmount());
					fbuyVirtualwallet1
							.setFfrozen(fbuyVirtualwallet1.getFfrozen() - left_amount * fsubscription.getFprice());
					fbuyVirtualwallet1
							.setFtotal(fbuyVirtualwallet1.getFtotal() + left_amount * fsubscription.getFprice());
					fbuyVirtualwallet1.setFlastUpdateTime(Utils.getTimestamp());
					this.fvirtualwalletDAO.attachDirty(fbuyVirtualwallet1);
				} else {
					// 因为有人低价卖出，冻结剩余部分返回钱包
					double left_amount = (buy.getFamount() - buy.getFsuccessAmount());
					fbuyWallet.setFfrozenRmb(fbuyWallet.getFfrozenRmb() - left_amount);
					fbuyWallet.setFtotalRmb(fbuyWallet.getFtotalRmb() + left_amount);
					fbuyWallet.setFlastUpdateTime(Utils.getTimestamp());
					this.fwalletDAO.attachDirty(fbuyWallet);

				}
			}

			if (flags) {
				buyLog.setFamount(buyLog.getFamount() * fsubscription.getFprice());
				buyLog.setDefAssSymbol(fsubscription.getFvirtualcointype().getfSymbol());
				sellFee = sellFee * fsubscription.getFprice();
			} else {
				buyLog.setFamount(buyLog.getFamount());
				buyLog.setDefAssSymbol("￥");
			}
			stationMailService.save(buyLog, sell.getFuser().getFid(), buyFee, sellFee);
			// 买方 需要知道 每个buy.getFprize()买的 买了多少个buyLog.getFcount() - buyFee 花了
			// 多少钱 buyLog.getFamount()
			// 买房 每个buy.getFprize()卖的 卖了多少个 buyLog.getFcount() 一共挣了多少
			// buyLog.getFamount() - sellFee

			/*
			 * //加入成功交易 this.realTimeData.addEntrustSuccessMap(id, sellLog) ;
			 * //加入成功交易 this.realTimeData.addEntrustSuccessMap(id, buyLog) ;
			 */

		} catch (Exception e) {
			throw new RuntimeException();
		}

	}

	public Fentrust findFentrustById(String id) {
		return this.fentrustDAO.findById(id);
	}

	public List<Fentrustlog> findFentrustLogByFentrust(Fentrust fentrust) {
		return this.fentrustlogDAO.findByProperty("fentrust.fid", fentrust.getFid());
	}

	public List<Fentrustlog> findFentrustLogsByParam(int firstResult, int maxResults, String filter, boolean isFY) {
		return this.fentrustDAO.findByParam(firstResult, maxResults, filter, isFY, "Fentrustlog");
	}

	// 最新成交记录
	public List<Fentrust> findLatestSuccessDeal(String coinTypeId, int fentrustType, int count) {
		return this.fentrustDAO.findLatestSuccessDeal(coinTypeId, fentrustType, count);
	}

	public List<Fentrust> findAllGoingFentrust(String coinTypeId, int fentrustType, boolean isLimit) {
		return this.fentrustDAO.findAllGoingFentrust(coinTypeId, fentrustType, isLimit);
	}

	// 获得24小时内的成交记录
	/*
	 * public List<Fentrustlog> findLatestSuccessDeal24(int coinTypeId, int
	 * hour) { List<Fentrustlog> list =
	 * this.fentrustlogDAO.findLatestSuccessDeal24(coinTypeId, 24); if(list ==
	 * null || list.size() == 0){ return null; } return list; }
	 */

	// 最近N条成交记录
	public List<Fentrustlog> findLatestSuccessDeal(String coinTypeId, int count) {
		List<Fentrustlog> list = this.fentrustlogDAO.findLatestSuccessDeal(coinTypeId, count);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list;
	}

	public Fentrustlog findLatestDeal(String coinTypeId) {
		Fentrustlog fentrust = this.fentrustDAO.findLatestDeal(coinTypeId);
		if (fentrust == null)
			return null;
		return fentrust;
	}

	// 委托记录
	public List<Fentrust> findFentrustHistory(String fuid, String fvirtualCoinTypeId, int[] entrust_type,
			int first_result, int max_result, String order, int entrust_status[], Date beginDate, Date endDate)
			throws Exception {
		List<Fentrust> list = this.fentrustDAO.getFentrustHistory(fuid, fvirtualCoinTypeId, entrust_type, first_result,
				max_result, order, entrust_status, beginDate, endDate);
		for (Fentrust fentrust : list) {
			fentrust.getFvirtualcointype().getFname();
		}
		return list;
	}

	// 计划委托
	public List<Fentrustplan> findEntrustPlan(int type, int status[]) {
		List<Fentrustplan> list = this.fentrustplanDAO.findEntrustPlan(type, status);

		return list;
	}

	// 委托买入
	public Fentrust updateEntrustBuy(String coinTypeId, double tradeAmount, double tradeCnyPrice, Fuser fuser,
			boolean fisLimit) throws Exception {

		boolean flag = false;
		Fvirtualcointype f = fvirtualcointypeDAO.findByFisDefAsset("1");// 获取默认充值资产

		try {
			Fwallet fwallet = this.fwalletDAO.findById(fuser.getFwallet().getFid());

			double ffeeRate = this.ffeesDAO.findFfee(coinTypeId, fuser.getFscore().getFlevel()).getFbuyfee();
			double ffee = 0F;

			// 买入总价格
			double totalTradePrice = 0F;
			if (fisLimit) {
				totalTradePrice = tradeCnyPrice;
				ffee = 0;
			} else {
				// 总手续费人民币
				totalTradePrice = tradeAmount * tradeCnyPrice;
				ffee = tradeAmount * ffeeRate;
			}
			//
			if (null != f) {
				String fivwFilter = "where fvirtualcointype.fid='" + f.getFid() + "' and fuser.fid='" + fuser.getFid()
						+ "'";
				// 获取平台钱包
				Fvirtualwallet fvirtualwallet = null;
				fvirtualwallet = virtualWalletService.list(0, 0, fivwFilter, false).get(0);
				// 获取比例 人民币与数字资产的
				Fsubscription fsubscription = subscriptionService.findByFviId(f.getFid());
				double price = fsubscription.getFprice();
				if (fvirtualwallet.getFtotal() < totalTradePrice * price)
					return null;
				fvirtualwallet.setFtotal(fvirtualwallet.getFtotal() - totalTradePrice * price);
				fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen() + totalTradePrice * price);
				fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
				this.virtualWalletService.updateObj(fvirtualwallet);
			} else {
				if (fwallet.getFtotalRmb() < totalTradePrice)
					return null;
				fwallet.setFtotalRmb(fwallet.getFtotalRmb() - totalTradePrice);
				fwallet.setFfrozenRmb(fwallet.getFfrozenRmb() + totalTradePrice);
				fwallet.setFlastUpdateTime(Utils.getTimestamp());
				this.fwalletDAO.attachDirty(fwallet);
			}

			Fentrust fentrust = new Fentrust();

			if (fisLimit) {
				fentrust.setFcount(0F);
				fentrust.setFleftCount(0F);
				fentrust.setFprize(0);
			} else {
				fentrust.setFcount(tradeAmount);
				fentrust.setFleftCount(tradeAmount);
				fentrust.setFprize(tradeCnyPrice);
			}

			fentrust.setFamount(totalTradePrice);
			fentrust.setFfees(ffee);
			fentrust.setFleftfees(ffee);
			fentrust.setFcreateTime(Utils.getTimestamp());
			fentrust.setFentrustType(EntrustTypeEnum.BUY);
			fentrust.setFisLimit(fisLimit);
			fentrust.setFlastUpdatTime(Utils.getTimestamp());
			fentrust.setFstatus(EntrustStatusEnum.Going);
			fentrust.setFsuccessAmount(0F);
			fentrust.setFhasSubscription(false);
			fentrust.setFuser(fuser);
			fentrust.setFvirtualcointype(this.fvirtualcointypeDAO.findById(coinTypeId));
			fuser.setFwallet(fwallet);
			this.fentrustDAO.save(fentrust);

			return fentrust;
		} catch (Exception e) {
			throw new RuntimeException();
		}

	}

	// 委托卖出
	public Fentrust updateEntrustSell(String coinTypeId, double tradeAmount, double tradeCnyPrice, Fuser fuser,
			boolean fisLimit) throws Exception {

		boolean flag = false;

		try {
			Fvirtualwallet fvirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(fuser.getFid(), coinTypeId);
			if (fvirtualwallet.getFtotal() < tradeAmount) {
				return null;
			}

			fvirtualwallet.setFtotal(fvirtualwallet.getFtotal() - tradeAmount);
			fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen() + tradeAmount);
			fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);

			double ffeeRate = this.ffeesDAO.findFfee(coinTypeId, fuser.getFscore().getFlevel()).getFfee();
			Fentrust fentrust = new Fentrust();

			// 总手续费人民币
			double ffee = 0;
			if (fisLimit) {
				fentrust.setFamount(0F);
				fentrust.setFfees(ffee);
				fentrust.setFleftfees(ffee);
			} else {
				ffee = tradeAmount * tradeCnyPrice * ffeeRate;
				fentrust.setFamount(tradeAmount * tradeCnyPrice);
				fentrust.setFfees(ffee);
				fentrust.setFleftfees(ffee);
			}

			fentrust.setFcount(tradeAmount);
			fentrust.setFleftCount(tradeAmount);
			fentrust.setFcreateTime(Utils.getTimestamp());
			fentrust.setFentrustType(EntrustTypeEnum.SELL);
			fentrust.setFisLimit(fisLimit);
			fentrust.setFlastUpdatTime(Utils.getTimestamp());
			fentrust.setFprize(tradeCnyPrice);
			fentrust.setFstatus(EntrustStatusEnum.Going);
			fentrust.setFsuccessAmount(0F);
			fentrust.setFuser(fuser);
			fentrust.setFhasSubscription(false);
			fentrust.setFvirtualcointype(this.fvirtualcointypeDAO.findById(coinTypeId));
			this.fentrustDAO.save(fentrust);

			return fentrust;
		} catch (Exception e) {
			throw new RuntimeException();
		}

	}

	// 委托记录
	public List<Fentrust> findFentrustHistory(int firstResult, int maxResults, String filter, boolean isFY)
			throws Exception {
		List<Fentrust> list = this.fentrustDAO.list(firstResult, maxResults, filter, isFY);
		for (Fentrust fentrust : list) {
			fentrust.getFvirtualcointype().getFname();
		}
		return list;
	}

	// 委托记录
	public List<Fentrust> findFentrustHistory(String fuid, String fvirtualCoinTypeId, int[] entrust_type,
			int first_result, int max_result, String order, int entrust_status[]) throws Exception {
		List<Fentrust> list = this.fentrustDAO.getFentrustHistory(fuid, fvirtualCoinTypeId, entrust_type, first_result,
				max_result, order, entrust_status);
		for (Fentrust fentrust : list) {
			fentrust.getFvirtualcointype().getFname();
		}
		return list;
	}

	public int findFentrustHistoryCount(String fuid, String fvirtualCoinTypeId, int[] entrust_type,
			int entrust_status[]) throws Exception {
		return this.fentrustDAO.getFentrustHistoryCount(fuid, fvirtualCoinTypeId, entrust_type, entrust_status);
	}

	public List<Fentrustplan> findFentrustplan(String fuser, String fvirtualcointype, int[] fstatus, int firtResult,
			int maxResult, String order) {
		return this.fentrustplanDAO.findFentrustplan(fuser, fvirtualcointype, fstatus, firtResult, maxResult, order);
	}

	public Fentrustplan findFentrustplanById(int id) {
		return this.fentrustplanDAO.findById(id);
	}

	public long findFentrustplanCount(String fuser, String fvirtualcointype, int[] fstatus) {
		return this.fentrustplanDAO.findFentrustplanCount(fuser, fvirtualcointype, fstatus);
	}

	public void updateCancelFentrust(Fentrust fentrust, Fuser fuser) {

		try {
			Fwallet fwallet = fuser.getFwallet();
			Fvirtualwallet fvirtualDefWallet = null;// 默认资产
			// 获取比例 人民币与数字资产的
			Fsubscription fsubscription = null;
			double price = 1;
			Fvirtualwallet fvirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(fuser.getFid(),
					fentrust.getFvirtualcointype().getFid());
			java.sql.Timestamp now = Utils.getTimestamp();

			fentrust.setFlastUpdatTime(now);
			fentrust.setFstatus(EntrustStatusEnum.Cancel);
			this.fentrustDAO.attachDirty(fentrust);
			Fvirtualcointype f = fvirtualcointypeDAO.findByFisDefAsset("1");// 获取默认充值资产
			if (fentrust.getFentrustType() == EntrustTypeEnum.BUY) {
				// 买
				double leftAmount = fentrust.getFamount() - fentrust.getFsuccessAmount();
				if (null != f) {
					fsubscription = subscriptionService.findByFviId(f.getFid());
					price = fsubscription.getFprice();
					fvirtualDefWallet = fvirtualwalletDAO.findVirtualWallet(fuser.getFid(), f.getFid());
					fvirtualDefWallet.setFtotal(fvirtualDefWallet.getFtotal() + leftAmount * price);
					fvirtualDefWallet.setFfrozen(fvirtualDefWallet.getFfrozen() - leftAmount * price);
					fvirtualDefWallet.setFlastUpdateTime(now);
					this.fvirtualwalletDAO.attachDirty(fvirtualDefWallet);
				} else {
					fwallet.setFtotalRmb(fwallet.getFtotalRmb() + leftAmount);
					fwallet.setFfrozenRmb(fwallet.getFfrozenRmb() - leftAmount);
					fwallet.setFlastUpdateTime(now);
					this.fwalletDAO.attachDirty(fwallet);
				}

			} else {
				// 卖
				double leftCount = fentrust.getFleftCount();
				fvirtualwallet.setFtotal(fvirtualwallet.getFtotal() + leftCount);
				fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen() - leftCount);
				fvirtualwallet.setFlastUpdateTime(now);
				this.fvirtualwalletDAO.attachDirty(fvirtualwallet);

			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateCancelEntrustPlan(Fentrustplan fentrustplan, Fuser fuser) {

		try {
			Fwallet fwallet = fuser.getFwallet();
			Fvirtualwallet fvirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(fuser.getFid(),
					fentrustplan.getFvirtualcointype().getFid());
			this.fentrustplanDAO.attachDirty(fentrustplan);

			if (fentrustplan.getFtype() == EntrustPlanTypeEnum.BUY) {
				double total = fentrustplan.getFamount();
				fwallet.setFtotalRmb(fwallet.getFtotalRmb() + total);
				fwallet.setFfrozenRmb(fwallet.getFfrozenRmb() - total);
				fwallet.setFlastUpdateTime(Utils.getTimestamp());
				this.fwalletDAO.attachDirty(fwallet);
			} else {
				double total = fentrustplan.getFcount();
				fvirtualwallet.setFtotal(fvirtualwallet.getFtotal() + total);
				fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen() - total);
				fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
				this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}

	}

	public Fsubscription findFsubscriptionById(String id) {
		return this.fsubscriptionDAO.findById(id);
	}

	public Fsubscription findFirstSubscription(int type) {
		Fsubscription fsubscription = null;
		List<Fsubscription> fsubscriptions = this.fsubscriptionDAO.findByParam(0, 1,
				" where fisopen=1 and ftype = " + type + " order by fid asc ", true, "Fsubscription");
		if (fsubscriptions.size() > 0) {
			fsubscription = fsubscriptions.get(0);
		}
		return fsubscription;
	}

	public List<Fsubscriptionlog> findFsubscriptionlogByParam(int firstResult, int maxResults, String filter,
			boolean isFY) {
		return this.fsubscriptionlogDAO.findByParam(firstResult, maxResults, filter, isFY, "Fsubscriptionlog");
	}

	public List<Fsubscriptionlog> findFsubScriptionLog(Fuser fuser, String id) {
		List<Fsubscriptionlog> fsubscriptionlogs = this.fsubscriptionlogDAO.findByParam(0, 0, " where fuser.fid='"
				+ fuser.getFid() + "'" + " and fsubscription.fid='" + id + "'" + " order by fcreatetime desc", false,
				"Fsubscriptionlog");
		return fsubscriptionlogs;
	}

	public List<Fsubscriptionlog> findFsubScriptionLog(int firstResult, int maxResults, Fuser fuser, String id) {
		List<Fsubscriptionlog> fsubscriptionlogs = this.fsubscriptionlogDAO
				.findByParam(
						firstResult, maxResults, " where fuser.fid='" + fuser.getFid() + "'"
								+ " and fsubscription.fid='" + id + "'" + " order by fcreatetime desc",
						true, "Fsubscriptionlog");
		return fsubscriptionlogs;
	}

	public void updateSubscription(Fwallet fwallet, Fvirtualwallet fvirtualwallet, Fsubscriptionlog fsubscriptionlog,
			Fsubscription fsubscription, Fvirtualwallet fintrolVirtualwallet, Fintrolinfo fintrolinfo) {
		try {
			this.fwalletDAO.attachDirty(fwallet);
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			this.fsubscriptionDAO.attachDirty(fsubscription);
			this.fsubscriptionlogDAO.save(fsubscriptionlog);
			if (fintrolVirtualwallet != null) {
				this.fvirtualwalletDAO.attachDirty(fintrolVirtualwallet);
			}
			if (fintrolinfo != null) {
				this.introlinfoDAO.save(fintrolinfo);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public List<Fentrust> findFentrustByParam(int firstResult, int maxResults, String filter, boolean isFY) {
		return this.fentrustDAO.findByParam(firstResult, maxResults, filter, isFY, "Fentrust");
	}

	public int findFentrustByParamCount(String filter) {
		return this.fentrustDAO.findByParamCount(filter, "Fentrust");
	}

	public int findFsubscriptionlogByParamCount(String filter) {
		return this.fentrustDAO.findByParamCount(filter, "Fsubscriptionlog");
	}

	public void updateFeeLog(Fentrust entrust, Fvirtualwallet fvirtualwallet) {
		try {
			this.fentrustDAO.attachDirty(entrust);
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateSubscription(Fvirtualwallet fvirtualwalletCost, Fvirtualwallet fvirtualwallet,
			Fsubscriptionlog fsubscriptionlog, Fsubscription fsubscription) {
		try {
			this.fvirtualwalletDAO.attachDirty(fvirtualwalletCost);
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			this.fsubscriptionDAO.attachDirty(fsubscription);
			this.fsubscriptionlogDAO.save(fsubscriptionlog);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateFeeLog(Fentrust entrust, Fwallet fwallet) {
		try {
			this.fentrustDAO.attachDirty(entrust);
			this.fwalletDAO.attachDirty(fwallet);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateSubscription(Fwallet fwallet, Fvirtualwallet fvirtualwallet, Fsubscriptionlog fsubscriptionlog,
			Fsubscription fsubscription, Fintrolinfo introlinfo, Fwallet fintrolWallet) {
		try {
			this.fwalletDAO.attachDirty(fwallet);
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			this.fsubscriptionDAO.attachDirty(fsubscription);
			this.fsubscriptionlogDAO.save(fsubscriptionlog);
			if (introlinfo != null) {
				this.fintrolinfoDAO.save(introlinfo);
			}
			if (fintrolWallet != null) {
				this.fwalletDAO.attachDirty(fintrolWallet);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public List<Fentrust> findFentrustsByParam(int firstResult, int maxResults, String filter, boolean isFY) {
		return this.fentrustDAO.findByParam(firstResult, maxResults, filter, isFY, "Fentrust");
	}

	public void updateFentrustSubscription(Fintrolinfo fintrolinfo, Fentrust fentrust, Fvirtualwallet fvirtualwallet,
			Fwallet fwallet) {
		try {
			this.fintrolinfoDAO.save(fintrolinfo);
			this.fentrustDAO.attachDirty(fentrust);
			if (fvirtualwallet != null) {
				this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			}
			if (fwallet != null) {
				this.fwalletDAO.attachDirty(fwallet);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateFentrust(Fentrust fentrust) {
		try {
			this.fentrustDAO.attachDirty(fentrust);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateFentrustLog(Fentrustlog Fentrustlog) {
		try {
			this.fentrustlogDAO.attachDirty(Fentrustlog);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateFentrust(Fentrust fentrust, Fwallet fwallet, Fintrolinfo introlInfo) {
		try {
			this.fentrustDAO.attachDirty(fentrust);
			this.fwalletDAO.attachDirty(fwallet);
			this.fintrolinfoDAO.save(introlInfo);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateSubscription(Fwallet fwallet1, Fvirtualwallet fvirtualwallet1, Fvirtualwallet fvirtualwallet,
			Fsubscriptionlog fsubscriptionlog, Fsubscription fsubscription) {
		try {
			if (fwallet1 != null) {
				this.fwalletDAO.attachDirty(fwallet1);
			}
			if (fvirtualwallet1 != null) {
				this.fvirtualwalletDAO.attachDirty(fvirtualwallet1);
			}
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			this.fsubscriptionDAO.attachDirty(fsubscription);
			this.fsubscriptionlogDAO.save(fsubscriptionlog);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateSubscription(Fwallet fwallet, Fvirtualwallet fvirtualwallet, Fvirtualwallet fvirtualwallet1,
			Fsubscriptionlog fsubscriptionlog, Fsubscription fsubscription, Fvirtualwallet fintrolvirtualwallet,
			Fintrolinfo fintrolinfo) {
		try {
			if (fwallet != null) {
				this.fwalletDAO.attachDirty(fwallet);
			}
			if (fvirtualwallet1 != null) {
				this.fvirtualwalletDAO.attachDirty(fvirtualwallet1);
			}
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			this.fsubscriptionDAO.attachDirty(fsubscription);

			this.fsubscriptionlogDAO.save(fsubscriptionlog);

			if (fintrolvirtualwallet != null) {
				this.fvirtualwalletDAO.attachDirty(fintrolvirtualwallet);
			}
			if (fintrolinfo != null) {
				this.introlinfoDAO.save(fintrolinfo);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateFentrust(Fentrust fentrust, List<Fwallet> fwallets, List<Fintrolinfo> fintrolinfos) {
		try {
			this.fentrustDAO.attachDirty(fentrust);
			for (Fintrolinfo fintrolinfo : fintrolinfos) {
				this.fintrolinfoDAO.save(fintrolinfo);
			}
			for (Fwallet fwallet : fwallets) {
				this.fwalletDAO.attachDirty(fwallet);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateFentrustLog(Fentrustlog Fentrustlog, List<Fwallet> fwallets, List<Fintrolinfo> fintrolinfos) {
		try {
			this.fentrustlogDAO.attachDirty(Fentrustlog);
			for (Fintrolinfo fintrolinfo : fintrolinfos) {
				this.fintrolinfoDAO.save(fintrolinfo);
			}
			for (Fwallet fwallet : fwallets) {
				this.fwalletDAO.attachDirty(fwallet);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateCoinFentrust(Fentrust fentrust, List<Fvirtualwallet> fvirtualwallets,
			List<Fintrolinfo> fintrolinfos) {
		try {
			this.fentrustDAO.attachDirty(fentrust);
			for (Fintrolinfo fintrolinfo : fintrolinfos) {
				this.fintrolinfoDAO.save(fintrolinfo);
			}
			for (Fvirtualwallet fvirtualwallet : fvirtualwallets) {
				this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void updateCoinFentrustLog(Fentrustlog Fentrustlog, List<Fvirtualwallet> fvirtualwallets,
			List<Fintrolinfo> fintrolinfos) {
		try {
			this.fentrustlogDAO.attachDirty(Fentrustlog);
			for (Fintrolinfo fintrolinfo : fintrolinfos) {
				this.fintrolinfoDAO.save(fintrolinfo);
			}
			for (Fvirtualwallet fvirtualwallet : fvirtualwallets) {
				this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			}
		} catch (Exception e) {
			throw new RuntimeException();
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

	/**
	 * 认购数量总条数
	 * @param fuser
	 * @param fid
	 * @return
	 */
	public int findFsubScriptionLogCount(Fuser fuser, String id) {
		int totalCount = this.fsubscriptionlogDAO
				.findFsubScriptionLogCount(" where fuser.fid='" + fuser.getFid() + "'"
								+ " and fsubscription.fid='" + id + "'" , "Fsubscriptionlog");
		return totalCount;
	}

	/*
	 * public List<Fentrust> findFentrustHistory1(int fuid, int
	 * fvirtualCoinTypeId, int[] entrust_type, int first_result, int max_number,
	 * String order, int entrust_status[]) throws Exception { List<Fentrust>
	 * list = this.fentrustDAO.getFentrustHistory1(fuid, fvirtualCoinTypeId,
	 * entrust_type, first_result, max_number, order, entrust_status); for
	 * (Fentrust fentrust : list) { fentrust.getFvirtualcointype().getFname(); }
	 * return list; }
	 */
}
