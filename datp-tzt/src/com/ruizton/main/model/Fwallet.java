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

/**
 * Fwallet entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "fwallet")
//@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Fwallet implements java.io.Serializable {

	// Fields
	private String fid;
	private double ftotalRmb;
	private double ffrozenRmb;
	private Timestamp flastUpdateTime;
	private Fuser fuser ;
	private Set<Fuser> fusers = new HashSet<Fuser>(0);
	private int version ;
	
	private double fborrowCny;//已借款
	private double fHaveAppointBorrowCny ;//已预约借款
	
	private double fcanLendCny;//可放款
	private double ffrozenLendCny;//冻结放款
	private double falreadyLendCny;//已放款
	// Constructors

	/** default constructor */
	public Fwallet() {
	}

	/** full constructor */
	public Fwallet(Fuser fuser, double ftotalRmb, double ffrozenRmb,
			Timestamp flastUpdateTime, Set<Fuser> fusers) {
		this.ftotalRmb = ftotalRmb;
		this.ffrozenRmb = ffrozenRmb;
		this.flastUpdateTime = flastUpdateTime;
		this.fusers = fusers;
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

	@Column(name = "fTotalRMB")
	public double getFtotalRmb() {
		return this.ftotalRmb;
	}

	public void setFtotalRmb(double ftotalRmb) {
		this.ftotalRmb = ftotalRmb;
	}

	@Column(name = "fFrozenRMB")
	public double getFfrozenRmb() {
		return this.ffrozenRmb;
	}

	public void setFfrozenRmb(double ffrozenRmb) {
		this.ffrozenRmb = ffrozenRmb;
	}

	@Column(name = "fLastUpdateTime", length = 0)
	public Timestamp getFlastUpdateTime() {
		return this.flastUpdateTime;
	}

	public void setFlastUpdateTime(Timestamp flastUpdateTime) {
		this.flastUpdateTime = flastUpdateTime;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "fwallet")
	public Set<Fuser> getFusers() {
		return this.fusers;
	}

	public void setFusers(Set<Fuser> fusers) {
		this.fusers = fusers;
	}
	@Version
    @Column(name="version")
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Transient
	public Fuser getFuser() {
		Set<Fuser> fusers = this.getFusers() ;
		if(fusers!=null && fusers.size()>0){
			return fusers.iterator().next() ;
		}
		return null;
	}

	public void setFuser(Fuser fuser) {
		this.fuser = fuser;
	}

	@Column(name = "fBorrowCny", precision = 16, scale = 6)
	public double getFborrowCny() {
		return this.fborrowCny;
	}

	public void setFborrowCny(double fborrowCny) {
		this.fborrowCny = fborrowCny;
	}

	@Column(name = "fCanLendCny", precision = 16, scale = 6)
	public double getFcanLendCny() {
		return this.fcanLendCny;
	}

	public void setFcanLendCny(double fcanLendCny) {
		this.fcanLendCny = fcanLendCny;
	}

	@Column(name = "fFrozenLendCny", precision = 16, scale = 6)
	public double getFfrozenLendCny() {
		return this.ffrozenLendCny;
	}

	public void setFfrozenLendCny(double ffrozenLendCny) {
		this.ffrozenLendCny = ffrozenLendCny;
	}

	@Column(name = "fAlreadyLendCny", precision = 16, scale = 6)
	public double getFalreadyLendCny() {
		return this.falreadyLendCny;
	}

	public void setFalreadyLendCny(double falreadyLendCny) {
		this.falreadyLendCny = falreadyLendCny;
	}

	@Column(name="fHaveAppointBorrowCny")
	public double getfHaveAppointBorrowCny() {
		return fHaveAppointBorrowCny;
	}

	public void setfHaveAppointBorrowCny(double fHaveAppointBorrowCny) {
		this.fHaveAppointBorrowCny = fHaveAppointBorrowCny;
	}

	
	
	
}