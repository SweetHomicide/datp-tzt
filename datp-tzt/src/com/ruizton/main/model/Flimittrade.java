package com.ruizton.main.model;

// default package

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * Flimittrade entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "flimittrade")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Flimittrade implements java.io.Serializable {

	// Fields

	private String fid;
	private Fvirtualcointype fvirtualcointype;
	private Double fpercent;
	private Double fupprice;
	private Double fdownprice;

	// Constructors


	/** default constructor */
	public Flimittrade() {
	}

	/** full constructor */
	public Flimittrade(Fvirtualcointype fvirtualcointype, Double fpercent) {
		this.fvirtualcointype = fvirtualcointype;
		this.fpercent = fpercent;
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fvid")
	public Fvirtualcointype getFvirtualcointype() {
		return this.fvirtualcointype;
	}

	public void setFvirtualcointype(Fvirtualcointype fvirtualcointype) {
		this.fvirtualcointype = fvirtualcointype;
	}

	@Column(name = "fpercent", precision = 16, scale = 6)
	public Double getFpercent() {
		return this.fpercent;
	}

	public void setFpercent(Double fpercent) {
		this.fpercent = fpercent;
	}

	@Column(name = "fupprice", precision = 16, scale = 6)
	public Double getFupprice() {
		return fupprice;
	}

	public void setFupprice(Double fupprice) {
		this.fupprice = fupprice;
	}

	@Column(name = "fdownprice", precision = 16, scale = 6)
	public Double getFdownprice() {
		return fdownprice;
	}

	public void setFdownprice(Double fdownprice) {
		this.fdownprice = fdownprice;
	}
}