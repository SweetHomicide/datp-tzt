package com.ditp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

//理财详情表
@Entity
public class TradeLogRead implements java.io.Serializable {

	private static final long serialVersionUID = -877480681718166610L;
	
	private String fid;
	private String ffinaId;//理财列表ID
	private String fuserId;//用户id
	private double famount;//金额
	private String ftype;//交易类型 0 买入 1 取出,提现
	private String fcreateTime;//创建时间
	private String flastUpdateTime;//最后更新时间
	private String finaName;
	private String ffinaWalletid;//钱包表id
	private String beginTime;
	private String endTime;
	private String fuserName;
	private String fsymbol;//符号
	public String getFuserName() {
		return fuserName;
	}
	public void setFuserName(String fuserName) {
		this.fuserName = fuserName;
	}
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "fid", unique = true, nullable = false)
	@GenericGenerator(name = "generator", strategy = "uuid")
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	
	@Column(name = "ffinaId", length = 32)
	public String getFfinaId() {
		return ffinaId;
	}
	public void setFfinaId(String ffinaId) {
		this.ffinaId = ffinaId;
	}
	
	@Column(name = "fuserId", length = 32)
	public String getFuserId() {
		return fuserId;
	}
	public void setFuserId(String fuserId) {
		this.fuserId = fuserId;
	}
	
	@Column(name = "famount", precision = 16, scale = 6)
	public double getFamount() {
		return famount;
	}
	public void setFamount(double famount) {
		this.famount = famount;
	}
	
	@Column(name = "ftype", length = 5)
	public String getFtype() {
		return ftype;
	}
	public void setFtype(String ftype) {
		this.ftype = ftype;
	}
	
	@Column(name = "fcreateTime", length = 20)
	public String getFcreateTime() {
		return fcreateTime;
	}
	public void setFcreateTime(String fcreateTime) {
		this.fcreateTime = fcreateTime;
	}
	
	@Column(name = "flastUpdateTime", length = 20)
	public String getFlastUpdateTime() {
		return flastUpdateTime;
	}
	public void setFlastUpdateTime(String flastUpdateTime) {
		this.flastUpdateTime = flastUpdateTime;
	}
	
	
	public TradeLogRead(String fid, String ffinaId, String fuserId, double famount, String ftype, String fcreateTime,
			String flastUpdateTime) {
		super();
		this.fid = fid;
		this.ffinaId = ffinaId;
		this.fuserId = fuserId;
		this.famount = famount;
		this.ftype = ftype;
		this.fcreateTime = fcreateTime;
		this.flastUpdateTime = flastUpdateTime;
	}
	public TradeLogRead() {
		super();
	}	
	
	public String getFinaName() {
		return finaName;
	}
	public void setFinaName(String finaName) {
		this.finaName = finaName;
	}
	@Column(name = "ffinaWalletid", length = 32)
	public String getFfinaWalletid() {
		return ffinaWalletid;
	}
	public void setFfinaWalletid(String ffinaWalletid) {
		this.ffinaWalletid = ffinaWalletid;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getFsymbol() {
		return fsymbol;
	}
	public void setFsymbol(String fsymbol) {
		this.fsymbol = fsymbol;
	}

}
