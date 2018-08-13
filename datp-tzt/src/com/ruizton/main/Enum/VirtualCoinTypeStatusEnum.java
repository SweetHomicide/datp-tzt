package com.ruizton.main.Enum;

/**
 * @author   Dylan
 * @data     2018年8月14日
 * @typeName VirtualCoinTypeStatusEnum
 * 说明 ：  币种类型状态
 *		Normal   = 1;//正常
 *		Abnormal = 2;//禁用 
 */
public class VirtualCoinTypeStatusEnum {
    public static final int Normal = 1;//正常
    public static final int Abnormal = 2;//禁用
    
    public static String getEnumString(int value) {
		String name = "";
		if(value == Normal){
			name = "正常";
		}else if(value == Abnormal){
			name = "禁用";
		}
		return name;
	}
    
}
