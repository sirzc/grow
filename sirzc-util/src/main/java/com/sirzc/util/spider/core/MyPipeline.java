package com.sirzc.util.spider.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**   
 * @Title: MyPipeline.java
 * @Package com.sirzc.work.my
 * @Description: 爬取结果处理
 * @author 作者：Administrator
 * @date 创建时间：2018年7月12日 下午4:41:34
 * @version V1.0 
 * @since JDK 1.8    
 */
public class MyPipeline implements Pipeline {
	
	/**
	 * 存放爬取的结果集
	 */
	private Map<String, Object>  fields = new HashMap<String,Object>();
	
	/**
	 * 
	 * ResultItems保存了抽取结果，它是一个Map结构，<br/>
	 * 在page.putField(key,value)中保存的数据，可以通过ResultItems.get(key)获取
	 */
	public void process(ResultItems resultItems, Task task) {
		fields = resultItems.getAll();
		boolean isLink = (Boolean) fields.get("isLink");
		if(isLink){
			//处理链接列表
			this.useLink(fields);
		}else{
			//处理内容
			this.usePage(fields);
		}
	}

	/**
	 * 
	 * 操作得到的链接. <br/>
	 *
	 * @param data
	 * @author 作者：Administrator
	 */
	@SuppressWarnings("unchecked")
	private void useLink(Map<String, Object> data) {
		List<String> titles = (List<String>) data.get("titles");
		List<String> links =(List<String>) data.get("links");
		for (int i = 0; i < links.size(); i++){
			System.out.println("标题："+titles.get(i)+";"+"链接："+links.get(i));
		}
//		if(null == titles || null == links){
//			return;
//		}		
//		List<BlogLink> linklist = new ArrayList<BlogLink>();		
//		for(int i=0; i<titles.size(); ++i){
//			BlogLink blogLink = new BlogLink();
//			blogLink.setTitle(titles.get(i));
//			blogLink.setLink(links.get(i));
//			linklist.add(blogLink);
//		}
//		LinksList.addLinks("test", linklist);
		
	}

	/**
	 * 
	 * 操作得到的内容. <br/>
	 *
	 * @param data
	 * @author 作者：Administrator
	 */
	private void usePage(Map<String, Object> data) {
		System.out.println("title:"+data.get("title"));
		System.out.println("content:"+data.get("content"));
		System.out.println("tags:"+data.get("tags"));
	}
}
