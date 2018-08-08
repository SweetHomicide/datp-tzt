package com.ruizton.main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;


/**
 * Fwithdrawfees entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="fwithdrawfees")
public class Fwithdrawfees  implements java.io.Serializable {


    // Fields    

     private String fid;
     private double ffee;
     private int flevel;

     private int version ;
    // Constructors

    /** default constructor */
    public Fwithdrawfees() {
    }

    
    /** full constructor */
    public Fwithdrawfees(double ffee, int flevel) {
        this.ffee = ffee;
        this.flevel = flevel;
    }

   
    // Property accessors
    @GenericGenerator(name="generator", strategy="native")@Id @GeneratedValue(generator="generator")
    
    @Column(name="fid", unique=true, nullable=false)

    public String getFid() {
        return this.fid;
    }
    
    public void setFid(String fid) {
        this.fid = fid;
    }
    
    @Column(name="ffee", precision=15, scale=5)

    public double getFfee() {
        return this.ffee;
    }
    
    public void setFfee(double ffee) {
        this.ffee = ffee;
    }
    
    @Column(name="flevel")

    public int getFlevel() {
        return this.flevel;
    }
    
    public void setFlevel(int flevel) {
        this.flevel = flevel;
    }
    @Version
    @Column(name="version")
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}









}