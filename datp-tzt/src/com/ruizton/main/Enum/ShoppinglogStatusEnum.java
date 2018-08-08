package com.ruizton.main.Enum;

public class ShoppinglogStatusEnum {
    public static final int PAY = 1;
    public static final int USED = 2;
    public static final int SEND = 3;
    public static final int CHARGE = 4;
    public static final int CANCEL = 5;
    
    public static String getEnumString(int value) {
		String name = "";
		if(value == PAY){
			name = "已付款";
		}else if(value == USED){
			name = "已使用";
		}else if(value == SEND){
			name = "已发货";
		}else if(value == CHARGE){
			name = "已结算";
		}else if(value == CANCEL){
			name = "用户取消";
		}
		return name;
	}
    
}
