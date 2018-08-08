package com.ditp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="tb_fina_wallet")
public class WalletRead implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fid;
	private String fuserId;
	private String ffinaId;
	private double ftotal;
	private double ffrozen;
	private String fcreateTime;
	private String flastUpdateTime;
	private String finaName;//理财产品名称
	private double profamount;//昨日收益
	private double sumprofamount;//理财总收益
	private String fintype;//理财类型
	private String fsymbol;//资产符号
	public String getFsymbol() {
		return fsymbol;
	}
	public void setFsymbol(String fsymbol) {
		this.fsymbol = fsymbol;
	}
	@GenericGenerator(name = "generator", strategy = "uuid")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "fid", unique = true, nullable = false,length=32)
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	@Column(name = "fuserId", length=32)
	public String getFuserId() {
		return fuserId;
	}
	public void setFuserId(String fuserId) {
		this.fuserId = fuserId;
	}
	@Column(name = "ffinaId", length=32)
	public String getFfinaId() {
		return ffinaId;
	}
	public void setFfinaId(String ffinaId) {
		this.ffinaId = ffinaId;
	}
	@Column(name = "ftotal", precision = 16, scale = 6)
	public double getFtotal() {
		return ftotal;
	}
	public void setFtotal(double ftotal) {
		this.ftotal = ftotal;
	}
	@Column(name = "ffrozen", precision = 16, scale = 6)
	public double getFfrozen() {
		return ffrozen;
	}
	public void setFfrozen(double ffrozen) {
		this.ffrozen = ffrozen;
	}
	@Column(name = "fcreateTime", length=20)
	public String getFcreateTime() {
		return fcreateTime;
	}
	public void setFcreateTime(String fcreateTime) {
		this.fcreateTime = fcreateTime;
	}
	@Column(name = "flastUpdateTime", length=20)
	public String getFlastUpdateTime() {
		return flastUpdateTime;
	}
	public void setFlastUpdateTime(String flastUpdateTime) {
		this.flastUpdateTime = flastUpdateTime;
	}
	public String getFinaName() {
		return finaName;
	}
	public void setFinaName(String finaName) {
		this.finaName = finaName;
	}
	public double getProfamount() {
		return profamount;
	}
	public void setProfamount(double profamount) {
		this.profamount = profamount;
	}
	public double getSumprofamount() {
		return sumprofamount;
	}
	public void setSumprofamount(double sumprofamount) {
		this.sumprofamount = sumprofamount;
	}
	public String getFintype() {
		return fintype;
	}
	public void setFintype(String fintype) {
		this.fintype = fintype;
	}
	
	
	
}
