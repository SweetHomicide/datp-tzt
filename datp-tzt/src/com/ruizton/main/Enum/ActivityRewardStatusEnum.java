package com.ruizton.main.Enum;

public class ActivityRewardStatusEnum {
	public static final int NOT_REWARD = 1;//没发奖品
    public static final int REWARD = 2;//已发奖励
    public static final int REJECT = 3;//拒绝
    
    
    public static String getEnumString(int value) {
		String name = "";
		switch (value) {
		case NOT_REWARD:
			name = "待审核";
			break;
		case REWARD:
			name = "已发奖励";
			break;
		case REJECT:
			name = "拒绝";
			break;
		}
		return name;
	}
    
}
