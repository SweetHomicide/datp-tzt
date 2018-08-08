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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.service.front.FrontUserService;

import net.sf.json.JSONObject;

/**
 * Fentrust entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "fentrust")
// @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
//委托单
public class Fentrust implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2083829370997350052L;
	// Fields
	private String fid;
	private Fvirtualcointype fvirtualcointype;
	private Fuser fuser;
	private Timestamp fcreateTime;
	private Timestamp flastUpdatTime;
	private int fentrustType;// EntrustTypeEnum
	private String fentrustType_s;
	private double fprize;
	private double famount;
	private double ffees ;
	private double fleftfees ;
	private double fsuccessAmount;
	private double fcount;
	private double fleftCount;// 未成交数量
	private int fstatus;// EntrustStatusEnum
	private String fstatus_s;
	private boolean fisLimit;// 按照市价完全成交的订单
	private int version;
	private boolean fhasSubscription;

	// Constructors



	/** default constructor */
	public Fentrust() {
	}

	/** full constructor */
	public Fentrust(Fvirtualcointype fvirtualcointype,
			Fentrustplan fentrustplan, Fuser fuser, Timestamp fcreateTime,
			int fentrustType, double fprize, double famount, double fcount,
			int fstatus, Set<Fentrustlog> fentrustlogs) {
		this.fvirtualcointype = fvirtualcointype;
		this.fuser = fuser;
		this.fcreateTime = fcreateTime;
		this.fentrustType = fentrustType;
		this.fprize = fprize;
		this.famount = famount;
		this.fcount = fcount;
		this.fstatus = fstatus;
	}
	
	public Fentrust(Fentrustbean fen) {
		this.fvirtualcointype = new Fvirtualcointype();
		this.fuser = new Fuser();
		this.fuser.setFid(fen.getFuserid());
		this.fuser.setFwallet(new Fwallet());
		this.fuser.getFwallet().setFid(fen.getFwalletid());
		this.fvirtualcointype.setFid(fen.getFvirtualcointypeid());
		this.fcreateTime = fen.getFcreateTime();
		this.fentrustType = fen.getFentrustType();
		this.fprize = fen.getFprize();
		this.famount = fen.getFamount();
		this.fstatus = fen.getFstatus();
		//this.flastUpdatTime = fen.getFlastUpdatTime();
		this.ffees = fen.getFfees();
		this.fleftfees = fen.getFleftfees();
		this.fsuccessAmount = fen.getFsuccessAmount();
		this.fcount = fen.getFcount();
		this.fleftCount = fen.getFleftCount();
		this.version = fen.getVersion();
	}
	
	public Fentrust(JSONObject fenJsonObject) {
		this.fvirtualcointype = new Fvirtualcointype();
		this.fuser = new Fuser();
		this.fuser.setFwallet(new Fwallet());
		try {
			this.fid=fenJsonObject.getString("fid");
			this.fuser.setFid(fenJsonObject.getString("fuserid"));
			this.fuser.getFwallet().setFid(fenJsonObject.getString("fwalletid"));
			this.fvirtualcointype.setFid(fenJsonObject.getString("fvirtualcointypeid"));
			this.fcreateTime =Timestamp.valueOf(fenJsonObject.getString("fcreateTime"));
			this.fentrustType =Integer.parseInt(fenJsonObject.getString("fentrustType"));
			this.fprize =Double.parseDouble(fenJsonObject.getString("fprize"));
			this.famount = Double.parseDouble(fenJsonObject.getString("famount"));
			this.fstatus = Integer.parseInt(fenJsonObject.getString("fstatus"));
			//this.flastUpdatTime = Timestamp.valueOf(fenJsonObject.getString("flastUpdatTime"));
			this.ffees = Double.parseDouble(fenJsonObject.getString("ffees"));
			this.fleftfees =Double.parseDouble(fenJsonObject.getString("fleftfees"));
			this.fsuccessAmount =Double.parseDouble(fenJsonObject.getString("fsuccessAmount"));
			this.fcount = Double.parseDouble(fenJsonObject.getString("fcount"));
			this.fleftCount =Double.parseDouble( fenJsonObject.getString("fleftCount"));
			this.version = Integer.parseInt(fenJsonObject.getString("version"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 
		
	}
	
	
	public Fentrust(JSONObject fenJsonObject,Object object) {
		try {
			this.fprize =Double.parseDouble(fenJsonObject.getString("fprize"));
			this.fleftCount =Double.parseDouble( fenJsonObject.getString("fleftCount"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 
		
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fVi_fId")
	public Fvirtualcointype getFvirtualcointype() {
		return this.fvirtualcointype;
	}

	public void setFvirtualcointype(Fvirtualcointype fvirtualcointype) {
		this.fvirtualcointype = fvirtualcointype;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FUs_fId")
	public Fuser getFuser() {
		return this.fuser;
	}

	public void setFuser(Fuser fuser) {
		this.fuser = fuser;
	}

	@Column(name = "fCreateTime", length = 0)
	public Timestamp getFcreateTime() {
		return this.fcreateTime;
	}

	public void setFcreateTime(Timestamp fcreateTime) {
		this.fcreateTime = fcreateTime;
	}

	@Column(name = "fEntrustType")
	public int getFentrustType() {
		return this.fentrustType;
	}

	public void setFentrustType(int fentrustType) {
		this.fentrustType = fentrustType;
	}

	@Column(name = "fPrize", precision = 12, scale = 0)
	public double getFprize() {
		return this.fprize;
	}

	public void setFprize(double fprize) {
		this.fprize = fprize;
	}

	@Column(name = "fAmount", precision = 12, scale = 0)
	public double getFamount() {
		return this.famount;
	}

	public void setFamount(double famount) {
		this.famount = famount;
	}

	@Column(name = "fCount", precision = 12, scale = 0)
	public double getFcount() {
		return this.fcount;
	}

	public void setFcount(double fcount) {
		this.fcount = fcount;
	}

	@Column(name = "fStatus")
	public int getFstatus() {
		return this.fstatus;
	}

	public void setFstatus(int fstatus) {
		this.fstatus = fstatus;
	}

	@Column(name = "flastUpdatTime")
	public Timestamp getFlastUpdatTime() {
		return flastUpdatTime;
	}

	public void setFlastUpdatTime(Timestamp flastUpdatTime) {
		this.flastUpdatTime = flastUpdatTime;
	}

	@Column(name = "fleftCount")
	public double getFleftCount() {
		return fleftCount;
	}

	public void setFleftCount(double fleftCount) {
		this.fleftCount = fleftCount;
	}

	@Column(name = "fsuccessAmount")
	public double getFsuccessAmount() {
		return fsuccessAmount;
	}

	public void setFsuccessAmount(double fsuccessAmount) {
		this.fsuccessAmount = fsuccessAmount;
	}

	@Column(name = "fisLimit")
	public boolean isFisLimit() {
		return fisLimit;
	}

	public void setFisLimit(boolean fisLimit) {
		this.fisLimit = fisLimit;
	}

	@Version
	@Column(name = "version")
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Transient
	public String getFentrustType_s() {
		int type = this.getFentrustType();
		return EntrustTypeEnum.getEnumString(type);
	}

	public void setFentrustType_s(String fentrustType_s) {
		this.fentrustType_s = fentrustType_s;
	}

	@Transient
	public String getFstatus_s() {
		int status = this.getFstatus();
		return EntrustStatusEnum.getEnumString(status);
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
	}

	@Column(name="ffees")
	public double getFfees() {
		return ffees;
	}

	public void setFfees(double ffees) {
		this.ffees = ffees;
	}

	@Column(name="fleftfees")
	public double getFleftfees() {
		return fleftfees;
	}

	public void setFleftfees(double fleftfees) {
		this.fleftfees = fleftfees;
	}
	
	@Column(name="fhasSubscription")
	public boolean isFhasSubscription() {
		return fhasSubscription;
	}

	public void setFhasSubscription(boolean fhasSubscription) {
		this.fhasSubscription = fhasSubscription;
	}

}