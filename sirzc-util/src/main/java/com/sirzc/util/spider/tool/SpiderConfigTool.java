package com.sirzc.util.spider.tool;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.sirzc.util.core.ToolUtil;

/**
 * 
 * @Title: SpiderConfigTool.java
 * @Package com.sirzc.util.spider.common
 * @Description: 读取爬虫配置信息
 * @author 作者：Administrator
 * @date 创建时间：2018年7月13日 下午6:30:10
 * @version V1.0
 * @since JDK 1.8
 */
public class SpiderConfigTool {

	private String		configPath	= "Spider.xml";
	private File		file;
	private SAXReader	reader;
	private Document	doc;
	private Node		spiderNode;

	public SpiderConfigTool(String spiderName) throws DocumentException{
		file = new File(ToolUtil.getWebRootPath("src/main/resources/" + configPath));
		if (!file.exists()){
			return;
		}
		reader = new SAXReader();
		doc = reader.read(file);
		spiderNode = getSpider(spiderName);

	}

	public Node getSpiderNode() {
		return spiderNode;
	}

	private Node getSpider(String spiderName) {
		List<Node> list = doc.selectNodes("config/spider-cofig");

		for (Node i : list){
			if (i.selectSingleNode("name").getText().equals(spiderName)){
				return i;
			}
		}

		return null;
	}

	public Document getDoc() {
		return doc;
	}

	public static void main(String[] args) throws DocumentException {
		SpiderConfigTool spider = new SpiderConfigTool("jianshu.com");
		Node node = spider.getSpiderNode();
		System.out.println(node.asXML());
	}
}
