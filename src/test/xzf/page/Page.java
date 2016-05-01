package test.xzf.page;


import org.apache.http.Header;

import test.xzf.parse.ParseData;

public class Page {
	private WebURL pageUrl;
	
	private byte[] contentData;
	
	private String contentEncoding;
	
	private long contentLength;
	
	private String contentType;
	
	private String contentCharset;
	
	protected int statusCode;
	
	private String language;
	
	protected Header[] pageHeaders;
	
	protected ParseData parseData;
	
	
	
	public Page(WebURL pageUrl) {
		this.pageUrl = pageUrl;
	}

	
	
	
	
	

	public WebURL getPageUrl() {
		return pageUrl;
	}


	public void setPageUrl(WebURL pageUrl) {
		this.pageUrl = pageUrl;
	}


	public byte[] getContentData() {
		return contentData;
	}


	public void setContentData(byte[] contentData) {
		this.contentData = contentData;
	}


	public String getContentEncoding() {
		return contentEncoding;
	}


	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}


	public long getContentLength() {
		return contentLength;
	}


	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}


	public String getContentType() {
		return contentType;
	}


	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public String getContentCharset() {
		return contentCharset;
	}


	public void setContentCharset(String contentCharset) {
		this.contentCharset = contentCharset;
	}


	public int getStatusCode() {
		return statusCode;
	}


	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public Header[] getPageHeaders() {
		return pageHeaders;
	}


	public void setPageHeaders(Header[] pageHeaders) {
		this.pageHeaders = pageHeaders;
	}


	public ParseData getParseData() {
		return parseData;
	}


	public void setParseData(ParseData parseData) {
		this.parseData = parseData;
	}
}
