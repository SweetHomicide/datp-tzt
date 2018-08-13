package com.ruizton.main.Enum;

/**
 * 验证码发送状态
 * @author   Dylan
 * @data     2018年8月13日
 * @typeName ValidateMessageStatusEnum
 * 说明 ： 
 *		1 为 未发送
 *		2 为 已发送
 */
public class ValidateMessageStatusEnum {
    public static final int Not_send = 1;//未验证
    public static final int Send = 2;//已验证
    
    public static String getEnumString(int value) {
		String name = "";
		if(value == Not_send){
			name = "未发送";
		}else if(value == Send){
			name = "已发送";
		}
		return name;
	}
    
}
