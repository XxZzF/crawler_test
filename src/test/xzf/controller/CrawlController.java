package test.xzf.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;

import test.xzf.crawler.CrawlConfig;
import test.xzf.crawler.WebCrawler;
import test.xzf.fetcher.PageFetcher;
import test.xzf.frontier.Frontier;
import test.xzf.frontier.Visited;
import test.xzf.login.ZhiHuLogin;
import test.xzf.monitor.IdleConnectionMonitor;
import test.xzf.page.WebURL;

public class CrawlController {
	
	private String controllerName;
	
	private CrawlConfig crawlConfig;
	
	private PageFetcher pageFetcher;
	
	private Frontier frontier = new Frontier();
	
	private Visited visited = new Visited();
	
	private IdleConnectionMonitor connMonitor;
	
	private HttpClientContext httpContext;
	
	private int numberOfCrawlers;
	
	private List<Thread> crawlers = new ArrayList<Thread>();
	
	
	public CrawlController(String controllerName, CrawlConfig crawlConfig) {
		this.controllerName = controllerName;
		this.crawlConfig = crawlConfig;
		this.pageFetcher = new PageFetcher(crawlConfig);
		this.connMonitor = new IdleConnectionMonitor(this.pageFetcher.getCm());
		this.numberOfCrawlers = crawlConfig.getNumberOfCrawlers();
	}
	
	public void setSeeds(List<WebURL> seeds) {
		frontier.setSeeds(seeds);
	}
	
	public void doLogin() {
		httpContext = HttpClientContext.create();
		login(pageFetcher.getHttpClient(), httpContext);
	}
	
	public void login(CloseableHttpClient httpClient, HttpClientContext httpContext) {
		ZhiHuLogin zhiHuLogin = new ZhiHuLogin(httpClient, httpContext);
		zhiHuLogin.login();
	}
	
	public void start() {
		for (int i = 0; i < numberOfCrawlers; i++) {
			Thread thread;
			if (httpContext == null)
				thread = new Thread(new WebCrawler(frontier, visited, pageFetcher), controllerName + " Crawler" + i);
			else
				thread = new Thread(new WebCrawler(frontier, visited, pageFetcher, httpContext), controllerName + " Crawler" + i);
			
			thread.start();
			crawlers.add(thread);
		}
		
		Thread connMonitorThread;
		connMonitorThread = new Thread(connMonitor, "connMonitorThread");
		connMonitorThread.start();
	}
}
 