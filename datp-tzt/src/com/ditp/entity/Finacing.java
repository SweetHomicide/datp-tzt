package com.ditp.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="tb_fina_finacing")
public class Finacing implements java.io.Serializable{
	
	private String fid;
	private String  fname;
	private String ftype;//1、灵活         随时可以存取2锁定期限  在锁定时间不可取出（字典表）
	private String fassetsType;//支付的资产类型 1、数字资产类型 2、人民币（字典表)
	private String fstatus;//0 未发布 1 发布
	private double fproportion;
	private String flimit;//产品锁定时间内不允许取出，单位天
	private String fbeginTime;
	private String fendTime;
	private double fminAmount;
	private double fmaxAmount;
	private String fcreateTime;
	private String flastUpdateTime;
	private String fcycle;//日，周，年  单位天
	private String fvitypeId;//读取数字资产类型
	private String fdtype;//字典表映射
	private List<Detail> detailList;
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

	@Column(unique=true,name = "fname", length=20)
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	@Column(name = "ftype", length=5)
	public String getFtype() {
		return ftype;
	}
	public void setFtype(String ftype) {
		this.ftype = ftype;
	}
	@Column(name = "fassetsType", length=5)
	public String getFassetsType() {
		return fassetsType;
	}
	public void setFassetsType(String fassetsType) {
		this.fassetsType = fassetsType;
	}
	@Column(name = "fstatus", length=1)
	public String getFstatus() {
		return fstatus;
	}
	public void setFstatus(String fstatus) {
		this.fstatus = fstatus;
	}
	@Column(name = "fproportion", precision = 8, scale = 4)
	public double getFproportion() {
		return fproportion;
	}
	public void setFproportion(double fproportion) {
		this.fproportion = fproportion;
	}
	@Column(name = "flimit", length=4)
	public String getFlimit() {
		return flimit;
	}
	public void setFlimit(String flimit) {
		this.flimit = flimit;
	}
	@Column(name = "fbeginTime", length=20)
	public String getFbeginTime() {
		return fbeginTime;
	}
	public void setFbeginTime(String fbeginTime) {
		this.fbeginTime = fbeginTime;
	}
	@Column(name = "fendTime", length=20)
	public String getFendTime() {
		return fendTime;
	}
	public void setFendTime(String fendTime) {
		this.fendTime = fendTime;
	}
	@Column(name = "fminAmount", precision = 16, scale = 6)
	public double getFminAmount() {
		return fminAmount;
	}
	public void setFminAmount(double fminAmount) {
		this.fminAmount = fminAmount;
	}
	@Column(name = "fmaxAmount", precision = 16, scale = 6)
	public double getFmaxAmount() {
		return fmaxAmount;
	}
	public void setFmaxAmount(double fmaxAmount) {
		this.fmaxAmount = fmaxAmount;
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
	@Column(name = "fcycle", length=4)
	public String getFcycle() {
		return fcycle;
	}
	public void setFcycle(String fcycle) {
		this.fcycle = fcycle;
	}
	@Column(name = "fvitypeId", length=32)
	public String getFvitypeId() {
		return fvitypeId;
	}
	public void setFvitypeId(String fvitypeId) {
		this.fvitypeId = fvitypeId;
	}
	@Transient
	public String getFdtype() {
		return fdtype;
	}
	public void setFdtype(String fdtype) {
		this.fdtype = fdtype;
	}
	@Transient
	public List<Detail> getDetailList() {
		return detailList;
	}
	public void setDetailList(List<Detail> detailList) {
		this.detailList = detailList;
	}
	
	
}
