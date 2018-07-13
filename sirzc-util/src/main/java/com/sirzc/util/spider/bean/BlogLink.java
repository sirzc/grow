package com.sirzc.util.spider.bean;


/**
 * 
 * @Title: BlogLink.java
 * @Package com.sirzc.util.spider.bean
 * @Description: 博客链接 beans
 * @author 作者：Administrator
 * @date 创建时间：2018年7月13日 下午6:29:24
 * @version V1.0 
 * @since JDK 1.8
 */
public class BlogLink {
	
	//private String access_token="";	//	true	oauth2_token获取的access_token	
	
	private String 	title="";			//	true		博客标题	
	private String link="";				//	原博客链接
	private long id;					//	
	
	public BlogLink(Blog blog){
		title = blog.getTitle();
		link = blog.getLink();
	}
	
	public BlogLink(){}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "BlogLink [title=" + title + ", link=" + link + ", id=" + id + "]";
	}
	
	
}