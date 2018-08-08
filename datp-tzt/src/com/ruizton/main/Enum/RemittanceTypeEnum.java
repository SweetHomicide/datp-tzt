package com.ruizton.main.Enum;

public class RemittanceTypeEnum {//汇款方式：柜台、网银、atm
    public static final int Type1 = 0;//柜台 转款
    public static final int Type2 = 2;//银联支付//ATM机
    public static final int Type3 = 3;//支付宝//网上银行
    public static final int Type4 = 4;//微信//网上银行
   
    public static String getEnumString(int value) {
		String name = "";
		if(value == Type1){
			name = "银行卡转账支付";
		}else if(value == Type2){
			name = "银联在线支付";
		}else if(value == Type3){
			name = "支付宝支付";
		}else if(value == Type4){
			name = "微信支付";
		}
		return name;
	}
}
