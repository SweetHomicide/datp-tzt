package com.ruizton.main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * Fapi entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "fapi")
public class Fapi implements java.io.Serializable {

	// Fields

	private String fid;
	private String fpartner;
	private String fsecret;
	private String label ;
	
	private Fuser fuser ;
	// Constructors

	/** default constructor */
	public Fapi() {
	}

	/** full constructor */
	public Fapi(String fpartner, String fsecret) {
		this.fpartner = fpartner;
		this.fsecret = fsecret;
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

	@Column(name = "fpartner", length = 128)
	public String getFpartner() {
		return this.fpartner;
	}

	public void setFpartner(String fpartner) {
		this.fpartner = fpartner;
	}

	@Column(name = "fsecret", length = 256)
	public String getFsecret() {
		return this.fsecret;
	}

	public void setFsecret(String fsecret) {
		this.fsecret = fsecret;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="fuser")
	public Fuser getFuser() {
		return fuser;
	}

	public void setFuser(Fuser fuser) {
		this.fuser = fuser;
	}

	@Column(name="label")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	
	
}