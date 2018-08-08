package com.ruizton.main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.ruizton.util.HTMLSpirit;

/**
 * Fabout entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "fabout")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Fabout implements java.io.Serializable {

	// Fields

	private String fid;
	private String ftitle;
	private String fcontent;
	private String fcontent_s;
	private String ftype;

	// Constructors

	/** default constructor */
	public Fabout() {
	}

	/** full constructor */
	public Fabout(String ftitle, String fcontent) {
		this.ftitle = ftitle;
		this.fcontent = fcontent;
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

	@Column(name = "ftitle", length = 128)
	public String getFtitle() {
		return this.ftitle;
	}

	public void setFtitle(String ftitle) {
		this.ftitle = ftitle;
	}

	@Column(name = "fcontent", length = 65535)
	public String getFcontent() {
		return this.fcontent;
	}

	public void setFcontent(String fcontent) {
		this.fcontent = fcontent;
	}

	@Transient
	public String getFcontent_s() {
		return HTMLSpirit.delHTMLTag(getFcontent());
	}

	public void setFcontent_s(String fcontent_s) {
		this.fcontent_s = fcontent_s;
	}
	
	@Column(name = "ftype")
	public String getFtype() {
		return ftype;
	}

	public void setFtype(String ftype) {
		this.ftype = ftype;
	}


}