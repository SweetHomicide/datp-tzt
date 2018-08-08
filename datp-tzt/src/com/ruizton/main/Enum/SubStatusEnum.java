package com.ruizton.main.Enum;

public class SubStatusEnum {
    public static final int INIT = 1;//
    public static final int YES = 2;//
    public static final int NO = 3;//
    
    public static String getEnumString(int value) {
		String name = "";
		if(value == INIT){
			name = "等待抽签";
		}else if(value == YES){
			name = "众筹成功";
		}else if(value == NO){
			name = "众筹失败";
		}
		return name;
	}
    
}
