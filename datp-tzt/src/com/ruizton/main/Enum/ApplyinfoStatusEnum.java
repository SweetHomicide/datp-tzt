package com.ruizton.main.Enum;

public class ApplyinfoStatusEnum {
    public static final int WAIT = 1;//未委托
    public static final int SUCCESS = 2 ;//已委托
    public static final int REJECT = 3;//取消

 //  有效用户； 金豆初级玩家（个数），金豆资深玩家（个数），金豆圈子成员（个数），金豆圈子领袖（个数）
    public static String getEnumString(int value) {
		String name = "";
		if(value == WAIT){
			name = "等待审核";
		}else if(value == SUCCESS){
			name = "审核通过";
		}else if(value == REJECT){
			name = "审核不通过";
		}
		return name;
	}
    
}
