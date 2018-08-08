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
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

/**
 * Fvirtualwallet entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "fvirtualwallet")
// @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Fvirtualwallet implements java.io.Serializable {

	// Fields

	private String fid;
	private Fvirtualcointype fvirtualcointype;
	private double ftotal;
	private double ffrozen;
	private Timestamp flastUpdateTime;
	private Fuser fuser;
	private int version;

	private double fborrowBtc;// 已借款
	private double fHaveAppointBorrowBtc;// 已预约借款

	private double fcanlendBtc;// 可放款
	private double ffrozenLendBtc;// 冻结放款
	private double falreadyLendBtc;// 已放款

	// Constructors

	/** default constructor */
	public Fvirtualwallet() {
	}

	/** full constructor */
	public Fvirtualwallet(Fvirtualcointype fvirtualcointype, double ftotal,
			double ffrozen, Timestamp flastUpdateTime, Set<Fuser> fusers) {
		this.fvirtualcointype = fvirtualcointype;
		this.ftotal = ftotal;
		this.ffrozen = ffrozen;
		this.flastUpdateTime = flastUpdateTime;
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fVi_fId")
	public Fvirtualcointype getFvirtualcointype() {
		return this.fvirtualcointype;
	}

	public void setFvirtualcointype(Fvirtualcointype fvirtualcointype) {
		this.fvirtualcointype = fvirtualcointype;
	}

	@Column(name = "fTotal", precision = 12, scale = 0)
	public double getFtotal() {
		return this.ftotal;
	}

	public void setFtotal(double ftotal) {
		this.ftotal = ftotal;
	}

	@Column(name = "fFrozen", precision = 12, scale = 0)
	public double getFfrozen() {
		return this.ffrozen;
	}

	public void setFfrozen(double ffrozen) {
		this.ffrozen = ffrozen;
	}

	@Column(name = "fLastUpdateTime", length = 0)
	public Timestamp getFlastUpdateTime() {
		return this.flastUpdateTime;
	}

	public void setFlastUpdateTime(Timestamp flastUpdateTime) {
		this.flastUpdateTime = flastUpdateTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fuid")
	public Fuser getFuser() {
		return fuser;
	}

	public void setFuser(Fuser fuser) {
		this.fuser = fuser;
	}

	@Version
	@Column(name = "version")
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Column(name = "fBorrowBtc", precision = 16, scale = 6)
	public double getFborrowBtc() {
		return this.fborrowBtc;
	}

	public void setFborrowBtc(double fborrowBtc) {
		this.fborrowBtc = fborrowBtc;
	}

	@Column(name = "fCanlendBtc", precision = 16, scale = 6)
	public double getFcanlendBtc() {
		return this.fcanlendBtc;
	}

	public void setFcanlendBtc(double fcanlendBtc) {
		this.fcanlendBtc = fcanlendBtc;
	}

	@Column(name = "fFrozenLendBtc", precision = 16, scale = 6)
	public double getFfrozenLendBtc() {
		return this.ffrozenLendBtc;
	}

	public void setFfrozenLendBtc(double ffrozenLendBtc) {
		this.ffrozenLendBtc = ffrozenLendBtc;
	}

	@Column(name = "fAlreadyLendBtc", precision = 16, scale = 6)
	public double getFalreadyLendBtc() {
		return this.falreadyLendBtc;
	}

	public void setFalreadyLendBtc(double falreadyLendBtc) {
		this.falreadyLendBtc = falreadyLendBtc;
	}

	@Column(name = "fHaveAppointBorrowBtc")
	public double getfHaveAppointBorrowBtc() {
		return fHaveAppointBorrowBtc;
	}

	public void setfHaveAppointBorrowBtc(double fHaveAppointBorrowBtc) {
		this.fHaveAppointBorrowBtc = fHaveAppointBorrowBtc;
	}

}