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
import org.hibernate.annotations.GenericGenerator;

/**
 * FroleSecurity entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "frole_security")
public class FroleSecurity implements java.io.Serializable {

	// Fields

	private String fid;
	private Fsecurity fsecurity;
	private Frole frole;

	// Constructors

	/** default constructor */
	public FroleSecurity() {
	}

	/** full constructor */
	public FroleSecurity(Fsecurity fsecurity, Frole frole) {
		this.fsecurity = fsecurity;
		this.frole = frole;
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
	@JoinColumn(name = "fsecurityid")
	public Fsecurity getFsecurity() {
		return this.fsecurity;
	}

	public void setFsecurity(Fsecurity fsecurity) {
		this.fsecurity = fsecurity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "froleid")
	public Frole getFrole() {
		return this.frole;
	}

	public void setFrole(Frole frole) {
		this.frole = frole;
	}

}