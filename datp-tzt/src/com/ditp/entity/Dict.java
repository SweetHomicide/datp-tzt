package com.ditp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tb_sys_dict")
public class Dict implements java.io.Serializable{
	private static final long serialVersionUID = 5197484716095811052L;
	private String fid;  //id
	private String fcode;  //编码,唯一
    private String fvalue; //值
    private String fseq;  //顺序
    private String fstate = "0"; // 状态 0启用 1停用
	private String fdefault = "0"; // 是否默认，0：是；1：否
    private String fparentId;  //父ID,空值为根

    public Dict() {
        //默认构造函数
    }
    
    public Dict(String fcode, String fvalue, String fseq, String fstate, String fdefault, String fparentId) {
		super();
		this.fcode = fcode;
		this.fvalue = fvalue;
		this.fseq = fseq;
		this.fstate = fstate;
		this.fdefault = fdefault;
		this.fparentId = fparentId;
	}
	@GenericGenerator(name = "generator", strategy = "uuid")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "fId", unique = true, nullable = false)
	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	@Column(nullable = false,unique=true,length = 30)
	public String getFcode() {
		return fcode;
	}

	public void setFcode(String fcode) {
		this.fcode = fcode;
	}

	@Column(nullable = false,length = 50)
	public String getFvalue() {
		return fvalue;
	}

	public void setFvalue(String fvalue) {
		this.fvalue = fvalue;
	}

	@Column(length = 3)
	public String getFseq() {
		return fseq;
	}

	public void setFseq(String fseq) {
		this.fseq = fseq;
	}

	@Column(nullable = false,length = 1)
	public String getFstate() {
		return fstate;
	}

	public void setFstate(String fstate) {
		this.fstate = fstate;
	}

	@Column(nullable = false,length = 1)
	public String getFdefault() {
		return fdefault;
	}

	public void setFdefault(String fdefault) {
		this.fdefault = fdefault;
	}

	@Column(length = 32)
	public String getFparentId() {
		return fparentId;
	}

	public void setFparentId(String fparentId) {
		this.fparentId = fparentId;
	}

    
}
