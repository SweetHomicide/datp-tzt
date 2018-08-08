package com.ruizton.main.service.front;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.Enum.BankInfoStatusEnum;
import com.ruizton.main.Enum.BankInfoWithdrawStatusEnum;
import com.ruizton.main.Enum.LogTypeEnum;
import com.ruizton.main.Enum.SendMailTypeEnum;
import com.ruizton.main.Enum.UserGradeEnum;
import com.ruizton.main.Enum.ValidateMailStatusEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.auto.TaskList;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.ValidateMap;
import com.ruizton.main.dao.EmailvalidateDAO;
import com.ruizton.main.dao.FapiDAO;
import com.ruizton.main.dao.FbankinfoDAO;
import com.ruizton.main.dao.FbankinfoWithdrawDAO;
import com.ruizton.main.dao.FintrolinfoDAO;
import com.ruizton.main.dao.FlogDAO;
import com.ruizton.main.dao.FmessageDAO;
import com.ruizton.main.dao.FpoolDAO;
import com.ruizton.main.dao.FscoreDAO;
import com.ruizton.main.dao.FsystemargsDAO;
import com.ruizton.main.dao.FtransportlogDAO;
import com.ruizton.main.dao.FuserDAO;
import com.ruizton.main.dao.FusersettingDAO;
import com.ruizton.main.dao.FvalidateemailDAO;
import com.ruizton.main.dao.FvirtualaddressDAO;
import com.ruizton.main.dao.FvirtualaddressWithdrawDAO;
import com.ruizton.main.dao.FvirtualcointypeDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.model.BTCMessage;
import com.ruizton.main.model.Emailvalidate;
import com.ruizton.main.model.Fapi;
import com.ruizton.main.model.Fbankinfo;
import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Flog;
import com.ruizton.main.model.Fmessage;
import com.ruizton.main.model.Fpool;
import com.ruizton.main.model.Fscore;
import com.ruizton.main.model.Fsystemargs;
import com.ruizton.main.model.Ftransportlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fusersetting;
import com.ruizton.main.model.Fvalidateemail;
import com.ruizton.main.model.Fvirtualaddress;
import com.ruizton.main.model.FvirtualaddressWithdraw;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.model.Fwebbaseinfo;
import com.ruizton.main.service.BaseService;
import com.ruizton.util.Constant;
import com.ruizton.util.ConstantKeys;
import com.ruizton.util.Utils;

@Service
public class FrontUserService extends BaseService {
	private static final Logger log = LoggerFactory.getLogger(FrontUserService.class);
	@Autowired
	private FuserDAO fuserDAO ;
	@Autowired
	private EmailvalidateDAO emailvalidateDAO ;
	@Autowired
	private FvalidateemailDAO validateemailsDAO ;
	@Autowired
	private FwalletDAO fwalletDAO ;
	@Autowired
	private TaskList taskList ;
	@Autowired
	private FbankinfoDAO fbankinfoDAO ;
	@Autowired
	private FscoreDAO fscoreDAO ;
	@Autowired
	private FvirtualcointypeDAO fvirtualcointypeDAO ;
	@Autowired
	private FvirtualwalletDAO fvirtualwalletDAO ;
	@Autowired
	private FvirtualaddressDAO fvirtualaddressDAO ;
	@Autowired
	private FvirtualaddressWithdrawDAO fvirtualaddressWithdrawDAO ;
	@Autowired
	private FbankinfoWithdrawDAO fbankinfoWithdrawDAO ;
	@Autowired
	private FsystemargsDAO fsystemargsDAO ;
	@Autowired
	private FapiDAO fapiDAO ;
	@Autowired
	private ValidateMap validateMap ;
	@Autowired
	private FlogDAO flogDAO ;
	@Autowired
	private FpoolDAO fpoolDAO ;
	@Autowired
	private FusersettingDAO fusersettingDAO ;
	@Autowired
	private ConstantMap constantMap ;
	@Autowired
	private FintrolinfoDAO introlinfoDAO;
	@Autowired
	private FmessageDAO fmessageDAO;
	@Autowired
	private FtransportlogDAO transportlogDAO ;
	
	public boolean nickValidated(String name) throws Exception {
		boolean flag = false ;
		if(name!=null && !name.trim().equals("")){
			List<Fuser> list = fuserDAO.findByProperty("floginName", name) ;
			if(list.size()>0){
				flag = true ;
			}
		}
		return flag ;
	}
	
	public boolean saveRegister(Fuser fuser) throws Exception{
		try {
			this.fuserDAO.save(fuser) ;
			
			//用户基本设置信息表
			Fusersetting fusersetting = new Fusersetting() ;
			fusersetting.setFisAutoReturnToAccount(false) ;
			fusersetting.setFuser(fuser) ;
			fusersetting.setFticketQty(0d);
			fusersetting.setFsendDate(null);
			fusersetting.setFissend(false);
			this.fusersettingDAO.save(fusersetting) ;
			fuser.setFusersetting(fusersetting) ;
			
			//初始化钱包
			Fwallet fwallet = new Fwallet() ;
			fwallet.setFtotalRmb(0F) ;
			fwallet.setFfrozenRmb(0F) ;
			fwallet.setFlastUpdateTime(Utils.getTimestamp()) ;
			this.fwalletDAO.save(fwallet) ;
			fuser.setFwallet(fwallet) ;

			
			List<Fvirtualcointype> fvirtualcointypes = (List)this.constantMap.get("virtualCoinType");
			for (Fvirtualcointype fvirtualcointype : fvirtualcointypes) {
				Fvirtualwallet fvirtualwallet = new Fvirtualwallet() ;
				fvirtualwallet.setFtotal(0F) ;
				fvirtualwallet.setFfrozen(0F) ;
				fvirtualwallet.setFvirtualcointype(fvirtualcointype) ;
				fvirtualwallet.setFuser(fuser) ;
				fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp()) ;
				this.fvirtualwalletDAO.save(fvirtualwallet) ;
			}
			//接受充值的虚拟地址
			for (Fvirtualcointype fvirtualcointype : fvirtualcointypes) {
				
				if(!fvirtualcointype.isFIsWithDraw()){
					continue ;
				}
				
				Fpool fpool = this.fpoolDAO.getOneFpool(fvirtualcointype) ;
				String address = fpool.getFaddress() ;
				Fvirtualaddress fvirtualaddress = new Fvirtualaddress() ;
				fvirtualaddress.setFadderess(address) ;
				fvirtualaddress.setFcreateTime(Utils.getTimestamp()) ;
				fvirtualaddress.setFuser(fuser) ;
				fvirtualaddress.setFvirtualcointype(fvirtualcointype) ;
				if(address==null || address.trim().equalsIgnoreCase("null") || address.trim().equals("")){
					throw new RuntimeException() ;
				}
				
				fpool.setFstatus(1) ;
				this.fpoolDAO.attachDirty(fpool) ;
				
				this.fvirtualaddressDAO.save(fvirtualaddress) ;
			}
//			//对外提现的虚拟地址
//			for (Fvirtualcointype fvirtualcointype : fvirtualcointypes) {
//				FvirtualaddressWithdraw fvirtualaddressWithdraw = new FvirtualaddressWithdraw() ;
//				fvirtualaddressWithdraw.setFadderess(null) ;
//				fvirtualaddressWithdraw.setFcreateTime(Utils.getTimestamp()) ;
//				fvirtualaddressWithdraw.setFuser(fuser) ;
//				fvirtualaddressWithdraw.setFvirtualcointype(fvirtualcointype) ;
//				this.fvirtualaddressWithdrawDAO.save(fvirtualaddressWithdraw) ;
//			}
			
//			//充值的银行账号
//			Fbankinfo fbankinfo = new Fbankinfo() ;
//			fbankinfo.setFcreateTime(Utils.getTimestamp()) ;
//			fbankinfo.setFstatus(BankInfoStatusEnum.NORMAL_VALUE) ;
//			fbankinfo.setFuser(fuser) ;
//			this.fbankinfoDAO.save(fbankinfo) ;
			
//			//提现的银行账号
//			FbankinfoWithdraw fbankinfoWithdraw = new FbankinfoWithdraw() ;
//			fbankinfoWithdraw.setFcreateTime(Utils.getTimestamp()) ;
//			fbankinfoWithdraw.setFstatus(BankInfoWithdrawStatusEnum.NORMAL_VALUE) ;
//			fbankinfoWithdraw.setFuser(fuser) ;
//			this.fbankinfoWithdrawDAO.save(fbankinfoWithdraw) ;
			
			//积分
			Fscore fscore = new Fscore() ;
			fscore.setFlevel(1) ;
			fscore.setFscore(0) ;
			fscore.setFgroupqty(0);
			fscore.setFtreeqty(0);
			fscore.setFkillQty(0);
			fscore.setFissend(false);
			this.fscoreDAO.save(fscore) ;
			fuser.setFscore(fscore) ;
		} catch (Exception e) {
			e.printStackTrace(); 
			throw new RuntimeException() ;
		}
		
		return true ;
	}
	public String addAddress(Fuser fuser) throws Exception{
		String msg="";
		String successMsg="";
		try {
			List<Fvirtualcointype> fvirtualcointypes = (List)this.constantMap.get("virtualCoinType");		
			//接受充值的虚拟地址
			for (Fvirtualcointype fvirtualcointype : fvirtualcointypes) {
				if(!fvirtualcointype.isFIsWithDraw()){
					continue ;
				}
				String filter= " where fuser.fid='"+fuser.getFid()+"' and fVi_fId='"+fvirtualcointype.getFid()+"'";
				int count=fvirtualaddressDAO.findByParamCount(filter, "Fvirtualaddress");
				if(count>0)
				{
					msg+=fvirtualcointype.getfShortName()+"已存在</br>";
				}else{
				Fpool fpool = this.fpoolDAO.getOneFpool(fvirtualcointype) ;
				String address = fpool.getFaddress() ;
				Fvirtualaddress fvirtualaddress = new Fvirtualaddress() ;
				fvirtualaddress.setFadderess(address) ;
				fvirtualaddress.setFcreateTime(Utils.getTimestamp()) ;
				fvirtualaddress.setFuser(fuser) ;
				fvirtualaddress.setFvirtualcointype(fvirtualcointype) ;
				if(address==null || address.trim().equalsIgnoreCase("null") || address.trim().equals("")){
					throw new RuntimeException() ;
				}
				
				fpool.setFstatus(1) ;
				
				try {
					this.fpoolDAO.attachDirty(fpool) ;
					
					this.fvirtualaddressDAO.save(fvirtualaddress) ;
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				successMsg+=fvirtualcointype.getfShortName()+"增加成功</br>";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException() ;
		}
		if(msg!="")
		{
			if(successMsg!="")
			{
				msg+=successMsg;
			}
			return msg;
		}else{
			return "新增钱包地址成功";
		}
	}
	
	public Fuser updateCheckLogin(Fuser fuser,String ip,boolean ismail){
		Fuser flag = null ;
		try{
			Map<String, Object> map = new HashMap<String, Object>() ;
			map.put("floginName", fuser.getFemail().toLowerCase()) ;
			map.put("floginPassword", Utils.MD5(fuser.getFloginPassword())) ;
			
			List<Fuser> list = this.fuserDAO.findByMap(map) ;
			if(list.size()>0){
				flag = list.get(0) ;
			}
//			if(ismail == false ){
//				map.put("ftelephone", fuser.getFemail().toLowerCase()) ;
//				map.put("floginPassword", Utils.MD5(fuser.getFloginPassword())) ;
//				
//				List<Fuser> list = this.fuserDAO.findByMap(map) ;
//				if(list.size()>0){
//					flag = list.get(0) ;
//				}
//			}else{
//				map.put("femail", fuser.getFemail().toLowerCase()) ;
//				map.put("floginPassword", Utils.MD5(fuser.getFloginPassword())) ;
//				
//				List<Fuser> list = this.fuserDAO.findByMap(map) ;
//				if(list.size()>0){
//					flag = list.get(0) ;
//				}
//
//			}
			
			if(flag!=null){
				Flog flog = new Flog() ;
				flog.setFcreateTime(Utils.getTimestamp()) ;
				flog.setFkey1(String.valueOf(flag.getFid())) ;
				flog.setFkey2(flag.getFloginName()) ;
				flog.setFkey3(ip) ;
				flog.setFtype(LogTypeEnum.User_LOGIN) ;
				this.flogDAO.save(flog) ;
				
				//更新登陆时间
				flag.setFlastLoginIp(ip);
				flag.setFlastLoginTime(Utils.getTimestamp());
				this.fuserDAO.attachDirty(flag);
			}
			
		}catch(Exception e){
			e.printStackTrace() ;
			fuser = null ;
			throw new RuntimeException() ;
		}
		return flag ;
	}
	
	public List<Fvirtualwallet> findVirtualWallet(String fuid, boolean isFY){
		/*TreeMap<Timestamp, Fvirtualwallet> treeMap = new TreeMap<Timestamp, Fvirtualwallet>(new Comparator<Timestamp>() {

			public int compare(Timestamp o1, Timestamp o2) {
				return o1.compareTo(o2) ;
			}
			
		}) ;*/
		//TreeMap<Timestamp, Fvirtualwallet> treeMap = new TreeMap<Timestamp, Fvirtualwallet>();
		List<Fvirtualwallet> fvirtualwallets = this.fvirtualwalletDAO.findX(fuid, VirtualCoinTypeStatusEnum.Normal, isFY) ;
		/*for (Fvirtualwallet fvirtualwallet : fvirtualwallets) {
			treeMap.put(fvirtualwallet.getFvirtualcointype().getFaddTime(), fvirtualwallet) ;
		}*/
		return fvirtualwallets ;
	}
	
	public Fvirtualwallet findVirtualWalletByUser(String fuid,String virtualCoinId){
		Fvirtualwallet fvirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(fuid, virtualCoinId) ;
		return fvirtualwallet ;
	}
	
	public Fuser findById(String introlUserId){
		Fuser fuser = this.fuserDAO.findById(introlUserId) ;
		return fuser ;
	}
	
	public int findIntroUserCount(Fuser fuser){
		return this.fuserDAO.findByProperty("fIntroUser_id.fid", fuser.getFid()).size() ;
	}
	
	public Fuser findById4WithDraw(String id) throws Exception{
		//提现，需要银行信息
		Fuser fuser = this.fuserDAO.findById(id) ;
		fuser.getFwallet().getFtotalRmb() ;
		return fuser ;
	}
	
	public Fbankinfo findUserBankInfo(String uid){
		Fbankinfo fbankinfo = this.fbankinfoDAO.findUserBankInfo(uid) ;
		return fbankinfo ;
	}
	
	public FbankinfoWithdraw findByIdWithBankInfos(String id){
		return this.fbankinfoWithdrawDAO.findById(id);
	}
	
	public boolean isEmailExists(String email) throws Exception{
		boolean flag = false ;
		List<Fuser> list = this.fuserDAO.findByProperty("femail", email) ;
		flag = list.size()>0 ;
		return flag ;
	}
	
	public boolean isTelephoneExists(String telephone) throws Exception{
		boolean flag = false ;
		List<Fuser> list = this.fuserDAO.findByProperty("ftelephone", telephone) ;
		flag = list.size()>0 ;
		return flag ;
	}
	
	public void updateFUser(Fuser fuser,HttpSession session,int logType,String ip){
		
		this.fuserDAO.attachDirty(fuser) ;
		if(logType >0){//操作记录。无记录填负数
			Flog flog = new Flog() ;
			flog.setFcreateTime(Utils.getTimestamp()) ;
			flog.setFkey1(String.valueOf(fuser.getFid())) ;
			flog.setFkey2(fuser.getFloginName()) ;
			flog.setFkey3(ip) ;
			flog.setFtype(logType) ;
			this.flogDAO.save(flog) ;
		}
		if(session!=null && session.getAttribute("login_user")!=null){			
			session.setAttribute("login_user", fuser) ;
		}
	}
	
	public void updateFuser(Fuser fuser){
		this.fuserDAO.attachDirty(fuser) ;
	}
	
	public void updateFuser(Fuser fuser,Fintrolinfo introlInfo,
			Fscore fintrolScore,Fscore fscore){
		try {
			if(fuser != null){
				this.fuserDAO.attachDirty(fuser) ;
			}
			if(introlInfo != null){
				this.introlinfoDAO.save(introlInfo);
			}
			if(fintrolScore != null){
				this.fscoreDAO.attachDirty(fintrolScore);
			}
			if(fscore != null){
				this.fscoreDAO.attachDirty(fscore);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	
	public List<Fuser> findUserByProperty(String key,Object value){
		return this.fuserDAO.findByProperty(key, value) ;
	}
	
	public void addBankInfo(Fbankinfo fbankinfo,Fuser fuser){
		try {
			Fbankinfo example = new Fbankinfo() ;
			example.setFuser(fuser) ;
			example.setFstatus(BankInfoStatusEnum.NORMAL_VALUE) ;
			List<Fbankinfo> fbankinfos = this.fbankinfoDAO.findByExample(example) ;
			for (Fbankinfo fbankinfo2 : fbankinfos) {
				fbankinfo2.setFstatus(BankInfoStatusEnum.ABNORMAL_VALUE) ;
				this.fbankinfoDAO.attachDirty(fbankinfo2) ;
			}
			this.fbankinfoDAO.save(fbankinfo) ;
		} catch (Exception e) {
		     throw new RuntimeException();
		}
	}
	
	public void updateBankInfoWithdraw(FbankinfoWithdraw fbankinfoWithdraw){
		try {
			fbankinfoWithdrawDAO.save(fbankinfoWithdraw) ;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public void updateDelBankInfoWithdraw(FbankinfoWithdraw fbankinfoWithdraw){
		try {
			fbankinfoWithdrawDAO.delete(fbankinfoWithdraw);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public Fwallet findFwalletById(String id){
		return this.fwalletDAO.findById(id) ;
	}
	
	public boolean deleteAllUser() throws Exception{
		List<Fuser> fusers = this.fuserDAO.findAll() ;
		for (Fuser fuser : fusers) {
			this.fuserDAO.delete(fuser) ;
		}
		return true ;
	}
	
	public String getSystemArgs(String key){
		String value = null ;
		List<Fsystemargs> list = this.fsystemargsDAO.findByFkey(key) ;
		if(list.size()>0){
			value = list.get(0).getFvalue() ;
		}
		return value ;
	}

	
	public Fapi addFapi(Fuser fuser,String label){
		Fapi fapi = new Fapi() ;
		fapi.setFpartner(Utils.UUID()) ;
		fapi.setFsecret(Utils.randomString(36).toUpperCase()) ;
		fapi.setFuser(fuser) ;
		fapi.setLabel(label);
		
		this.fapiDAO.save(fapi) ;
		
		fuser.setFlastUpdateTime(Utils.getTimestamp()) ;
		this.fuserDAO.attachDirty(fuser) ;
		
		return fapi ;
	}
	
	public void deleteFapi(Fapi fapi){
		this.fapiDAO.delete(fapi) ;
	}
	
	public Fapi findFapiById(int id){
		return this.fapiDAO.findById(id) ;
	}
	
	public List<FbankinfoWithdraw> findFbankinfoWithdrawByFuser(int firstResult, int maxResults, String filter,boolean isFY){
		return this.fbankinfoWithdrawDAO.list(firstResult, maxResults, filter, isFY);
	}
	
	public boolean saveValidateEmail(Fuser fuser,String email,boolean force) throws RuntimeException{
		boolean flag = false ;
		try {
			if(!force){
				//半小时内只能发送一次
				Emailvalidate ev = this.validateMap.getMailMap(fuser.getFid()+"_"+SendMailTypeEnum.ValidateMail) ;
				if(ev!=null && Utils.getTimestamp().getTime()-ev.getFcreateTime().getTime()<30*60*1000L){
					flag = false ;
					return flag ;
				}
			}
			
			
			//发送激活邮件
			String UUID = Utils.UUID() ;
			//发送给用户的邮件
			Fvalidateemail validateemails = new Fvalidateemail() ;
			validateemails.setFtitle(this.getSystemArgs(ConstantKeys.WEB_NAME)+"邮箱绑定") ;
			validateemails.setFcontent(
					this.getSystemArgs(ConstantKeys.mailValidateContent)
					.replace("#firstLevelDomain#", this.getSystemArgs(ConstantKeys.firstLevelDomain))
					.replace("#url#", Constant.Domain+"validate/mail_validate.html?uid="+fuser.getFid()+"&&uuid="+UUID)
					.replace("#fulldomain#", this.getSystemArgs(ConstantKeys.fulldomain))
					.replace("#englishName#", this.getSystemArgs(ConstantKeys.englishName))) ;
			validateemails.setFcreateTime(Utils.getTimestamp()) ;
			validateemails.setFstatus(ValidateMailStatusEnum.Not_send) ;
			validateemails.setFuser(fuser) ;
			validateemails.setEmail(email);
			this.validateemailsDAO.save(validateemails) ;
			
			//验证并对应到用户的UUID
			Emailvalidate emailvalidate = new Emailvalidate() ;
			emailvalidate.setFcreateTime(Utils.getTimestamp()) ;
			emailvalidate.setFuser(fuser) ;
			emailvalidate.setFuuid(UUID) ;
			emailvalidate.setFmail(email);
			emailvalidate.setType(SendMailTypeEnum.ValidateMail) ;
			this.emailvalidateDAO.save(emailvalidate) ;
			
			//加入邮件队列
			this.taskList.returnMailList(validateemails.getFid()) ;
			
			this.validateMap.putMailMap(fuser.getFid()+"_"+SendMailTypeEnum.ValidateMail, emailvalidate) ;
			
			flag = true ;
			return flag ;
		} catch (Exception e) {
			throw new RuntimeException() ;
		}
		
	}
	
	public void updateDisabledApi(Fuser fuser){
		try {
			Fapi fapi = fuser.getFapi() ;
			fuser.setFapi(null) ;
			this.fuserDAO.attachDirty(fuser) ;
			
			if(fapi!=null){
				this.fapiDAO.delete(fapi) ;
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public List<Fapi> findFapiByProperty(String key,Object value){
		return this.fapiDAO.findByProperty(key, value) ;
	}

	public Fuser findFuserByFapi(Fapi fapi){
		List<Fuser> fusers = this.fuserDAO.findByProperty("fapi.fid", fapi.getFid()) ;
		return fusers.get(0) ;
	}
	
	public Fuser findByQQlogin(String openId){
		List<Fuser> fusers = this.fuserDAO.findByProperty("qqlogin", openId) ;
		if(fusers.size()>0){
			return fusers.get(0) ;
		}else{
			return null ;
		}
	}
	
	public Fscore findFscoreById(int id){
		return this.fscoreDAO.findById(id) ;
	}
	
	public Fusersetting findFusersetting(int fid){
		return this.fusersettingDAO.findById(fid) ;
	}
	
	public void updateFusersetting(Fusersetting fusersetting){
		this.fusersettingDAO.attachDirty(fusersetting);
	}
	
	public void updateCleanScore(){
		this.fusersettingDAO.updateCleanScore() ;
	}
	
	public void updateSendLog(Fvirtualwallet fvirtualwallet,Fmessage message) {
		try {
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			this.fmessageDAO.save(message);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public void updateTransport(Ftransportlog ftransportlog ,
            Fvirtualwallet fvirtualwallet) {
		try {
			this.transportlogDAO.save(ftransportlog);
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public void updateTransport(Ftransportlog ftransportlog ,
            Fvirtualwallet fvirtualwallet,Fvirtualwallet w2) {
		try {
			this.transportlogDAO.save(ftransportlog);
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
			this.fvirtualwalletDAO.attachDirty(w2);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public void updateCancelTransport(Ftransportlog ftransportlog ,
            Fvirtualwallet fvirtualwallet) {
		try {
			this.transportlogDAO.attachDirty(ftransportlog);
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public FbankinfoWithdraw findByIdWithBankInfos(String fid, String bankNum) {
		return this.fbankinfoWithdrawDAO.findByParam(fid, bankNum);
		
	}
	public FbankinfoWithdraw findFbankinfoWithdraw(String fid) {
		return this.fbankinfoWithdrawDAO.findById(fid) ;
		
	}

	public List<Fvirtualwallet> findVirtualWallet(String fuid, int firstResult, int maxResult, boolean isFY) {
		// TODO Auto-generated method stub
		List<Fvirtualwallet> fvirtualwallets = this.fvirtualwalletDAO.find(fuid, VirtualCoinTypeStatusEnum.Normal, firstResult, maxResult, isFY);
		return fvirtualwallets;
	}

	public List<Fvirtualwallet> findVirtualWallet(String fuid, String keyWord, int firstResult, int maxResult,
			boolean isFY) {
		// TODO Auto-generated method stub
		List<Fvirtualwallet> fvirtualwallets = this.fvirtualwalletDAO.findByName(fuid, VirtualCoinTypeStatusEnum.Normal, keyWord, firstResult, maxResult, isFY);
		return fvirtualwallets;
	}

	public int findVirtualWalletCount(String sql) {
		// TODO Auto-generated method stub
		int count1 = this.fvirtualwalletDAO.findVirtualWalletCount(sql);
		return count1;
	}
	public void updatelogFindPwd(Fuser fuser,String ip){
		try {
			Flog flog = new Flog() ;
			flog.setFcreateTime(Utils.getTimestamp()) ;
			flog.setFkey1(String.valueOf(fuser.getFid())) ;
			flog.setFkey2(fuser.getFloginName()) ;
			flog.setFkey3(ip) ;
			flog.setFtype(LogTypeEnum.User_FIND_PWD) ;
			this.flogDAO.save(flog) ;
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
}
