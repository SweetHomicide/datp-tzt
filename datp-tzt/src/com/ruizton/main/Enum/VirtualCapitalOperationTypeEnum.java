package com.ruizton.main.Enum;

//资金操作类型
public class VirtualCapitalOperationTypeEnum {
	public static final int COIN_IN = 1 ;
	public static final int COIN_OUT = 2 ;
	
	public static String getEnumString(int value) {
		String name = "";
		if(value == COIN_IN){
			name = "数字资产充值";
		}else if(value == COIN_OUT){
			name = "数字资产提现";
		}
		return name;
	}
}
