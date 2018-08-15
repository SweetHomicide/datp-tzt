package com.ruizton.main.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.annotations.GenericGenerator;

/**
 * FvirtualaddressWithdraw entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "fvirtualaddress_withdraw")
public class FvirtualaddressWithdraw implements java.io.Serializable {

	// Fields

	private String fid;
	private int version;
	private Fvirtualcointype fvirtualcointype; //币种类型
	private String fadderess;//地址
	private Fuser fuser ;//用户
	private Timestamp fcreateTime;
	private String fremark;

	// Constructors

	/** default constructor */
	public FvirtualaddressWithdraw() {
	}

	/** full constructor */
	public FvirtualaddressWithdraw(Fvirtualcointype fvirtualcointype,
			String fadderess, int fuid, Timestamp fcreateTime) {
		this.fvirtualcointype = fvirtualcointype;
		this.fadderess = fadderess;
		this.fcreateTime = fcreateTime;
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

	@Version
	@Column(name = "version")
	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fVi_fId")
	public Fvirtualcointype getFvirtualcointype() {
		return this.fvirtualcointype;
	}

	public void setFvirtualcointype(Fvirtualcointype fvirtualcointype) {
		this.fvirtualcointype = fvirtualcointype;
	}

	@Column(name = "fAdderess", length = 128)
	public String getFadderess() {
		return this.fadderess;
	}

	public void setFadderess(String fadderess) {
		this.fadderess = fadderess;
	}

	@Column(name = "fCreateTime", length = 0)
	public Timestamp getFcreateTime() {
		return this.fcreateTime;
	}

	public void setFcreateTime(Timestamp fcreateTime) {
		this.fcreateTime = fcreateTime;
	}
	
	@Column(name = "fremark")
	public String getFremark() {
		return fremark;
	}

	public void setFremark(String fremark) {
		this.fremark = fremark;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fuid")
	public Fuser getFuser() {
		return fuser;
	}

	public void setFuser(Fuser fuser) {
		this.fuser = fuser;
	}

}