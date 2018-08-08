package com.ditp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

//理财详情表
@Entity
@Table(name = "tb_fina_detail")
public class Detail implements java.io.Serializable {

	private static final long serialVersionUID = 3729416337090977372L;

	private String fid;
	private String ffinaId;//理财列表ID
	private String ftitle;//理财标题
	private String fcontent;//理财内容
	private String ftype;//理财类型
	
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "fid", unique = true, nullable = false)
	@GenericGenerator(name = "generator", strategy = "uuid")
	public String getFid() {
		return this.fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	@Column(name = "ffinaId", length = 32)
	public String getFfinaId() {
		return ffinaId;
	}

	public void setFfinaId(String ffinaId) {
		this.ffinaId = ffinaId;
	}

	@Column(name = "ftitle", length = 100)
	public String getFtitle() {
		return ftitle;
	}

	public void setFtitle(String ftitle) {
		this.ftitle = ftitle;
	}

	@Column(name = "fcontent", length = 1000)
	public String getFcontent() {
		return fcontent;
	}

	public void setFcontent(String fcontent) {
		this.fcontent = fcontent;
	}

	@Column(name = "ftype", length = 5)
	public String getFtype() {
		return ftype;
	}

	public void setFtype(String ftype) {
		this.ftype = ftype;
	}

	public Detail(String fid, String ffinaId, String ftitle, String fcontent, String ftype) {
		super();
		this.fid = fid;
		this.ffinaId = ffinaId;
		this.ftitle = ftitle;
		this.fcontent = fcontent;
		this.ftype = ftype;
	}

	public Detail() {
		super();
	}

	
	
}