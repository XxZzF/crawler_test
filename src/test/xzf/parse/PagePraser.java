package test.xzf.parse;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import test.xzf.page.Page;
import test.xzf.page.WebURL;

public class PagePraser {
	public static void parsePage(Page page) {
		makeFile(page);
		ParseData parseData = new ParseData();
		page.setParseData(parseData);
		
		
		String pageContent = null;
		List<String> urls = new LinkedList<>();
		try {
			pageContent = new String(page.getContentData(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		Document doc = Jsoup.parse(pageContent, page.getPageUrl().getUrl().toString());
		Elements links = doc.select("a[href]");
		for (Element link : links) {
			urls.add(link.attr("href").trim());		//解析url的时候去掉url行尾的空格 避免illegal character异常
		}
		
		parseData.setPageContent(pageContent);
		parseData.setUrls(urls);
	}
	
	public static void makeFile(Page page) {
		FileOutputStream file = null;
		try {
			String fileName = page.getPageUrl().getUrl().toString();
			fileName = fileName.substring(0, fileName.length() - 1);
			fileName = fileName.replaceAll("\\W?", "");
			file = new FileOutputStream("F:\\zhihu\\" + fileName + ".html");
			try {
				file.write(page.getContentData());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (file != null)
					file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
