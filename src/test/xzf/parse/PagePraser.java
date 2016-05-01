package test.xzf.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import test.xzf.page.Page;

public class PagePraser {
	private static Object mutex = new Object();
	
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
	
	public static void makeFile(final Page page) {
		String fileName = null;
		fileName = page.getPageUrl().getUrl().toString();
		fileName = fileName.replaceAll("\\W?", "");
		fileName = fileName + "_d=" + page.getPageUrl().getDepth();
		
		Path path = Paths.get("F:\\zhihu\\" + fileName + ".html");
		
		try {
			final AsynchronousFileChannel fc = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE,
					StandardOpenOption.CREATE);
			ByteBuffer buffer = ByteBuffer.wrap(page.getContentData());
			fc.write(buffer, 0, fileName, new CompletionHandler<Integer, String>() {
				@Override
				public void completed(Integer result, String attachment) {
					try {
						fc.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void failed(Throwable exc, String attachment) {
					System.out.println("fail!! ---" + attachment);
					exc.printStackTrace();
				}
			});
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		
		
		//把抓取到的网址写入文件
		synchronized (mutex) {
			File f = new File("F:\\website.txt");
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			FileWriter fw = null;
			try {
				fw = new FileWriter(f, true);
				fw.write(fileName + "\r\n");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
}
