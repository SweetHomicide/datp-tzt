package com.ruizton.main.Enum;

public class WithdrawalsEnum {
	 	public static final int Type1 = 1;//银行卡线下提现
	    public static final int Type2 = 2;//银行卡线上提现
	   
	    public static String getEnumString(int value) {
			String name = "";
			if(value == Type1){
				name = "银行卡线下提现";
			}else if(value == Type2){
				name = "银联线上提现";
			}
			return name;
		}
}
