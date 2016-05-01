package test.xzf.frontier;

import java.util.LinkedList;
import java.util.List;

import test.xzf.page.WebURL;

public class Frontier {
	
	private static List<WebURL> workQueue = new LinkedList<>();
	
	private static final Object mutex = new Object();
	
	
	/**
	 * 从爬虫队列中取出10条url
	 * @return
	 */
	public List<WebURL> getNextUrls() {
		List<WebURL> nextUrls = new LinkedList<>();
		synchronized (mutex) {
			int listSize = workQueue.size();
			for (int i = 0; i < 10 && i < listSize; i++) {
				nextUrls.add(workQueue.get(0));
				workQueue.remove(0);
			}
		}
		return nextUrls;
	}
	
	public void add(WebURL url) {
		synchronized (mutex) {
			workQueue.add(url);
		}
	}
	
	/**
	 * 初始化爬虫队列的时候设置种子url
	 * @param url
	 */
	public void setSeeds(List<WebURL> seeds) {
		for (WebURL seed : seeds) {
			workQueue.add(seed);
		}
	}
}
