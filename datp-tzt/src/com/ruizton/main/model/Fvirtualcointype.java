package com.ruizton.main.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.ruizton.main.Enum.CoinTypeEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;

/**
 * Fvirtualcointype entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "fvirtualcointype")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Fvirtualcointype implements java.io.Serializable {

	// Fields
	private String fid;
	private int fid_s;
	private boolean fisShare;// 是否可以交易   1为可以交易  
	private boolean FIsWithDraw;// 是否可以充值提现
	private String fname;//币种名称
	private String fShortName;//简写名称
	private String fdescription;//描述
	private Timestamp faddTime;//添加时间
	private int fstatus;// VirtualCoinTypeStatusEnum
	private String fstatus_s;
	private String fSymbol;
	private String faccess_key;
	private String fsecrt_key;
	private String fip;
	private String fport;
	private double lastDealPrize;// fake,最新成交价格
	private double higestBuyPrize;//最高买入
	private double lowestSellPrize;//最低卖出
	private boolean canLend;// 是否可以借贷
	private boolean fisDefAsset;// 是否是默认资产   0不是  1是
	
	private Set<Ffees> ffees = new HashSet<Ffees>(0);
	private Set<Fentrustplan> fentrustplans = new HashSet<Fentrustplan>(0);
	private Set<Fentrust> fentrusts = new HashSet<Fentrust>(0);
	private Set<Fvirtualcaptualoperation> fvirtualcaptualoperations = new HashSet<Fvirtualcaptualoperation>(0);
	private Set<Fvirtualwallet> fvirtualwallets = new HashSet<Fvirtualwallet>(0);
	private int version;
	private String furl;

	private String feName;
	private Timestamp fstarttime;

	private String fweburl;
	private String fwalleturl;
	private String fminingurl;

	// 中文名：凯特币 英文名：katecoin 英文简称：KTC
	// 研发者：凯特币开发团队 核心算法：Scrypt 发布日期：2015-08-05
	// 区块速度：2分钟 发行总量：4200万 存量：
	// 证明方式：POW+POS 难度调整：6个月 区块奖励：4个
	// 主要特色： 不足之处
	private String fowner;
	private String fshuangfa;
	private String fqukai;
	private String ftotal;
	private String fintotal;
	private String ffangshi;
	private String fnandu;
	private String fqukaijian;
	private String fgood;
	private String fbad;

	private int fcount;
	private String ftradetime;
	private double fprice;
	private Double total;
	
	private int ftype;
	private String ftype_s;

	// Constructors



	/** default constructor */
	public Fvirtualcointype() {
	}

	/** full constructor */
	public Fvirtualcointype(String fname, String fdescription,
			Timestamp faddTime, Set<Fentrustplan> fentrustplans,
			Set<Fentrust> fentrusts,
			Set<Fvirtualcaptualoperation> fvirtualcaptualoperations,
			Set<Fvirtualwallet> fvirtualwallets) {
		this.fname = fname;
		this.fdescription = fdescription;
		this.faddTime = faddTime;
		this.fentrustplans = fentrustplans;
		this.fentrusts = fentrusts;
		this.fvirtualcaptualoperations = fvirtualcaptualoperations;
		this.fvirtualwallets = fvirtualwallets;
	}

	// Property accessors
	@GenericGenerator(name = "generator", strategy = "uuid")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "fId", unique = true, nullable = false)
	public String getFid() {
		return this.fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	@Column(name = "fName", length = 32)
	public String getFname() {
		return this.fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	@Column(name = "fDescription", length = 32)
	public String getFdescription() {
		return this.fdescription;
	}

	public void setFdescription(String fdescription) {
		this.fdescription = fdescription;
	}

	@Column(name = "fAddTime", length = 0)
	public Timestamp getFaddTime() {
		return this.faddTime;
	}

	public void setFaddTime(Timestamp faddTime) {
		this.faddTime = faddTime;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "fvirtualcointype")
	public Set<Fentrustplan> getFentrustplans() {
		return this.fentrustplans;
	}

	public void setFentrustplans(Set<Fentrustplan> fentrustplans) {
		this.fentrustplans = fentrustplans;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "fvirtualcointype")
	public Set<Fentrust> getFentrusts() {
		return this.fentrusts;
	}

	public void setFentrusts(Set<Fentrust> fentrusts) {
		this.fentrusts = fentrusts;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "fvirtualcointype")
	public Set<Fvirtualcaptualoperation> getFvirtualcaptualoperations() {
		return this.fvirtualcaptualoperations;
	}

	public void setFvirtualcaptualoperations(
			Set<Fvirtualcaptualoperation> fvirtualcaptualoperations) {
		this.fvirtualcaptualoperations = fvirtualcaptualoperations;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "fvirtualcointype")
	public Set<Ffees> getFfees() {
		return ffees;
	}

	public void setFfees(Set<Ffees> ffees) {
		this.ffees = ffees;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "fvirtualcointype")
	public Set<Fvirtualwallet> getFvirtualwallets() {
		return this.fvirtualwallets;
	}

	public void setFvirtualwallets(Set<Fvirtualwallet> fvirtualwallets) {
		this.fvirtualwallets = fvirtualwallets;
	}

	@Column(name = "fstatus")
	public int getFstatus() {
		return fstatus;
	}

	public void setFstatus(int fstatus) {
		this.fstatus = fstatus;
	}

	@Column(name = "fShortName")
	public String getfShortName() {
		return fShortName;
	}

	public void setfShortName(String fShortName) {
		this.fShortName = fShortName;
	}

	@Transient
	public String getFstatus_s() {
		return VirtualCoinTypeStatusEnum.getEnumString(this.getFstatus());
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
	}

	@Version
	@Column(name = "version")
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Column(name = "fSymbol")
	public String getfSymbol() {
		return fSymbol;
	}

	public void setfSymbol(String fSymbol) {
		this.fSymbol = fSymbol;
	}

	@Column(name = "faccess_key")
	public String getFaccess_key() {
		return faccess_key;
	}

	public void setFaccess_key(String faccess_key) {
		this.faccess_key = faccess_key;
	}

	@Column(name = "fsecrt_key")
	public String getFsecrt_key() {
		return fsecrt_key;
	}

	public void setFsecrt_key(String fsecrt_key) {
		this.fsecrt_key = fsecrt_key;
	}

	@Column(name = "fip")
	public String getFip() {
		return fip;
	}

	public void setFip(String fip) {
		this.fip = fip;
	}

	@Column(name = "fport")
	public String getFport() {
		return fport;
	}

	public void setFport(String fport) {
		this.fport = fport;
	}

	@Transient
	public double getLastDealPrize() {
		return lastDealPrize;
	}

	public void setLastDealPrize(double lastDealPrize) {
		this.lastDealPrize = lastDealPrize;
	}

	@Transient
	public double getHigestBuyPrize() {
		return higestBuyPrize;
	}

	public void setHigestBuyPrize(double higestBuyPrize) {
		this.higestBuyPrize = higestBuyPrize;
	}

	@Transient
	public double getLowestSellPrize() {
		return lowestSellPrize;
	}

	public void setLowestSellPrize(double lowestSellPrize) {
		this.lowestSellPrize = lowestSellPrize;
	}

	@Transient
	public String getFid_s() {
		String id = this.getFid();
		if (id != null) {
			return String.valueOf(id);
		}
		return String.valueOf(0);
	}

	public void setFid_s(int fid_s) {
		this.fid_s = fid_s;
	}
	
	/**
	 * 
	 *  作者：           Dylan
	 *  标题：           isFisShare 
	 *  时间：           2018年8月14日
	 *  描述：          币种是否可以交易  
	 *  	   true  可以
	 *  	   false 不可以         
	 *  @return boolean
	 */
	@Column(name = "fisShare")
	public boolean isFisShare() {
		return fisShare;
	}

	public void setFisShare(boolean fisShare) {
		this.fisShare = fisShare;
	}

	@Column(name = "FIsWithDraw")
	public boolean isFIsWithDraw() {
		return FIsWithDraw;
	}

	public void setFIsWithDraw(boolean fIsWithDraw) {
		FIsWithDraw = fIsWithDraw;
	}

	@Column(name = "furl")
	public String getFurl() {
		return furl;
	}

	public void setFurl(String furl) {
		this.furl = furl;
	}

	@Column(name = "feName")
	public String getFeName() {
		return feName;
	}

	public void setFeName(String feName) {
		this.feName = feName;
	}

	@Column(name = "fstarttime")
	public Timestamp getFstarttime() {
		return fstarttime;
	}

	public void setFstarttime(Timestamp fstarttime) {
		this.fstarttime = fstarttime;
	}

	@Column(name = "fweburl")
	public String getFweburl() {
		return fweburl;
	}

	public void setFweburl(String fweburl) {
		this.fweburl = fweburl;
	}

	@Column(name = "fwalleturl")
	public String getFwalleturl() {
		return fwalleturl;
	}

	public void setFwalleturl(String fwalleturl) {
		this.fwalleturl = fwalleturl;
	}

	@Column(name = "fminingurl")
	public String getFminingurl() {
		return fminingurl;
	}

	public void setFminingurl(String fminingurl) {
		this.fminingurl = fminingurl;
	}

	@Column(name = "fowner")
	public String getFowner() {
		return fowner;
	}

	public void setFowner(String fowner) {
		this.fowner = fowner;
	}

	@Column(name = "fshuangfa")
	public String getFshuangfa() {
		return fshuangfa;
	}

	public void setFshuangfa(String fshuangfa) {
		this.fshuangfa = fshuangfa;
	}

	@Column(name = "fqukai")
	public String getFqukai() {
		return fqukai;
	}

	public void setFqukai(String fqukai) {
		this.fqukai = fqukai;
	}

	@Column(name = "ftotal")
	public String getFtotal() {
		return ftotal;
	}

	public void setFtotal(String ftotal) {
		this.ftotal = ftotal;
	}

	@Column(name = "fintotal")
	public String getFintotal() {
		return fintotal;
	}

	public void setFintotal(String fintotal) {
		this.fintotal = fintotal;
	}

	@Column(name = "ffangshi")
	public String getFfangshi() {
		return ffangshi;
	}

	public void setFfangshi(String ffangshi) {
		this.ffangshi = ffangshi;
	}

	@Column(name = "fnandu")
	public String getFnandu() {
		return fnandu;
	}

	public void setFnandu(String fnandu) {
		this.fnandu = fnandu;
	}

	@Column(name = "fqukaijian")
	public String getFqukaijian() {
		return fqukaijian;
	}

	public void setFqukaijian(String fqukaijian) {
		this.fqukaijian = fqukaijian;
	}

	@Column(name = "fgood")
	public String getFgood() {
		return fgood;
	}

	public void setFgood(String fgood) {
		this.fgood = fgood;
	}

	@Column(name = "fbad")
	public String getFbad() {
		return fbad;
	}

	public void setFbad(String fbad) {
		this.fbad = fbad;
	}

	@Column(name = "fcount")
	public int getFcount() {
		return fcount;
	}

	public void setFcount(int fcount) {
		this.fcount = fcount;
	}

	@Column(name = "ftradetime")
	public String getFtradetime() {
		return ftradetime;
	}

	public void setFtradetime(String ftradetime) {
		this.ftradetime = ftradetime;
	}
	
	@Column(name = "fprice")
	public double getFprice() {
		return fprice;
	}

	public void setFprice(double fprice) {
		this.fprice = fprice;
	}
	
	@Column(name = "ftype")
	public int getFtype() {
		return ftype;
	}

	public void setFtype(int ftype) {
		this.ftype = ftype;
	}

	@Transient
	public String getFtype_s() {
		return CoinTypeEnum.getEnumString(this.getFtype());
	}

	public void setFtype_s(String ftype_s) {
		this.ftype_s = ftype_s;
	}

	@Transient
	public boolean isCanLend() {
		return true;
	}

	public void setCanLend(boolean canLend) {
		this.canLend = canLend;
	}

    @Transient
	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	@Column(name = "fisDefAsset")
	public boolean isFisDefAsset() {
		return fisDefAsset;
	}

	public void setFisDefAsset(boolean fisDefAsset) {
		this.fisDefAsset = fisDefAsset;
	}
}