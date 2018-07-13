package com.sirzc.util.spider.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sirzc.util.spider.bean.BlogLink;

/**
 * 
 * @Title: LinksList.java
 * @Package com.sirzc.util.spider.tool
 * @Description: 爬虫获取的博客列表
 * @author 作者：Administrator
 * @date 创建时间：2018年7月13日 下午6:31:39
 * @version V1.0 
 * @since JDK 1.8
 */
public class LinksList {

	//用户名，对应一个用户列表，如果用户为新用户则put新的列表
	private static Map<String, ConcurrentHashMap<String, BlogLink>> linkMap = new ConcurrentHashMap <String,ConcurrentHashMap<String, BlogLink>>();
	
	public static void clearList(String user){
		linkMap.remove(user);
	}
	
	public static void addLinks(String user, List<BlogLink> links) {
		
		ConcurrentHashMap<String,BlogLink> linkList;
		
		if(linkMap.containsKey(user)){
			linkList= linkMap.get(user);
		} else{
			linkList = new ConcurrentHashMap<String,BlogLink>();
		}
		
		//put links 去重复
		for(int i=0; i<links.size(); ++i){
			String key = links.get(i).getLink();
			
			if(linkList.containsKey(key)){	//重复，不提交
				continue;
			}
			
			linkList.put(key, links.get(i));
		}
		
		linkMap.put(user, linkList);
	}
	
	public static List<BlogLink> getLinkList(String user) {
		ConcurrentHashMap<String, BlogLink> hash;
		if(linkMap.containsKey(user)){
			hash = linkMap.remove(user);
			return new ArrayList<BlogLink>(hash.values());	//hash to list
		}
		
		return null;
	}
	
}