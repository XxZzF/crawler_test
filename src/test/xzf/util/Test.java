package test.xzf.util;

import java.util.ArrayList;
import java.util.List;

import test.xzf.controller.CrawlController;
import test.xzf.crawler.CrawlConfig;
import test.xzf.page.WebURL;

public class Test {

	public static void main(String[] args) {
		CrawlConfig config = new CrawlConfig();
		
		List<WebURL> seeds = new ArrayList<>();
		seeds.add(new WebURL("https://www.zhihu.com/", null));
		
		CrawlController controller = new CrawlController("xzf", config);
		controller.setSeeds(seeds);
		controller.doLogin();
		controller.start();
	}
}
