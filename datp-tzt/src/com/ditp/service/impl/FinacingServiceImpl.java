package com.ditp.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ditp.dao.DetailDao;
import com.ditp.dao.FinacingDao;
import com.ditp.dao.ProfitlogDAO;
import com.ditp.dao.TradeDao;
import com.ditp.dao.WalletDao;
import com.ditp.entity.Detail;
import com.ditp.entity.Finacing;
import com.ditp.entity.FinacingRead;
import com.ditp.entity.ProfitLog;
import com.ditp.entity.TradeLog;
import com.ditp.entity.Wallet;
import com.ditp.service.FinacingService;
import com.ruizton.main.dao.FuserDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.util.Utils;

@Service
@Transactional
public class FinacingServiceImpl implements FinacingService {
	@Autowired
	private FinacingDao finacingDao;
	@Autowired
	private WalletDao walletDao;
	@Autowired
	private FwalletDAO fwalletDao;
	@Autowired
	private FvirtualwalletDAO fvirtualwalletDao;
	@Autowired
	private FuserDAO fuserDao;
	@Autowired
	private ProfitlogDAO profitlogDAO;
	@Autowired
	private TradeDao tradeDao;
	@Autowired
	private DetailDao detailDao;
	@Autowired
	private FvirtualwalletDAO fvirtualwalletDAO ;
	@Override
	public List<FinacingRead> get(Integer page) {
		List<FinacingRead> list = finacingDao.get(page);
		return list;
	}
	public List<FinacingRead> get(Integer page, String filter) {
		List<FinacingRead> list = finacingDao.get(page,filter);
		return list;
	}

	@Override
	public List<Finacing> getRecommendProduct(int flag) {
		return finacingDao.getRecommendProduct(flag);
	}

	/**
	 * 理财自动结算
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean autoClear() throws ParseException {
		Fuser fuser = null;// 用户理财钱包
	 	List<Wallet> listFincWallet = null;// 用户理财钱包
		Fwallet fwallet = null;// 用户钱包
		Fvirtualwallet fvirtualwallet = null;// 用户虚拟币钱包
		List<Fwallet> listFwallet = null;

		String filter = "where fstatus=00501";
		List<Finacing> list = finacingDao.getByfilter(filter);
		if (null != list) {
			// 循环理财
			for (Finacing finacing : list) {
				try {
					double clearMoney = 0;// 结算金额
					int fcycle = Integer.parseInt(finacing.getFcycle());// 结算周期
					long nowDay = Utils.getTimestamp().getTime();// 当前时间
					long day = (long) (nowDay / 60 / 60 / 1000 / 24);
					//long day = (long) (nowDay / 60 / 1000);//分钟
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = sdf.parse(finacing.getFbeginTime());
					long fincBeginday = (long) (date.getTime() / 60 / 60 / 1000 / 24);
					//long fincBeginday = (long) (date.getTime() / 60/ 1000);//分钟
					long fcycleDay = day - fincBeginday;
					System.out.println("相差日期  "+day);
					System.out.println("相差日期  "+fincBeginday);

					//
					if (fcycleDay == 0 || fcycleDay < 0) {
						continue;
					}
					boolean isClear = false;
					// 判断今天是否为结算周期的时间
					if (fcycleDay % fcycle == 0) {
						isClear = true;
					}
					if (isClear) {
						// 根据理财产品查询钱包
						listFincWallet = walletDao.get(" where ftotal!=0 and ffinaId='" + finacing.getFid() + "'");
						if (null != listFincWallet && listFincWallet.size() > 0) {
							for (Wallet fincWallet : listFincWallet) {
								try {
									fuser = fuserDao.findById(fincWallet.getFuserId());
									if (null != fincWallet) {
										// 计算收益
										clearMoney = finacing.getFproportion() / 100 * fincWallet.getFtotal();
										// 获取用户钱包进行结算 00201 人民币
										if (finacing.getFassetsType().equals("00201")) {
											fwallet = fwalletDao.findById(fuser.getFwallet().getFid());
											if (finacing.getFtype().equals("00102")) {//固定
												fwallet.setFtotalRmb(fwallet.getFtotalRmb()+ fincWallet.getFtotal() + clearMoney);
											}else{
												fwallet.setFtotalRmb(fwallet.getFtotalRmb() + clearMoney);
											}
											fwallet.setFlastUpdateTime(Utils.getTimestamp());
											fwalletDao.attachDirty(fwallet);

										} else if (finacing.getFassetsType().equals("00202")) {// 数字资产
																								// 00202
											fvirtualwallet = fvirtualwalletDao.findVirtualWallet(fincWallet.getFuserId(),
													finacing.getFvitypeId());
											if (finacing.getFtype().equals("00102")) {//固定
												fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+fincWallet.getFtotal() + clearMoney);
											} else {
												fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+ clearMoney);
											}
											fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
											fvirtualwalletDao.attachDirty(fvirtualwallet);
										}
										// 固定期限理财
										if (finacing.getFtype().equals("00102")) {
											double amount = fincWallet.getFtotal();
											fincWallet.setFtotal(0);
											fincWallet.setFlastUpdateTime(Utils.getCurTimeString());
											walletDao.Save(fincWallet);
											// 添加提现记录
											TradeLog tradeLog = new TradeLog();
											tradeLog.setFfinaWalletid(fincWallet.getFid());
											tradeLog.setFfinaId(finacing.getFid());
											tradeLog.setFuserId(fincWallet.getFuserId());
											tradeLog.setFamount(amount);
											tradeLog.setFtype("00302");
											tradeLog.setFcreateTime(Utils.getCurTimeString());
											tradeLog.setFlastUpdateTime(Utils.getCurTimeString());
											tradeDao.save(tradeLog);
										}
										// 活期理财
										else if (finacing.getFtype().equals("00101")) {
										}
										ProfitLog prlog = new ProfitLog();
										prlog.setFfinaWalletid(fincWallet.getFid());
										prlog.setFfinaId(finacing.getFid());
										prlog.setFuserid(fincWallet.getFuserId());
										prlog.setFamount(clearMoney);
										prlog.setFproportion(finacing.getFproportion());
										prlog.setFcreateTime(Utils.getCurTimeString());
										profitlogDAO.Save(prlog);
										System.out.println("结算成功");
									}
								} catch (Exception e) {
									System.out.println("异常错误");
								}
							}
						}
					}
				} catch (NumberFormatException e) {
				}

			}
		}
		return true;
	}

	@Override
	public void save(Finacing finacing) {
		try {
			if (finacing.getFid() == null || "".equals(finacing.getFid())) {
				finacing.setFcreateTime(Utils.getCurTimeString());
				finacing.setFlastUpdateTime(Utils.getCurTimeString());
			} else {
				finacing.setFlastUpdateTime(Utils.getCurTimeString());
			}
			finacing.setFassetsType("00202");
			if (finacing.getFstatus() == null || "".equals(finacing.getFstatus())) {
				finacing.setFstatus("00502");
			} else {
				finacing.setFstatus("00501");
			}
			finacingDao.save(finacing);
			Finacing finacing1 = finacingDao.getByFname(finacing.getFname());
			List<Detail> detailList = finacing.getDetailList();
			for (Detail detail : detailList) {
				detail.setFfinaId(finacing1.getFid());
				detailDao.save(detail);
			}
		} catch (Exception e) {
			System.out.println("保存失败");
		}

	}

	@Override
	public Finacing getById(String fid) {
		Finacing result = null;
		try {
			result = finacingDao.getById(fid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void del(String fids) {
		try {
			finacingDao.del(fids);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public List<FinacingRead> toList(int currentPage, int pageSize) {
		int firstResult = (currentPage - 1) * pageSize;
		Date date = new Date();
		String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		String filter = " where a.fstatus = '00501' "
				+ " AND str_to_date(fbeginTime,'%Y-%m-%d %H:%i:%s')< '"+format
				+ "' AND str_to_date(fendTime,'%Y-%m-%d %H:%i:%s')> '"+format
				+ "' order by a.fproportion desc";// 已经发布按照利率排序
		//return finacingDao.toList(firstResult, pageSize, filter);
		return finacingDao.get(currentPage,pageSize,filter);
	}

	@Override
	public FinacingRead getByFid(String fid) {
		FinacingRead result = null;
		try {
			result = finacingDao.getByFid(fid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int findTount() {
		return finacingDao.findTount();
	}

	@Override
	public Fvirtualwallet findVirtualWallet(String fid, String fvitypeId) {
		Fvirtualwallet fvirtualwallet = null;
		fvirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(fid, fvitypeId);
		return fvirtualwallet;
	}

}
