package com.ruizton.main.Enum;

public class ActivityTypeTypeEnum {
    public static final int TRADE = 1;//
    public static final int WITHDRAW = 2;//
    
    public static String getEnumString(int value) {
		String name = "";
		if(value == TRADE){
			name = "交易";
		}else if(value == WITHDRAW){
			name = "充值";
		}
		return name;
	}
    
}
