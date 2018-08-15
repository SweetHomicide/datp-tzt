package com.ditp.service.wallet;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ditp.service.StationMailService;
import com.ruizton.main.dao.FfeesDAO;
import com.ruizton.main.dao.FvirtualaddressDAO;
import com.ruizton.main.model.Fpool;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualaddress;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.admin.PoolService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualCapitaloperationService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.util.Comm;
import com.ruizton.util.Utils;
import com.wallet.platfrom.sdk.IWalletPlatfromInterface;
import com.wallet.platfrom.sdk.ProcessResult;
import com.wallet.platfrom.sdk.WalletPlatfromConfig;
import com.wallet.platfrom.sdk.beans.WithdrawDataBean;
/**
 * @author   Dylan
 * @data     2018年8月11日
 * @typeName WalletPlatFromService
 * 说明 ：钱包服务
 *
 */
@Service
public class WalletPlatFromService implements IWalletPlatfromInterface {

	@Autowired
	private PoolService poolService;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private UserService userService;
	@Autowired
	private VirtualCapitaloperationService virtualCapitaloperationService;
	@Autowired
	private FvirtualaddressDAO fvirtualaddressDAO;
	@Autowired
	private VirtualWalletService virtualWalletService;
	@Autowired
	private StationMailService stationMailService;

	private Fvirtualwallet fvirtualwallet;
	private Fvirtualcaptualoperation fvtpt;

	@Autowired
	private FfeesDAO ffeesDAO ;
	//每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();
	
	public static WalletPlatfromConfig CONFIG=new WalletPlatfromConfig();
	static{
//		CONFIG.setPrivateKeyFile("/usr/local/iexchg/keyfile/private-iexchg.key");
//	    CONFIG.setPublicKeyFile("/usr/local/iexchg/keyfile/public-wallet.key");
		
		
	    
	    CONFIG.setPrivateKeyFile("/Work/SoftwareRuntime/tzt-wallet/keyfile/private-wallet.key");
	    CONFIG.setPublicKeyFile("/Work/SoftwareRuntime/tzt-wallet/keyfile/public-tzt.key");
	}
	
	/**
	 * 充币交易数据处理
	 * 如果不想让上传，就尽量要使用setSuccess(TRUE)和setCode(0)
	 * @param txid
	 *            交易ID
	 * @param address
	 *            充币地址
	 * @param amount
	 *            充币金额
	 * @return 处理结果
	 */
	public ProcessResult charge(String txid, String address, BigDecimal amount) {
		// TODO Auto-generated method stub
		ProcessResult pr=new ProcessResult();
		try {
			
			if(txid.equals("")||txid==null)
			{
	            pr.setSuccess(true);
	            pr.setCode(0);
	            pr.setMessage("交易ID不能为空");
	            return pr;
			}
       	if(address.equals("")||address==null)
			{
	            pr.setSuccess(true);
	            pr.setCode(0);
	            pr.setMessage("地址不能为空");
	            return pr;
			}
			if(amount.equals("")||amount==null)
			{
	            pr.setSuccess(true);
	            pr.setCode(0);
	            pr.setMessage("充币金额不能为空");
	            return pr;
			}
			//根据充币地址匹配用户ID和币种类型
			Fuser fuModel=new Fuser();//用户id
			Fvirtualcointype fviType=new Fvirtualcointype();//币种类型
			//根据充值地址查询交易所的用户地址表
			List<Fvirtualaddress> listfviaddress=fvirtualaddressDAO.findByProperty("fadderess", address);
			if(listfviaddress.size()>0)
			{
				for(Fvirtualaddress fviAddress:listfviaddress)
				{
					fuModel=fviAddress.getFuser();//用户id
					fviType=fviAddress.getFvirtualcointype();//币种id
			//充值记录
			fvtpt=new Fvirtualcaptualoperation();		
			fvtpt.setFuser(fuModel);
			fvtpt.setFvirtualcointype(fviType);
			//获取当前时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
			String dateString = sdf.format(new java.util.Date());
			fvtpt.setFcreateTime(Timestamp.valueOf(dateString));
			double amo= Double.parseDouble(amount.toString());
			fvtpt.setFamount(amo);
			fvtpt.setFfees(0.000000);
			fvtpt.setFtype(1);
			fvtpt.setFstatus(1);
			fvtpt.setFlastUpdateTime(Timestamp.valueOf(dateString));
			fvtpt.setVersion(0);
			fvtpt.setRecharge_virtual_address(address);
			fvtpt.setFconfirmations(0);
			fvtpt.setFhasOwner(true);
			
			fvtpt.setFtradeUniqueNumber(txid);
			virtualCapitaloperationService.saveObj(fvtpt);
			
			//获取平台钱包
			String fivwFilter="where fvirtualcointype.fid='"+fviType.getFid()+"' and fuser.fid='"+fuModel.getFid()+"'";
			List<Fvirtualwallet> listFvirtualwallet= virtualWalletService.list(0, 0, fivwFilter, false);
			
			if(listFvirtualwallet.size()>0)
			{
				for(Fvirtualwallet fviw:listFvirtualwallet)
				{
					fvirtualwallet=fviw;
				}
			}
			double amoTotal=fvirtualwallet.getFfrozen()+amo;
			fvirtualwallet.setFfrozen(amoTotal);
			virtualWalletService.updateObj(fvirtualwallet);
             pr.setSuccess(true);
             pr.setCode(0);
             pr.setMessage("");
             
				}
			}
			
			return pr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
             pr.setSuccess(true);
            pr.setCode(0);
            pr.setMessage(e.getMessage());
			return pr;
		}
	}
	/**
	 * 确认充币交易达到确认数交易
	 * 
	 * @param txid
	 *            交易ID
	 * @param confirms
	 *            确认数
	 * @return 处理结果
	 */
	public ProcessResult confirmCharge(String txid, Integer confirms) {
		// TODO Auto-generated method stub
		ProcessResult pr=new ProcessResult();
		int code=1;//1 继续上传 0请求成功
		int virConfirs=Integer.parseInt(Comm.getVIR_CONFIRS());
		boolean Success=false;
		try {
           //attachDirty
	    String fivwFilterV="where ftype='1' and ftradeUniqueNumber='"+txid+"'";
		//List<Fvirtualcaptualoperation> listFvtpt=virtualCapitaloperationService.findByProperty("ftradeUniqueNumber", txid);
		List<Fvirtualcaptualoperation> listFvtpt=virtualCapitaloperationService.list(0,0,fivwFilterV, false);
		if(listFvtpt.size()>0)
		{
			for(Fvirtualcaptualoperation fiv:listFvtpt)
			{
				fvtpt=fiv;
			}
			//确认数至少3人才会成功
			if(confirms>=virConfirs)
			{
				fvtpt.setFstatus(3);
				//发送站内信
				stationMailService.sendStationMail(fvtpt);
				
			}		
			fvtpt.setFtradeUniqueNumber(txid);
			fvtpt.setFconfirmations(confirms);
			virtualCapitaloperationService.updateObj(fvtpt);
			
			Fuser fuModel=new Fuser();//用户id
			Fvirtualcointype fviType=new Fvirtualcointype();//币种类型		
			fuModel=fvtpt.getFuser();
			fviType=fvtpt.getFvirtualcointype();
			if(confirms>=virConfirs)
			{
			 String fivwFilter="where fvirtualcointype.fid='"+fviType.getFid()+"' and fuser.fid='"+fuModel.getFid()+"'";
			//获取平台钱包
			List<Fvirtualwallet> listFvirtualwallet= virtualWalletService.list(0, 0, fivwFilter, false);
			
			if(listFvirtualwallet.size()>0)
			{
				for(Fvirtualwallet fviw:listFvirtualwallet)
				{
					fvirtualwallet=fviw;
				}
			}
			double frozenAmo=fvtpt.getFamount();
			double amoTotal=fvirtualwallet.getFtotal();
			double frozenTal=fvirtualwallet.getFfrozen()-frozenAmo;
			fvirtualwallet.setFtotal(amoTotal+frozenAmo);
			fvirtualwallet.setFfrozen(frozenTal);
	    	virtualWalletService.updateObj(fvirtualwallet);
	    	Success=true;
	    	code=0;
			}
            pr.setSuccess(Success);
            pr.setCode(code);
            pr.setMessage("");
            return pr;
            
		}else{
            pr.setSuccess(false);
            pr.setCode(1);
            pr.setMessage("数据不存在");
            return pr;
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            pr.setSuccess(true);
            pr.setCode(0);
            pr.setMessage(e.getMessage());
            return pr;
		}
	}

	/**
	 * 获取钱包管理客户端配置
	 * 
	 * @return
	 */
	public WalletPlatfromConfig getConfig() {
		//CONFIG.setPrivateKeyFile("C:\\private.key");
	    //CONFIG.setPublicKeyFile("C:\\public2.key");
		return CONFIG;
	}
	/**
	 * 获取提币数据信息
	 * 
	 * @return 提币数据信息
	 */
	public List<WithdrawDataBean> getWithdrawDatas() {
		// TODO Auto-generated method stub
		
		try {
			List<WithdrawDataBean> listWith=new ArrayList<WithdrawDataBean>();
			WithdrawDataBean withddb=new WithdrawDataBean();
            //获取提币信息
			//查询条件  fstatus1、等待提现  2、正在处理 3、提现成功  4、用户取消

			String filter="where 1=1 and ftype=2 and fstatus in(1)";
			//当前页
			int currentPage = 1;
			List<Fvirtualcaptualoperation> listFviOp=virtualCapitaloperationService.list((currentPage-1)*numPerPage, numPerPage, filter+"",false);
			if(listFviOp.size()>0)
	{
		for(Fvirtualcaptualoperation FciOp:listFviOp)
		{
		
			withddb.setSerno(FciOp.getFid().toString());//平台的一个唯一的业务编号
			//根据简称获取币的类型
		List<Fvirtualcointype> listFvirtualcointype=virtualCoinService.findByProperty("fid",FciOp.getFvirtualcointype().getFid());
		if(listFvirtualcointype.size()>0)
		{
			for(Fvirtualcointype Fvitype:listFvirtualcointype)
			{
				withddb.setSymbol(Fvitype.getfSymbol());//资产（币种）名称 ，ABC
				
			}
		}		
			withddb.setAddress(FciOp.getWithdraw_virtual_address());//提币的地址
			Object amount=FciOp.getFamount();
			BigDecimal bigamount=new BigDecimal(amount.toString());
			withddb.setAmount(bigamount);//提币的金额
			listWith.add(withddb);
			//修改提币状态为处理中 之后处理
			FciOp.setFstatus(2);
			virtualCapitaloperationService.updateObj(FciOp);
			
		}
	}
			return listWith;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	/**
	 * 处理新获取地址的接口
	 * 
	 * @param addresses
	 *            新地址
	 * @return 还需要生成地址的数量
	 */
	
	public Integer processAddress(String symbol, List<String> addresses) {
		// TODO Auto-generated method stub
		//当前页
		int currentPage = 1;
	
		try {
			String fviid="";//币种类型ID
			Fvirtualcointype fvirtualType=new Fvirtualcointype();
			String filter="";
			//根据简称获取币的类型
		List<Fvirtualcointype> listFvirtualcointype=virtualCoinService.findByProperty("fSymbol",symbol);
		if(listFvirtualcointype !=null && listFvirtualcointype.size()>0)
		{
			for(Fvirtualcointype fvitype:listFvirtualcointype)
			{
				fviid=fvitype.getFid();
				fvirtualType=fvitype;

			}
		}else{
			return -2;//交易所没有上币
		}
		//查询条件

			//外键币种类型
			/*Fvirtualcointype type=new Fvirtualcointype();
			type.setFid(fviid);*/
			//遍历地址增加到数据库
			if(addresses!=null && addresses.size()>0)
			{
				for(String address:addresses)
				{
			Fpool poolInfo=new Fpool();
			poolInfo.setFaddress(address);//地址
			poolInfo.setFvirtualcointype(fvirtualType);//币种类别
			poolService.saveObj(poolInfo);
				}
			}
			//根据用户的倍数获取地址数量 暂定2倍
			int userCount=0;//用户数量
			int poolCount=0;//可用地址数量
			int addressCount=0;//理想可用地址数量
			int returnCount=0;
			filter="where 1=1 and (a.fstatus=0 or a.fstatus is null) "
				    + "and fvi_type='"+fviid+"'";
		List<Fuser> listUser=userService.findAll();
		userCount=listUser.size();
		//获取币种可用地址数量
		Map mapPool = poolService.mapFpool((currentPage-1)*numPerPage, numPerPage, filter+"",false);
		if(mapPool==null)
		{
			
		}else{
		poolCount=Integer.parseInt(mapPool.get("c").toString());
		}
		//poolCount=listPool;
		//TODO 如果用户是新币种 数量：用户数量+3000
		addressCount=3000;
	if(fvirtualType.getVersion()==0)
	{
		returnCount=userCount+addressCount;
		
	}else
		if(poolCount<addressCount)
		{
			returnCount=addressCount-poolCount;
		}
		  // returnCount=5;
			return returnCount;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 提币数据钱包客户端结果处理
	 * 
	 * @param success
	 *            处理是否成功
	 * @param txid
	 *            交易ID，在success = true时有效
	 * @param message
	 *            错误信息，在success = false时有效
	 * @param withdrawData
	 *            提币数据信息
	 * @return 处理结果
	 */
	public ProcessResult processWithdrawResult(Boolean success, String txid, String message, WithdrawDataBean withdrawData) {
		// TODO Auto-generated method stub
		ProcessResult pr=new ProcessResult();
		Fuser fuModel=new Fuser();//用户id
		System.out.println(success);
		Fvirtualcointype fviType=new Fvirtualcointype();//币种类型
		try {
			//虚拟币充值和提现列表
			fvtpt=virtualCapitaloperationService.findById(withdrawData.getSerno());
			if(success&&txid!=null&&txid!=""&&fvtpt!=null)
			{
				
				fvtpt.setFstatus(3);
				fvtpt.setFtradeUniqueNumber(txid);
				virtualCapitaloperationService.updateObj(fvtpt);	
				double fee=fvtpt.getFfees();
				fuModel=fvtpt.getFuser();
				fviType=fvtpt.getFvirtualcointype();				
				//获取平台钱包
			   String fivwFilter="where fvirtualcointype.fid='"+fviType.getFid()+"' and fuser.fid='"+fuModel.getFid()+"'";
				List<Fvirtualwallet> listFvirtualwallet= virtualWalletService.list(0, 0, fivwFilter, false);
				
				if(listFvirtualwallet.size()>0)
				{
					for(Fvirtualwallet fviw:listFvirtualwallet)
					{
						fvirtualwallet=fviw;
					}
				}
				
		        double wtd=Double.parseDouble(withdrawData.getAmount().toString())+fee;
				double frozenAmo=fvirtualwallet.getFfrozen()-wtd;
				fvirtualwallet.setFfrozen(frozenAmo);
		    	virtualWalletService.updateObj(fvirtualwallet);
		    	
	             pr.setSuccess(true);
	             pr.setCode(0);
	             pr.setMessage("");
	          //发送站内信
			  stationMailService.sendStationMail(fvtpt);
				
			}else if(!success)
			{
				System.out.println("提币失败");
				if("".equals(txid) || ("null").equals(txid))
				{
				fvtpt.setFstatus(5);
				fvtpt.setFtradeUniqueNumber("");
				virtualCapitaloperationService.updateObj(fvtpt);	
				
					double sum = fvtpt.getFamount()+ fvtpt.getFfees();
					fuModel=fvtpt.getFuser();
					fviType=fvtpt.getFvirtualcointype();	
					//获取平台钱包
						String fivwFilter="where fvirtualcointype.fid='"+fviType.getFid()+"' and fuser.fid='"+fuModel.getFid()+"'";
						List<Fvirtualwallet> listFvirtualwallet= virtualWalletService.list(0, 0, fivwFilter, false);
						
						if(listFvirtualwallet.size()>0)
						{
							for(Fvirtualwallet fviw:listFvirtualwallet)
							{
								fvirtualwallet=fviw;
							}
						}
						fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+fvtpt.getFamount()+ fvtpt.getFfees());
						fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen()-fvtpt.getFamount()-fvtpt.getFfees());
						virtualWalletService.updateObj(fvirtualwallet);
						
				
	             pr.setSuccess(true);
	             pr.setCode(0);
	             pr.setMessage("");
	             System.out.println(pr.getMessage()+":482+=====");
				}
			}
			

			    return pr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            pr.setSuccess(true);
            pr.setCode(0);
            pr.setMessage(e.getMessage());
            System.out.println(pr.getMessage());
			return pr;
		}
	}

}
