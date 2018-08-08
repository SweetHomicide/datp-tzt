package com.ruizton.main.Enum;

public class ActivityCompleteStatusEnum {
	public static final int NOT_GET_REWARD = 1;//
    public static final int GET_REWARD = 2;//
    
    public static String getEnumString(int value) {
		String name = "";
		switch (value) {
		case NOT_GET_REWARD:
			name = "未领奖";
			break;
		case GET_REWARD:
			name = "已领奖";
			break;
		}
		return name;
	}
    
}
