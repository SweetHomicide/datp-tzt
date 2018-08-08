package com.ruizton.main.Enum;

public class VirtualCapitalOperationOutStatusEnum {
	public static final int WaitForOperation = 1 ;//等待提现
	public static final int OperationLock = 2 ;//锁定，正在处理
	public static final int OperationSuccess = 3 ;//提现成功
	public static final int Cancel = 4 ;//用户取消
	public static final int fail = 5 ;//用户取消
	public static String getEnumString(int value) {
		String name = "";
		if(value == WaitForOperation){
			name = "等待转出";
		}else if(value == OperationLock){
			name = "正在处理";
		}else if(value == OperationSuccess){
			name = "转出成功";
		}else if(value == Cancel){
			name = "用户撤销" ;
		}
		else if(value == fail){
			name = "转出失败" ;
		}
		return name;
	}
}
