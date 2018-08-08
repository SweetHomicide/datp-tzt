package com.ruizton.main.Enum;



public class PayCodeStatusEnum {
    public static final int PAYCODE_CREATE = 1;//创建
    public static final int PAYCODE_USER_CONFIRM = 2 ;//用户确认
    public static final int PAYCODE_SUCCESS = 3;//充值成功
    public static final int PAYCODE_FAILURE = 4;//充值失败
    public static final int PAYCODE_CANCEL = 5;//充值失败
    
    public static String getEnumString(int value) {
		String name = "";
		if(value == PAYCODE_CREATE){
			name = "创建";
		}else if(value == PAYCODE_USER_CONFIRM){
			name = "用户确认";
		}else if(value == PAYCODE_SUCCESS){
			name = "充值成功";
		}else if(value == PAYCODE_FAILURE){
			name = "充值失败";
		}else if(value == PAYCODE_CANCEL){
			name = "作废";
		}
		return name;
	}
    
}
