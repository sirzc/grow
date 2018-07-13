package com.sirzc.util.spider.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import com.sirzc.util.spider.tool.SpiderConfigTool;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @Title: MyPageProcessor.java
 * @Package com.sirzc.work
 * @Description: 爬取网站处理
 * @author 作者：Administrator
 * @date 创建时间：2018年7月12日 下午3:57:18
 * @version V1.0
 * @since JDK 1.8
 */
public class MyPageProcessor implements PageProcessor {

	/**
	 * 存放链接的过滤信息
	 */
	public class LinkXpath {
		public String	linksXpath;		//链接列表过滤表达式
		public String	titlesXpath;	//title列表过滤表达式
	}

	/**
	 * 获取文件表达式
	 */
	public class ArticleXpath {
		public String	contentXpath;	//内容过滤表达式
		public String	titleXpath;		//title过滤表达式
		public String	tagsXpath;		//tags过滤表达式
	}

	private Site				site;			// 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private String				blogFlag;		//博客url的内容标志域
	private String				domain;			//当前域名
	private String				url;			//爬的地址
	private List<LinkXpath>		linkXpaths;		//获取链接表达式
	private List<ArticleXpath>	articleXpaths;	//获取文件表达式
	private List<String>		PagelinksRex;	//类别页列表过滤表达式
	private SpiderConfigTool	spiderConfig;	//读取配置信息

	public Site getSite() {
		return site;
	}

	public void process(Page page) {
		Pattern pattern = Pattern.compile(blogFlag);
		Matcher matcher = pattern.matcher(url);
		boolean result = matcher.find(); //匹配是否是内容页
		if (result){
			//执行内容的读取方法
			this.getParsePage(page);
			//保存抓取的结果(存放解析的链接),此处用来标记是否为链接
			page.putField("isLink", false);
		} else{
			//执行列表的读取方法
			this.getParseLink(page);
			//保存抓取的结果(存放解析的内容),此处用来标记是否为链接
			page.putField("isLink", true);
		}

	}

	/**
	 * 
	 * 解析抓取内容. <br/>
	 *
	 * @param page
	 * @author 作者：Administrator
	 */
	private void getParsePage(Page page) {
		String title = page.getHtml().xpath(articleXpaths.get(0).titleXpath).toString();
		String content = page.getHtml().xpath(articleXpaths.get(0).contentXpath).toString();
		String tags = page.getHtml().xpath(articleXpaths.get(0).tagsXpath).all().toString();

		for (int i = 1; i < articleXpaths.size() && StringUtils.isBlank(title); ++i){
			title = page.getHtml().xpath(articleXpaths.get(i).titleXpath).toString();
			content = page.getHtml().xpath(articleXpaths.get(i).contentXpath).toString();
			tags = page.getHtml().xpath(articleXpaths.get(i).tagsXpath).all().toString();
		}

		if (StringUtils.isBlank(content) || StringUtils.isBlank(title)){
			return;
		}

		if (!StringUtils.isBlank(tags)){
			tags = tags.substring(tags.indexOf("[") + 1, tags.indexOf("]"));
		}

		page.putField("content", content);
		page.putField("title", title);
		page.putField("tags", tags);

	}

	/**
	 * 
	 * 解析抓取列表. <br/>
	 *
	 * @param page
	 * @author 作者：Administrator
	 */
	private void getParseLink(Page page) {
		System.out.println(page.getHtml().toString());
		List<String> links = page.getHtml().xpath(linkXpaths.get(0).linksXpath).all();
		List<String> titles = page.getHtml().xpath(linkXpaths.get(0).titlesXpath).all();

		for (int i = 1; i < linkXpaths.size() && titles.size() == 0; ++i){
			links = page.getHtml().xpath(linkXpaths.get(i).linksXpath).all();
			titles = page.getHtml().xpath(linkXpaths.get(i).titlesXpath).all();
		}
		page.putField("titles", titles);
		page.putField("links", links);

		if (this.domain.equals("www.jianshu.com")){//关于简书博客爬取的处理方式
			String total = page.getHtml().xpath("//div[@class='info']/ul/li[3]/div[@class='meta-block']/a/p/text()")
					.toString();
			int totalArticle = Integer.valueOf(total);
			int mod = totalArticle % 9;
			int p = totalArticle / 9;
			int totalPage = (mod > 0) ? p + 1 : p;
			for (int i = 2; i < totalPage; i++){
				String url = PagelinksRex.get(0).replace("\\", "");
				//添加继续爬取的url
				page.addTargetRequest(url + i);
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Title: 默认构造函数
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @throws DocumentException
	 */
	public MyPageProcessor(String url) throws DocumentException{
		if (url.endsWith("/")){
			url = url.substring(0, url.length() - 1);
		}
		this.url = url;
		//切割域名 ：类似：csdn.net, 51cto.com, cnblogs.com, iteye.com
		String spiderName = "";
		Pattern p = Pattern.compile("\\.([a-zA-Z0-9]+\\.[a-zA-Z]+)");
		Matcher m = p.matcher(url);
		if (m.find()){
			spiderName = m.group(1);
		}
		this.spiderConfig = new SpiderConfigTool(spiderName);
		this.init();
	}

	/**
	 * 
	 * 初始化各项规则. <br/>
	 *
	 * @author 作者：Administrator
	 */
	private void init() {
		this.domain = spiderConfig.getSpiderNode().selectSingleNode("domain").getText();
		this.blogFlag = spiderConfig.getSpiderNode().selectSingleNode("blog-flag").getText();
		this.initSite();
		this.initLinkXpath();
		this.initArticleXpath();
		this.initPageRex();
	}

	/**
	 * 
	 * 抓取网站的相关配置，包括：编码、抓取间隔、重试次数等. <br/>
	 *
	 * @author 作者：Administrator
	 */
	private void initSite() {
		site = Site.me();
		site.setDomain(this.domain);
		site.setRetryTimes(3);
		site.setSleepTime(100);

	}

	/**
	 * 
	 * 初始化 获取链接列表xpath. <br/>
	 *
	 * @author 作者：Administrator
	 */
	private void initLinkXpath() {
		//获取链接表达式
		this.linkXpaths = new ArrayList<LinkXpath>();
		List<Node> list = spiderConfig.getSpiderNode().selectNodes("link-xpath");
		for (Node node : list){
			String link = node.selectSingleNode("links-xpath").getText();
			String title = node.selectSingleNode("titles-xpath").getText();
			LinkXpath linkXpath = new LinkXpath();
			linkXpath.linksXpath = link;
			linkXpath.titlesXpath = title;
			this.linkXpaths.add(linkXpath);
		}
	}

	/**
	 * 
	 * 初始化文件抓取规则xpath. <br/>
	 *
	 * @author 作者：Administrator
	 */
	private void initArticleXpath() {
		//获取文件表达式
		this.articleXpaths = new ArrayList<ArticleXpath>();
		List<Node> list = spiderConfig.getSpiderNode().selectNodes("article-xpath");
		for (Node node : list){
			String content = node.selectSingleNode("content-xpath").getText();
			String title = node.selectSingleNode("title-xpath").getText();
			String tags = node.selectSingleNode("tags-xpath").getText();
			ArticleXpath articleXpath = new ArticleXpath();
			articleXpath.contentXpath = content;
			articleXpath.titleXpath = title;
			articleXpath.tagsXpath = tags;
			articleXpaths.add(articleXpath);
		}
	}

	/**
	 * 
	 * 初始化 分页链接 xpath. <br/>
	 *
	 * @author 作者：Administrator
	 */
	private void initPageRex() {
		//获取分页设置
		this.PagelinksRex = new ArrayList<String>();
		List<Node> list = spiderConfig.getSpiderNode().selectNodes("page-links-rex");
		for (Node pagelink : list){
			String page = pagelink.getText();
			String string = url.replaceAll("\\.", "\\\\\\.");
			String temString = string + page;
			this.PagelinksRex.add(temString);
		}
	}

	public static void main(String[] args) {
		//		String url = "https://blog.csdn.net/zhang5476499/article/details/53288224";
		//		String url = "https://blog.csdn.net/zhang5476499";
		//		String url = "https://www.jianshu.com/u/adbc18310425";
		//		String url = "http://blog.sina.com.cn/guyuju";
		String url = "http://blog.sina.com.cn/s/blog_6a978c5a0100lrf6.html";
		try{
			Spider.create(new MyPageProcessor(url)).addUrl(url)
					.addPipeline(new MyPipeline())
					.thread(1).run();
		} catch (DocumentException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
