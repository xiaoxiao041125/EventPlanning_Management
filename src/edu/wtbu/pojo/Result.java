package edu.wtbu.pojo;

public class Result {
	Object data;
	Page page;
	String flag;
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	
	public Result() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Result(Object data, Page page, String flag) {
		super();
		this.data = data;
		this.page = page;
		this.flag = flag;
	}
	
}
