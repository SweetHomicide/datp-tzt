package com.ruizton.main.model;

// default package

import java.sql.Timestamp;
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
 * Fintrolinfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "fintrolinfo")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Fintrolinfo implements java.io.Serializable {

	// Fields

	private String fid;
	private Fuser fuser;
	private String ftitle;
	private Double fqty;
	private Timestamp fcreatetime;
	private boolean fiscny;

	// Constructors


	/** default constructor */
	public Fintrolinfo() {
	}

	/** full constructor */
	public Fintrolinfo(Fuser fuser, String ftitle, Double fqty,
			Timestamp fcreatetime) {
		this.fuser = fuser;
		this.ftitle = ftitle;
		this.fqty = fqty;
		this.fcreatetime = fcreatetime;
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
	@JoinColumn(name = "fuserid")
	public Fuser getFuser() {
		return this.fuser;
	}

	public void setFuser(Fuser fuser) {
		this.fuser = fuser;
	}

	@Column(name = "ftitle", length = 100)
	public String getFtitle() {
		return this.ftitle;
	}

	public void setFtitle(String ftitle) {
		this.ftitle = ftitle;
	}

	@Column(name = "fqty", precision = 16, scale = 6)
	public Double getFqty() {
		return this.fqty;
	}

	public void setFqty(Double fqty) {
		this.fqty = fqty;
	}

	@Column(name = "fcreatetime", length = 0)
	public Timestamp getFcreatetime() {
		return this.fcreatetime;
	}

	public void setFcreatetime(Timestamp fcreatetime) {
		this.fcreatetime = fcreatetime;
	}
	
	@Column(name = "fiscny")
	public boolean isFiscny() {
		return fiscny;
	}

	public void setFiscny(boolean fiscny) {
		this.fiscny = fiscny;
	}

}