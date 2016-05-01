package test.xzf.parse;

import java.util.List;

import test.xzf.page.WebURL;

public class ParseData {
	private String pageContent;
	
	private List<String> urls;
	
	public ParseData() {
		
	}

	public String getPageContent() {
		return pageContent;
	}

	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
}
