package com.sirzc.util.spider.tool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sirzc.util.spider.bean.Blog;

/**
 * 
 * @Title: BlogList.java
 * @Package com.sirzc.util.spider.tool
 * @Description: 爬虫获取的博客列表
 * @author 作者：Administrator
 * @date 创建时间：2018年7月13日 下午6:31:32
 * @version V1.0 
 * @since JDK 1.8
 */
public class BlogList {

	//用户名，对应一个用户列表，如果用户为新用户则put新的列表
	private static Map<String, Blog> blogMap = new ConcurrentHashMap <String,Blog>();
	
	public static void addBlog(Blog blog) {
		
		if(blogMap.containsKey(blog.getLink())){
			//已存在博客，有异常，没处理
			blogMap.put(blog.getLink(), blog);
		} else{
			blogMap.put(blog.getLink(), blog);
		}
	}
	
	public static Blog getBlog(String link) {
		if(blogMap.containsKey(link)){
			return blogMap.remove(link);
		}
		return null;
	}
	
}
