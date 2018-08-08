package com.ruizton.main.Enum;

//资金操作类型
public class CapitalOperationTypeEnum {
	public static final int RMB_IN = 1 ;
	public static final int RMB_OUT = 2 ;
	
	public static String getEnumString(int value) {
		String name = "";
		if(value == RMB_IN){
			name = "人民币充值";
		}else if(value == RMB_OUT){
			name = "人民币提现";
		}
		return name;
	}
}
