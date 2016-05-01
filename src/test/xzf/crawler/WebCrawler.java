package test.xzf.crawler;

import java.util.List;

import org.apache.http.client.protocol.HttpClientContext;

import test.xzf.fetcher.PageFetcher;
import test.xzf.frontier.Frontier;
import test.xzf.frontier.Visited;
import test.xzf.page.Page;
import test.xzf.page.WebURL;
import test.xzf.parse.PagePraser;

public class WebCrawler implements Runnable {
	
	private Frontier frontier;
	
	private Visited visited;
	
	private PageFetcher pageFetcher;
	
	private HttpClientContext httpContext;
	
	public WebCrawler(Frontier frontier, Visited visited, PageFetcher pageFetcher) {
		this.frontier = frontier;
		this.visited = visited;
		this.pageFetcher = pageFetcher;
	}
	
	public WebCrawler(Frontier frontier, Visited visited, PageFetcher pageFetcher, HttpClientContext httpContext) {
		this(frontier, visited, pageFetcher);
		this.httpContext = HttpClientContext.adapt(httpContext);
	}

	@Override
	public void run() {
		while (true) {
			// 从爬虫队列中取得url
			List<WebURL> urls = frontier.getNextUrls();
			//如果没有从队列中获取到url
			if (urls.size() == 0) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			for (WebURL url : urls) {
				Page page = new Page(url);
				handleUrlBeforeProcess(url);//把url加入到visited表中
				processPage(page);			//获取页面
				if (page.getContentData() == null) 
					continue;
				parsePage(page);			//解析页面
				filterUrls(page);
			}
		}
	}

	private void handleUrlBeforeProcess(WebURL url) {
		visited.add(url.getUrl().toString());
	}
	
	private void filterUrls(Page page) {
		List<String> urls = page.getParseData().getUrls();
		
		for (int i = 0; i < urls.size(); i++) {		//筛选出格式不正确的url
			if (!urls.get(i).startsWith("https://") && !urls.get(i).startsWith("http://")) {
				urls.remove(i);
				i--;
			}
		}
		for (String url : urls) {				//把正确的url放入到爬虫队列中
			if (!visited.isExit(url))
				frontier.add(new WebURL(url, page.getPageUrl()));
		}
	}

	public void processPage(Page page) {
		if (httpContext == null)
			pageFetcher.fetchPage(page);
		else
			pageFetcher.fetchPage(page, httpContext);
	}
	
	public void parsePage(Page page) {
		PagePraser.parsePage(page);
	}
}
