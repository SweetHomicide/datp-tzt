package com.ruizton.main.Enum;

public class SubscriptionTypeEnum {
	public static final int RMB = 1;//
    public static final int COIN = 2;//
    public static final int MEADOW = 3;//
    public static final int SMELTER = 4;//
    
    public static String getEnumString(int value) {
		String name = "";
		switch (value) {
		case RMB:
			name = "人民币认购";
			break;
		case COIN:
			name = "虚拟币换购";
			break;
		case MEADOW:
			name = "牧场认购";
			break;
		case SMELTER:
			name = "熔炉认购";
			break;	
		}
		return name;
	}
    
}
