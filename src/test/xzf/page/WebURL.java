package test.xzf.page;

import java.net.MalformedURLException;
import java.net.URL;

public class WebURL {
	
	private URL url;
	private int depth;
	
	private WebURL parentUrl;
	
	public WebURL(String url, WebURL parentUrl) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.parentUrl = parentUrl;
		
		if (parentUrl != null) {
			this.depth = parentUrl.getDepth() + 1;
		}
	}

	
	
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public WebURL getParentUrl() {
		return parentUrl;
	}

	public void setParentUrl(WebURL parentUrl) {
		this.parentUrl = parentUrl;
	}
}
