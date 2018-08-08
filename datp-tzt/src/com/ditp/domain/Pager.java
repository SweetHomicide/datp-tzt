package com.ditp.domain;

import java.io.Serializable;

public class Pager implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3843296486817365836L;
	private int pageSize=10;
	private int pageIndex=1;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageIndex() {
		return pageIndex-1;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
}
