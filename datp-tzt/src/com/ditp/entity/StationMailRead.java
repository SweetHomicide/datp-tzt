package com.ditp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 站内信
 * 
 * @author Thinkpad
 *
 */
@Entity
public class StationMailRead implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fid;
	private String fuserid;// 用户id
	private String ftitle;// 标题
	private String fcontent;//内容
	private String fstatus;//状态0未读 1已读
	private String ftype;//类别  买入  卖出  众筹  兑换 ...
	private String ftime;//创建时间
	private String fsendUserid;//发送人
	private String fsysMailId;//系统消息id
	private String fsendUserName;//发送人名称
	
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "fid", unique = true, nullable = false)
	@GenericGenerator(name = "generator", strategy = "uuid")
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	@Column(length = 32)
	public String getFuserid() {
		return fuserid;
	}
	public void setFuserid(String fuserid) {
		this.fuserid = fuserid;
	}
	@Column(length = 30)
	public String getFtitle() {
		return ftitle;
	}
	public void setFtitle(String ftitle) {
		this.ftitle = ftitle;
	}
	@Column(length = 200)
	public String getFcontent() {
		return fcontent;
	}
	public void setFcontent(String fcontent) {
		this.fcontent = fcontent;
	}
	@Column(length = 1)
	public String getFstatus() {
		return fstatus;
	}
	public void setFstatus(String fstatus) {
		this.fstatus = fstatus;
	}
	@Column(length = 5)
	public String getFtype() {
		return ftype;
	}
	public void setFtype(String ftype) {
		this.ftype = ftype;
	}
	@Column(length = 20)
	public String getFtime() {
		return ftime;
	}
	public void setFtime(String ftime) {
		this.ftime = ftime;
	}
	@Column(length = 32)
	public String getFsendUserid() {
		return fsendUserid;
	}
	public void setFsendUserid(String fsendUserid) {
		this.fsendUserid = fsendUserid;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getFsendUserName() {
		return fsendUserName;
	}
	public void setFsendUserName(String fsendUserName) {
		this.fsendUserName = fsendUserName;
	}
	public String getFsysMailId() {
		return fsysMailId;
	}
	public void setFsysMailId(String fsysMailId) {
		this.fsysMailId = fsysMailId;
	}
	
	
}
