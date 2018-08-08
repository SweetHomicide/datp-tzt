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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

import com.ruizton.main.Enum.AdminLevelEnum;
import com.ruizton.main.Enum.AdminStatusEnum;

/**
 * Fadmin entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "fadmin")
// @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Fadmin implements java.io.Serializable {

	// Fields

	private String fid;
	private String fname;
	private String fpassword;
	private Timestamp fcreateTime;
//	private int flevel;
//	private String flevel_s;
	private int fstatus;
	private String fstatus_s;
	private int version;
	private Frole frole;

	@Transient
	public String getFstatus_s() {
		if (this.fstatus == AdminStatusEnum.FORBBIN_VALUE) {
			this.setFstatus_s(AdminStatusEnum
					.getEnumString(AdminStatusEnum.FORBBIN_VALUE));
		} else {
			this.setFstatus_s(AdminStatusEnum
					.getEnumString(AdminStatusEnum.NORMAL_VALUE));
		}
		return fstatus_s;
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
	}

	@Column(name = "fstatus")
	public int getFstatus() {
		return fstatus;
	}

	public void setFstatus(int fstatus) {
		this.fstatus = fstatus;
	}

	// Constructors
//	@Transient
//	public String getFlevel_s() {
//		if (this.flevel == AdminLevelEnum.SUPER_VALUE) {
//			this.setFlevel_s(AdminLevelEnum
//					.getEnumString(AdminLevelEnum.SUPER_VALUE));
//		} else {
//			this.setFlevel_s(AdminLevelEnum
//					.getEnumString(AdminLevelEnum.NORMAL_VALUE));
//		}
//		return flevel_s;
//	}
//
//	public void setFlevel_s(String flevel_s) {
//		this.flevel_s = flevel_s;
//	}

	/** default constructor */
	public Fadmin() {
	}

	/** full constructor */
	public Fadmin(String fname, String fpassword, Timestamp fcreateTime) {
		this.fname = fname;
		this.fpassword = fpassword;
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

	@Column(name = "fName", length = 1024)
	public String getFname() {
		return this.fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	@Column(name = "fPassword", length = 1024)
	public String getFpassword() {
		return this.fpassword;
	}

	public void setFpassword(String fpassword) {
		this.fpassword = fpassword;
	}

	@Column(name = "fCreateTime", length = 0)
	public Timestamp getFcreateTime() {
		return this.fcreateTime;
	}

	public void setFcreateTime(Timestamp fcreateTime) {
		this.fcreateTime = fcreateTime;
	}
//
//	@Column(name = "fLevel")
//	public int getFlevel() {
//		return this.flevel;
//	}
//
//	public void setFlevel(int flevel) {
//		this.flevel = flevel;
//	}

	@Version
	@Column(name = "version")
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "froleId")
	public Frole getFrole() {
		return this.frole;
	}

	public void setFrole(Frole frole) {
		this.frole = frole;
	}

}