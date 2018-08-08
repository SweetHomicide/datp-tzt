package com.ruizton.main.Enum;

public class ActivityTypeEnum {
	public static final int REGISTER = 1;//
    public static final int LOGIN = 2;//
    public static final int ACTIVE_MAIL = 3;//
    public static final int REAL_NAME = 4;//
    public static final int BINNDING_MOBIL = 5;//
    public static final int BINNDING_GOOGLE = 6;//
    public static final int RECHARGE_CNY = 7;//
    public static final int NORMAL_ACTIVITY = 8;//
    
    public static String getEnumString(int value) {
		String name = "";
		switch (value) {
		case REGISTER:
			name = "注册";
			break;
		case LOGIN:
			name = "登陆";
			break;
		case ACTIVE_MAIL:
			name = "激活邮箱";
			break;
		case REAL_NAME:
			name = "实名认证";
			break;
		case BINNDING_MOBIL:
			name = "绑定手机";
			break;
		case BINNDING_GOOGLE:
			name = "绑定谷歌";
			break;
		case RECHARGE_CNY:
			name = "人民币充值";
			break;
		case NORMAL_ACTIVITY:
			name = "普通活动";
			break;
		}
		return name;
	}
    
}
