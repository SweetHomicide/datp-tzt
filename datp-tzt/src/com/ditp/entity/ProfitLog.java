package com.ditp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

//用户理财收益日志表
@Entity
@Table(name = "tb_fina_profitlog")
public class ProfitLog implements java.io.Serializable {

	private static final long serialVersionUID = 798872969518683206L;
	
	private String fid;
	private String ffinaWalletid;//理财产品ID
	private double famount;//收益金额
	private double fproportion;//当日收益率
	private String fcreateTime;//创建时间
	private String fuserid;//用户ID
	private String finaName;//理财名称
	private String ffinaId;//理财列表ID
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
	
	@Column(name = "ffinaWalletid", length = 32)
	public String getFfinaWalletid() {
		return ffinaWalletid;
	}
	public void setFfinaWalletid(String ffinaWalletid) {
		this.ffinaWalletid = ffinaWalletid;
	}
	
	@Column(name = "fcreateTime",length = 20)
	public String getFcreateTime() {
		return fcreateTime;
	}
	public void setFcreateTime(String fcreateTime) {
		this.fcreateTime = fcreateTime;
	}
	
	@Column(name = "fuserid",length = 32)
	public String getFuserid() {
		return fuserid;
	}
	public void setFuserid(String fuserid) {
		this.fuserid = fuserid;
	}
	
	@Column(name = "famount", precision = 16, scale = 6)
	public double getFamount() {
		return famount;
	}
	public void setFamount(double famount) {
		this.famount = famount;
	}
	
	@Column(name = "fproportion", precision = 8, scale = 4)
	public double getFproportion() {
		return fproportion;
	}
	public void setFproportion(double fproportion) {
		this.fproportion = fproportion;
	}
	
	
	public ProfitLog(String fid, String ffinaWalletid, double famount, double fproportion, String fcreateTime,
			String fuserid,String finaName) {
		super();
		this.fid = fid;
		this.ffinaWalletid = ffinaWalletid;
		this.famount = famount;
		this.fproportion = fproportion;
		this.fcreateTime = fcreateTime;
		this.fuserid = fuserid;
		this.finaName=finaName;
	}
	public ProfitLog() {
		super();
	}
	@Column(name = "ffinaId",length = 32)
	public String getFfinaId() {
		return ffinaId;
	}
	public void setFfinaId(String ffinaId) {
		this.ffinaId = ffinaId;
	}
	@Transient
	public String getFinaName() {
		return finaName;
	}
	public void setFinaName(String finaName) {
		this.finaName = finaName;
	}

	
}
