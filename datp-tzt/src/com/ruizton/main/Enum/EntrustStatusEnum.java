package com.ruizton.main.Enum;

public class EntrustStatusEnum {
    public static final int Going = 1;//未成交
    public static final int PartDeal = 2 ;//部分成交
    public static final int AllDeal = 3;//完全成交
    public static final int Cancel = 4;//撤销
    
    public static String getEnumString(int value) {
		String name = "";
		if(value == Going){
			name = "未成交";
		}else if(value == PartDeal){
			name = "部分成交";
		}else if(value == AllDeal){
			name = "完全成交";
		}else if(value == Cancel){
			name = "用户撤销";
		}
		return name;
	}
    
}
