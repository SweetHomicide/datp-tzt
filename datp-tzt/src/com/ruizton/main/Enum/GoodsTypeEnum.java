package com.ruizton.main.Enum;

public class GoodsTypeEnum {
    public static final int type1 = 1;
    public static final int type2 = 2;
    public static final int type3 = 3;
    public static final int type4 = 4;
    public static final int type5 = 5;
    public static final int type6 = 6;
            
    public static String getEnumString(int value) {
		String name = "";
		if(value == type1){
			name = "电子产品";
		}else if(value == type2){
			name = "衣服裤子";
		}else if(value == type3){
			name = "生活用品";
		}else if(value == type4){
			name = "化妆用品";
		}else if(value == type5){
			name = "食品类";
		}else if(value == type6){
			name = "酒类";
		}
		return name;
	}
    
}
