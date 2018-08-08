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

import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;

//委托单
public class Fentrustbean implements java.io.Serializable {

	/**
	 * 
	 */
	private String fid;
	//private Fvirtualcointype fvirtualcointype;
	private String fuserid;   //用户id
	private String fwalletid;  //钱包id
	private String fvirtualcointypeid;
	private Timestamp fcreateTime;
	private Timestamp flastUpdatTime;
	private int fentrustType;// EntrustTypeEnum
	//private String fentrustType_s;
	private double fprize;
	private double famount;
	private double ffees ;
	private double fleftfees ;
	private double fsuccessAmount;
	private double fcount;
	private double fleftCount;// 未成交数量
	private int fstatus;// EntrustStatusEnum
	//private String fstatus_s;
	private boolean fisLimit;// 按照市价完全成交的订单
	private int version;
	private boolean fhasSubscription;

	// Constructors
	/** default constructor */
	public Fentrustbean() {
	}

	public String getFid() {
		return this.fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public Fentrustbean(Fentrust fen) {
		super();
		this.fid = fen.getFid();
		if(fen.getFuser()!=null){
		this.fuserid = fen.getFuser().getFid();
         if(fen.getFuser().getFwallet()!=null){
		this.fwalletid = fen.getFuser().getFwallet().getFid();
         }
		}
		if (fen.getFvirtualcointype()!=null) {
			this.fvirtualcointypeid=fen.getFvirtualcointype().getFid();
		}
		this.fcreateTime = fen.getFcreateTime();
		//this.flastUpdatTime = fen.getFlastUpdatTime();
		this.fentrustType = fen.getFentrustType();
		this.fprize = fen.getFprize();
		this.famount = fen.getFamount();
		this.ffees = fen.getFfees();
		this.fleftfees = fen.getFleftfees();
		this.fsuccessAmount = fen.getFsuccessAmount();
		this.fcount = fen.getFcount();
		this.fleftCount = fen.getFleftCount();
		this.fstatus = fen.getFstatus();
		this.version = fen.getVersion();
	}

	public Fentrustbean(Fentrust f,Object object) {
		super();
		this.fprize = f.getFprize();
		this.fleftCount = f.getFleftCount();
	}

	public String getFuserid() {
		return fuserid;
	}

	public void setFuserid(String fuserid) {
		this.fuserid = fuserid;
	}

	public String getFwalletid() {
		return fwalletid;
	}

	public void setFwalletid(String fwalletid) {
		this.fwalletid = fwalletid;
	}

	public Timestamp getFcreateTime() {
		return this.fcreateTime;
	}

	public void setFcreateTime(Timestamp fcreateTime) {
		this.fcreateTime = fcreateTime;
	}

	public int getFentrustType() {
		return this.fentrustType;
	}

	public void setFentrustType(int fentrustType) {
		this.fentrustType = fentrustType;
	}

	public double getFprize() {
		return this.fprize;
	}

	public void setFprize(double fprize) {
		this.fprize = fprize;
	}

	public double getFamount() {
		return this.famount;
	}

	public void setFamount(double famount) {
		this.famount = famount;
	}

	public double getFcount() {
		return this.fcount;
	}

	public void setFcount(double fcount) {
		this.fcount = fcount;
	}

	public int getFstatus() {
		return this.fstatus;
	}

	public void setFstatus(int fstatus) {
		this.fstatus = fstatus;
	}

	public Timestamp getFlastUpdatTime() {
		return flastUpdatTime;
	}

	public void setFlastUpdatTime(Timestamp flastUpdatTime) {
		this.flastUpdatTime = flastUpdatTime;
	}

	public double getFleftCount() {
		return fleftCount;
	}

	public void setFleftCount(double fleftCount) {
		this.fleftCount = fleftCount;
	}

	public double getFsuccessAmount() {
		return fsuccessAmount;
	}

	public void setFsuccessAmount(double fsuccessAmount) {
		this.fsuccessAmount = fsuccessAmount;
	}

	public boolean isFisLimit() {
		return fisLimit;
	}

	public void setFisLimit(boolean fisLimit) {
		this.fisLimit = fisLimit;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public double getFfees() {
		return ffees;
	}

	public void setFfees(double ffees) {
		this.ffees = ffees;
	}

	public double getFleftfees() {
		return fleftfees;
	}

	public void setFleftfees(double fleftfees) {
		this.fleftfees = fleftfees;
	}
	
	public boolean isFhasSubscription() {
		return fhasSubscription;
	}

	public void setFhasSubscription(boolean fhasSubscription) {
		this.fhasSubscription = fhasSubscription;
	}

	public String getFvirtualcointypeid() {
		return fvirtualcointypeid;
	}

	public void setFvirtualcointypeid(String fvirtualcointypeid) {
		this.fvirtualcointypeid = fvirtualcointypeid;
	}

}