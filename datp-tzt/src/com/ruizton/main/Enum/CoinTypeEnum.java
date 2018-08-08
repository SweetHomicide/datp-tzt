package com.ruizton.main.Enum;

public class CoinTypeEnum {
    public static final int A_VALUE = 1;//正常
    
    public static String getEnumString(int value) {
		String name = "";
		if(value == A_VALUE){
			name = "虚拟币区";
		}
		return name;
	}
    
}
