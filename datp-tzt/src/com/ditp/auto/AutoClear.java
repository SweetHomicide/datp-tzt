package com.ditp.auto;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ditp.service.FinacingService;

/**
 * 理财自动结算
 * @author Thinkpad
 *
 */
@Component("taskJob")
public class AutoClear {
	@Autowired
	private FinacingService finacingService;
/**
 * 1、查询 理财列表 tb_fina_finacing  状态是1的 为对象 listFina
 * 轮询fina 判断 fina 是灵活存取还是固定结算 查询数据库 钱包表 为listwallet
 * 轮询listwallet 来结算 * 利息率 增加到对应虚拟币id的钱包
 */
	/**
	 * 结算
	 * @throws ParseException 
	 */
	@Scheduled(cron ="0 0 8 ? * *")
	//@Scheduled(cron ="0 0-59 14 * * ?")
	//@Scheduled(cron="0/60 * *  * * ? ")   //每5秒执行一次
	public void Clear() throws ParseException
	{
		boolean isClear=finacingService.autoClear();
		System.out.println("定时执行-----------------------------------");
	}
}
