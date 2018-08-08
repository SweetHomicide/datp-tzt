package com.ruizton.main.controller.front;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.ruizton.util.Constant;

import net.sf.json.JSONObject;


public class ApiConstant {
	public static HashMap<String, Integer> actionMap = new HashMap<String, Integer>() ;
	static {
		int i=0 ;
		actionMap.put("Invalidate", i++);// 无效
		
		actionMap.put("UserLogin", i++);// 登录
		actionMap.put("UserRegister", i++); // 注册
		actionMap.put("SendMessageCode", i++);
		actionMap.put("SendMailCode", i++);
		actionMap.put("FindLoginPassword", i++);
		
		actionMap.put("GetCoinListInfo", i++);// 每个币种的基础信息
		actionMap.put("GetVersion", i++);// 获取服务端最小版本号
		actionMap.put("GetMarketData", i++);// 行情
		actionMap.put("GetNews", i++);// 新闻
		actionMap.put("GetMyCenter", i++);// 个人中心
		actionMap.put("GetAbout", i++);// 个人中心
		actionMap.put("GetMarketDepth", i++);// 市场深度
		actionMap.put("RechargeBanks", i++);//充值银行列表
		actionMap.put("rechargeCnySubmit", i++);//银行卡充值修改状态
		actionMap.put("IsShiel", i++);//是否屏蔽测试版本
		actionMap.put("GetZhongChouList", i++);//查看众筹信息
		actionMap.put("exchangeAppAPI", i++);//兑换首页列表
		actionMap.put("isOpen", i++);//读取是否隐藏配置文件
		actionMap.put("FinacingView", i++);//理财产品列表
		//actionMap.put("GetFacingById", i++);//根据id获取理财信息
		actionMap.put("productIntroList", i++);//产品信息
		actionMap.put("getProduct", i++);//获取产品信息详情
		
		
		
		// 需要token
		i = 201 ;
		actionMap.put("GetAccountInfo", i++); // 账号信息，用户信息+交易账号+放款账号信息
		actionMap.put("GetBtcRechargeListRecord", i++);// 虚拟币充值，返回充值地址和记录
		actionMap.put("GetEntrustInfo", i++);// 委托订单
		actionMap.put("CancelEntrust", i++);// 取消订单
		actionMap.put("BtcTradeSubmit", i++);// 下单
		actionMap.put("TradePassword", i++);// 交易密码
		actionMap.put("GetIntrolinfo", i++);// 提成明细
		actionMap.put("GetIntrolDetail", i++);

		actionMap.put("ViewValidateIdentity", i++);// 查看认证信息

		actionMap.put("ValidateIdentity", i++);
		
		actionMap.put("RechargeCny", i++);
		
		actionMap.put("bindPhone", i++);
		actionMap.put("UnbindPhone", i++);
		actionMap.put("ChangebindPhone", i++);
		
		actionMap.put("GetWithdrawBankList", i++);
		actionMap.put("SetWithdrawCnyBankInfo", i++);
		actionMap.put("WithDrawCny", i++);
		actionMap.put("GetWithdrawBtcAddress", i++);
		actionMap.put("SetWithdrawBtcAddr", i++);
		actionMap.put("WithdrawBtcSubmit", i++);
		actionMap.put("changePassword", i++);
		actionMap.put("GetAllRecords", i++);
		actionMap.put("GetZhongChouResource", i++);//获取单个众筹详细信息
		actionMap.put("GetZhongChouLogs", i++);//获取众筹历史记录
		actionMap.put("nowCrowd", i++);//立即众筹
		actionMap.put("exchange", i++);//兑换操作页面
		actionMap.put("submitEx", i++);//提交兑换
		actionMap.put("getExLogs", i++);//获取历史记录
		actionMap.put("MyFincingView", i++);//获取我的理财
		actionMap.put("getDetailsFinacing", i++);//查看理财产品详情
		actionMap.put("buyRecord", i++);//查看财务中心中产品购买记录
		actionMap.put("ProfitLogView", i++);//查询 收益页面
		actionMap.put("getFacingById", i++);//购买页面
		actionMap.put("GetFacingById", i++);
		actionMap.put("getUnread", i++);//查询未读取站内信的数量
		actionMap.put("staMailSearch", i++);//站内信分页查询
		actionMap.put("staMailget", i++);//站内信查看详情
		actionMap.put("staMaildel", i++);//站内信批量删除
		actionMap.put("buyFinacing", i++);
		actionMap.put("kitingMoney", i++);
		actionMap.put("kitingView", i++);
		
	};


	public synchronized static Integer getInteger(String action) {

		try {
			Integer actionInteger = actionMap.get(action);

			if (actionInteger == null) {
				actionInteger = 0;
			}

			return actionInteger;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static int getInt(String param) {
		int ret = 0;
		try {
			ret = Integer.parseInt(param.trim());
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return ret;
	}

	public static double getDouble(String param) {
		double ret = 0;
		try {
			ret = Double.parseDouble(param.trim());
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return ret;
	}

	public static String getUnknownError(Exception e) {
		if(e != null){
			e.printStackTrace();
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.accumulate(APP_API_Controller.Result, true);
		jsonObject.accumulate(APP_API_Controller.ErrorCode, -10003);// 未知错误
		jsonObject.accumulate(APP_API_Controller.Value, "网络错误，请稍后重试");// 未知错误

		return jsonObject.toString();
	}
}