package com.ruizton.main.auto;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.ValidateMessageStatusEnum;
import com.ruizton.main.model.Fvalidatemessage;
import com.ruizton.main.service.front.FrontSystemArgsService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.util.ConstantKeys;
import com.ruizton.util.MessagesUtils;
import com.ruizton.util.Utils;

public class AutoSendMessage extends TimerTask {

	@Autowired
	private TaskList taskList ;
	@Autowired
	private FrontSystemArgsService frontSystemArgsService ;
	@Autowired
	private FrontValidateService frontValidateService ;
	
//	@Autowired
//	private LendRealTimeData lendRealTimeData;
//	@Autowired
//	private LendSystemArgsService lendSystemArgsService;
	

	private static final Logger log = LoggerFactory.getLogger(AutoSendMessage.class);
	@Override
	public void run() {
		
		try{
		// TODO Auto-generated method stub
		String id = this.taskList.getOneMessage() ;		
		if(id!=null){
			Fvalidatemessage fvalidatemessage = this.frontValidateService.findFvalidateMessageById(id) ;
			if(fvalidatemessage==null){
				return ;
			}
			
			String retCode;
			try {
				/*
				retCode = MessagesUtils.send(this.frontSystemArgsService.getSystemArgs(ConstantKeys.MESSAGE_NAME).trim(),
						this.frontSystemArgsService.getSystemArgs(ConstantKeys.MESSAGE_PASSWORD).trim(),
						this.frontSystemArgsService.getSystemArgs(ConstantKeys.MESSAGE_KEY).trim(),
						fvalidatemessage.getFphone(), fvalidatemessage.getFcontent(),fvalidatemessage.getFid().toString(),"");
				*/
				MessagesUtils.sendM5C(this.frontSystemArgsService.getSystemArgs(ConstantKeys.MESSAGE_NAME).trim(),
						this.frontSystemArgsService.getSystemArgs(ConstantKeys.MESSAGE_PASSWORD).trim(),
						this.frontSystemArgsService.getSystemArgs(ConstantKeys.MESSAGE_KEY).trim(), fvalidatemessage.getFcontent(), fvalidatemessage.getFphone());
				
				fvalidatemessage.setFsendTime(Utils.getTimestamp()) ;
				fvalidatemessage.setFstatus(ValidateMessageStatusEnum.Send) ;
				this.frontValidateService.updateFvalidateMessage(fvalidatemessage) ;
				
				/*System.out.println(fvalidatemessage.getFcontent());*/
			} catch (Exception e) {
				taskList.returnMessageList(id) ;
				log.error("短信验证码发送失败"+e.getMessage());
				//e.printStackTrace();
				
			}
		}
		}catch (Exception e) {		
			log.error("短信验证码发送失败"+e.getMessage());
			//e.printStackTrace();
			
		}
		
//		//P2P短信
//		Fvalidatemessage fvalidatemessage = lendRealTimeData.getOneMessage();
//		if(fvalidatemessage != null){
//			String name = this.lendSystemArgsService.findById(FlendSystemargsIds.MSG_NAME).getFvalue();
//			String password = this.lendSystemArgsService.findById(FlendSystemargsIds.MSG_PASSWORD).getFvalue();
//			NewMessagesUtils.send(name, password, fvalidatemessage.getFphone(), fvalidatemessage.getFcontent());
//			fvalidatemessage.setFsendTime(Utils.getTimestamp()) ;
//			fvalidatemessage.setFstatus(ValidateMessageStatusEnum.Send) ;
//			this.frontValidateService.updateFvalidateMessage(fvalidatemessage) ;
//		}
	}
}
