package com.ruizton.main.model;
// default package

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author   Dylan
 * @data     2018年8月9日
 * @typeName Ftradehistory
 * 说明 ： 交易记录表  对应的是Fvirtualcointype币种类型表
 *
 */
@Entity
@Table(name = "ftradehistory")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Ftradehistory implements java.io.Serializable {

	// Fields

	private String fid;
	private Date fdate;
	private Double fprice;
	private String fvid;//lwj   对应的是Fvirtualcointype表中的id 
	private Double ftotal;
//	private Fuser fuser;

	// Constructors

	/** default constructor */
	public Ftradehistory() {
	}

	/** full constructor */
	public Ftradehistory(Date fdate, Double fprice) {
		this.fdate = fdate;
		this.fprice = fprice;
	}

	// Property accessors
	@GenericGenerator(name = "generator", strategy = "uuid")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "fid", unique = true, nullable = false)
	public String getFid() {
		return this.fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fdate", length = 0)
	public Date getFdate() {
		return this.fdate;
	}

	public void setFdate(Date fdate) {
		this.fdate = fdate;
	}

	@Column(name = "fprice", precision = 16, scale = 6)
	public Double getFprice() {
		return this.fprice;
	}

	public void setFprice(Double fprice) {
		this.fprice = fprice;
	}
	
	@Column(name = "fvid")
	public String getFvid() {//lwj
		return fvid;
	}

	public void setFvid(String fvid) {//lwj
		this.fvid = fvid;
	}
	
	@Column(name = "ftotal")
	public Double getFtotal() {
		return ftotal;
	}

	public void setFtotal(Double ftotal) {
		this.ftotal = ftotal;
	}
//
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "fuserid")
//	public Fuser getFuser() {
//		return fuser;
//	}
//
//	public void setFuser(Fuser fuser) {
//		this.fuser = fuser;
//	}

}