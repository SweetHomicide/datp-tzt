package com.ruizton.util;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.VirtualCapitalOperationInStatusEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationTypeEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.auto.RechargeBtcData;
import com.ruizton.main.model.BTCInfo;
import com.ruizton.main.model.BTCMessage;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualaddress;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;

public class AutoRechargeBtcCome {
	private static final Logger log = LoggerFactory
			.getLogger(AutoRechargeBtcCome.class);
	@Autowired
	private RechargeBtcData rechargeBtcData ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private VirtualCoinService virtualCoinService ;
	@Autowired
	private FrontUserService frontUserService ;
	private boolean m_sync_flag = false ;
	
	public void work() {
		synchronized (this) {
				try{
//					System.out.print("================准备读到账。。。。");
					//遍历现有的
					String filter = "where fstatus=1 and FIsWithDraw=1";
					List<Fvirtualcointype> coins = this.virtualCoinService.list(0, 0, filter, false);
					//获取钱包中新数据
					for (Fvirtualcointype fvirtualcointype : coins) {
						String id = fvirtualcointype.getFid();
							try{

								if(fvirtualcointype==null || fvirtualcointype.getFstatus()==VirtualCoinTypeStatusEnum.Abnormal){
									continue ;
								}
								
								BTCMessage btcMessage = new BTCMessage() ;
								btcMessage.setACCESS_KEY(fvirtualcointype.getFaccess_key()) ;
								btcMessage.setIP(fvirtualcointype.getFip()) ;
								btcMessage.setPORT(fvirtualcointype.getFport()) ;
								btcMessage.setSECRET_KEY(fvirtualcointype.getFsecrt_key()) ;
								
								BTCUtils btcUtils = new BTCUtils(btcMessage) ;
//								System.out.print("================开始读到账。。。。");
								String[] tradeNumbers = this.rechargeBtcData.getSubKeys(Integer.valueOf(id)) ;
								for (String tradeNo : tradeNumbers) {
									try {
										Fvirtualcaptualoperation fvirtualcaptualoperation = this.rechargeBtcData.subGet(Integer.valueOf(id), tradeNo) ;
										if(fvirtualcaptualoperation!=null){
											fvirtualcaptualoperation = this.frontVirtualCoinService.findFvirtualcaptualoperationById(fvirtualcaptualoperation.getFid()) ;
											if(fvirtualcaptualoperation.getFstatus()!=VirtualCapitalOperationInStatusEnum.SUCCESS){
												BTCInfo btcInfo = null ;
												try {
													btcInfo = btcUtils.gettransactionValue(fvirtualcaptualoperation.getFtradeUniqueNumber(), "receive") ;
												} catch (Exception e1) {
													e1.printStackTrace();
												}
												if(btcInfo==null){
													log.error("Fvirtualcaptualoperation:"+fvirtualcaptualoperation.getFid()+" cannot find in btcwallet!") ;
													continue ;
												}
//												System.out.print("================确认数。。。。"+btcInfo.getConfirmations());
												if(btcInfo.getConfirmations()>=0){
													fvirtualcaptualoperation.setFamount(btcInfo.getAmount());
													fvirtualcaptualoperation.setFconfirmations(btcInfo.getConfirmations()) ;
													switch (btcInfo.getConfirmations()) {
													case VirtualCapitalOperationInStatusEnum.WAIT_0:
														fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.WAIT_0) ;
														break;
													case VirtualCapitalOperationInStatusEnum.WAIT_1:
														fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.WAIT_1) ;
														break;
													case VirtualCapitalOperationInStatusEnum.WAIT_2:
														fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.WAIT_2) ;
														break;
													default:
														fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.SUCCESS) ;
														break;
													}
													fvirtualcaptualoperation.setFlastUpdateTime(Utils.getTimestamp()) ;
													try{
														this.frontVirtualCoinService.updateFvirtualcaptualoperationCoinIn(fvirtualcaptualoperation) ;
														if(fvirtualcaptualoperation.getFstatus()==VirtualCapitalOperationInStatusEnum.SUCCESS){
															this.rechargeBtcData.subRemove(id, tradeNo) ;
														}
													}catch(Exception e){
														e.printStackTrace() ;
													}
												}
												
											}else{
												this.rechargeBtcData.subRemove(id, tradeNo) ;
											}
										}else{
											this.rechargeBtcData.subRemove(id, tradeNo) ;
										}
									} catch (Exception e) {
										continue;
									}
					 			}
							
							}catch(Exception e){
								e.printStackTrace() ;
							}
							
						}
					
				}catch (Exception e) {
					e.printStackTrace() ;
				}
		
		}

		
	}
	
	
	

	
	
	
	
	
	
	
	
	
	//加密
	private static final String KEY_ALGORITHM = "AES";
	private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
	private static Key toKey(byte[] key) throws Exception{
		return new SecretKeySpec(key, KEY_ALGORITHM);
	}
	private static String encrypt(String data, String key) throws Exception{
		Key k = toKey(Base64.decodeBase64(key.getBytes()));                           //还原密钥
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);              //实例化Cipher对象，它用于完成实际的加密操作
		cipher.init(Cipher.ENCRYPT_MODE, k);                               //初始化Cipher对象，设置为加密模式
		return new String(Base64.encodeBase64(cipher.doFinal(data.getBytes()))); //执行加密操作。加密后的结果通常都会用Base64编码进行传输
	}
	private static String decrypt(String data, String key) throws Exception{
		Key k = toKey(Base64.decodeBase64(key.getBytes()));
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);                          //初始化Cipher对象，设置为解密模式
		return new String(cipher.doFinal(Base64.decodeBase64(data.getBytes()))); //执行解密操作
	}
}
