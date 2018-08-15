package com.ruizton.main.Enum;

/**
 * @author   Dylan
 * @data     2018年8月14日
 * @typeName CoinTypeEnum
 * 说明 ：虚拟币类型
 *
 */
public class CoinTypeEnum {
    public static final int A_VALUE = 1;//正常
    
    public static String getEnumString(int value) {
		String name = "";
		if(value == A_VALUE){
			name = "虚拟币区";
		}
		return name;
	}
    
}
