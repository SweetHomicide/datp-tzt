
package com.ruizton.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.AutotradeStatusEnum;
import com.ruizton.main.Enum.SynTypeEnum;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.model.Fautotrade;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Flimittrade;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.service.admin.AutotradeService;
import com.ruizton.main.service.admin.LimittradeService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.front.FrontTradeService;

public class AutoTrade {

	@Autowired
	private FrontTradeService frontTradeService;
	@Autowired
	private LimittradeService limittradeService;
	@Autowired
	private RealTimeData realTimeData;
	@Autowired
	private AutotradeService autotradeService;
	@Autowired
	private UserService userService;
	@Autowired
	private HttpServletRequest request;
	
	public void work() {
			try {
				Random random = new Random();
				try {
					int xx = 100000;
					int cc = Math.abs(random.nextInt(10));
					Thread.sleep(xx/cc);
				} catch (Exception e1) {}
				String filter = "where fstatus=" + AutotradeStatusEnum.NORMAL;
				List<Fautotrade> list = autotradeService.list(0, 0, filter,false);
				for (Fautotrade fautotrade : list) {
					Fuser fuser = this.userService.findById(fautotrade.getFuser().getFid());
					if(!Utils.openTrade(fautotrade.getFvirtualcointype())) continue;
					String symbol = fautotrade.getFvirtualcointype().getFid();
					int limited = 0;
					double buyPrice = this.realTimeData.getHighestBuyPrize(symbol);
					double sellPrice = this.realTimeData.getLowestSellPrize(symbol);
					double last = Math.abs(buyPrice-sellPrice);//买入价和卖出价的差值绝对值
					
					double q1 =Math.random()*(fautotrade.getFmaxqty()-fautotrade.getFminqty());//最大数量-最小数量，差值随机数量
					double qty = Utils.getDouble(q1+fautotrade.getFminqty(),4);//差值随机数量+最小数量，取4为小数的随机交易数量
					double p1 = Math.random()*(fautotrade.getFmaxprice()-fautotrade.getFminprice())+fautotrade.getFminprice();//p1：允许范围内的随机购买价：最大浮动价-最小浮动价取随机值+最小买入价
					double price =Utils.getDouble(p1+buyPrice, fautotrade.getFvirtualcointype().getFcount());//价格为浮动价格+最高买入价，取相应小数为。
					
					TreeSet<Fentrust> buyMap = this.realTimeData.getBuyDepthMap(symbol);
					TreeSet<Fentrust> sellMap = this.realTimeData.getBuyDepthMap(symbol);
					
					if(qty <=0) continue;
					if(price <=0) continue;
					if(fautotrade.getFsynType() == SynTypeEnum.OKCOIN_BTC_VALUE){
						double ok = getOkcoinPrice("btc_cny", fautotrade.getFvirtualcointype().getFcount());
						if(ok >0){
							price = ok;
						}
//						System.out.println("**********OKCOIN-BTC********"+price);
					}else if(fautotrade.getFsynType() == SynTypeEnum.OKCOIN_LTC_VALUE){
						double ok = getOkcoinPrice("ltc_cny", fautotrade.getFvirtualcointype().getFcount());//最新成交价
						if(ok >0){
							price = ok;
						}
//						System.out.println("**********OKCOIN-LTC********"+price);
					}
					
					if(!((buyMap == null || buyMap.isEmpty() || buyMap.size() ==0)&&(sellMap == null || sellMap.isEmpty() || sellMap.size() ==0))){
						if(last <=0) continue;
						if(price <= buyPrice || price >= sellPrice) continue;//购买价小于等于买入价或者差价小于等于卖出价，没有优先级
//						System.out.println("**********"+symbol+"********"+price);
					}
					
					Flimittrade limittrade = this.isLimitTrade(symbol);
					double upPrice = 0d;
					double downPrice = 0d;
					if(limittrade != null){
						upPrice = Utils.getDouble(limittrade.getFupprice()+limittrade.getFupprice()*limittrade.getFpercent(), fautotrade.getFvirtualcointype().getFcount());
						downPrice = Utils.getDouble(limittrade.getFdownprice()-limittrade.getFdownprice()*limittrade.getFpercent(), fautotrade.getFvirtualcointype().getFcount());
						if(downPrice <0) downPrice=0;
						if(price > upPrice){
							continue;
						}
						if(price < downPrice){
							continue; 
						}
					}
					
					try {
						if(random.nextInt(10) >= 5){
							Fentrust fentrust1 = null ;
							if(true){
								boolean flag = false ;
								
								try {
									fentrust1 = this.frontTradeService.updateEntrustBuy(symbol, qty, price, fuser, limited==1) ;
									flag = true ;
								} catch (Exception e) {
									e.printStackTrace();
								}
								if(flag && fentrust1 != null){
									fentrust1 = this.frontTradeService.findFentrustById(fentrust1.getFid()) ;
									this.realTimeData.addEntrustBuyMap(symbol, fentrust1);
								}
							}
							Fentrust fentrust2 = null ;
							if(true && fentrust1 != null){
								boolean flag = false ;
								
								try {
									fentrust2 = this.frontTradeService.updateEntrustSell(symbol, qty, price,fuser, limited==1) ;
									flag = true ;
								} catch (Exception e) {
									e.printStackTrace();
								}
								if(flag && fentrust2 != null){
									fentrust2 = this.frontTradeService.findFentrustById(fentrust2.getFid()) ;
									this.realTimeData.addEntrustSellMap(symbol, fentrust2);
								}else{
									try {
										this.frontTradeService.updateCancelFentrust(fentrust1, fentrust1.getFuser()) ;
										this.realTimeData.removeEntrustBuyMap(fentrust1.getFvirtualcointype().getFid(), fentrust1) ;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}else{
							Fentrust fentrust2 = null ;
							if(true){
								boolean flag = false ;
								
								try {
									fentrust2 = this.frontTradeService.updateEntrustSell(symbol, qty, price,fuser, limited==1) ;
									flag = true ;
								} catch (Exception e) {
									e.printStackTrace();
								}
								if(flag && fentrust2 != null){
									fentrust2 = this.frontTradeService.findFentrustById(fentrust2.getFid()) ;
									this.realTimeData.addEntrustSellMap(symbol, fentrust2);
								}
							}
							
							Fentrust fentrust1 = null ;
							if(true && fentrust2 != null){
								boolean flag = false ;
								
								try {
									fentrust1 = this.frontTradeService.updateEntrustBuy(symbol, qty, price, fuser, limited==1) ;
									flag = true ;
								} catch (Exception e) {
									e.printStackTrace();
								}
								if(flag && fentrust1 != null){
									fentrust1 = this.frontTradeService.findFentrustById(fentrust1.getFid()) ;
									this.realTimeData.addEntrustBuyMap(symbol, fentrust1);
								}else{
									try {
										this.frontTradeService.updateCancelFentrust(fentrust2, fentrust2.getFuser()) ;
										this.realTimeData.removeEntrustSellMap(fentrust2.getFvirtualcointype().getFid(), fentrust2) ;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public Flimittrade isLimitTrade(String vid) {
		Flimittrade flimittrade = null;
		String filter = "where fvirtualcointype.fid='"+vid+"'";
		List<Flimittrade> flimittrades = this.limittradeService.list(0, 0, filter, false);
		if(flimittrades != null && flimittrades.size() >0){
			flimittrade = flimittrades.get(0);
		}
		return flimittrade;
	}
	
	private double getOkcoinPrice(String symbol,int count) {
		/**
		 * 请求参数
			参数名	参数类型	必填	描述
			symbol 	String 	否(默认btc_cny)btc_cny:比特币    ltc_cny :莱特币
		 */
		/**
		 *  返回值
		 *  date: 返回数据时服务器时间
			buy: 买一价
			high: 最高价
			last: 最新成交价
			low: 最低价
			sell: 卖一价
			vol: 成交量(最近的24小时)
		 */
		double last = 0d;
		try {
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
			URL url1 = new URL("https://www.okcoin.cn/api/ticker.do?symbol="+symbol) ;
			BufferedReader br = new BufferedReader(new InputStreamReader( url1.openStream()) ) ;
			StringBuffer sb = new StringBuffer() ;
			String tmp = null ;
			while((tmp=br.readLine())!=null){
				sb.append(tmp) ;
			}
			
			Map ltc = ((Map)JSONObject.fromObject(sb.toString()).get("ticker"));
			last = Utils.getDouble(Double.valueOf(ltc.get("last").toString()), count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return last;
	}
	
	HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session) {
            System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                               + session.getPeerHost());
            return true;
        }
    };
	
	private void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());
	}

	 class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}
}
