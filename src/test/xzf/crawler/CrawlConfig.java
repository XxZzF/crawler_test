package test.xzf.crawler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class CrawlConfig {

//	private static String[] properties = {
//			"maxTotal",				
//			"defaultMaxPerRoute"	
//			};
	
	/*-----------------------------------连接池配置-----------------------------------------*/
	private int maxTotal = 30;					//连接池最大连接数
	private int defaultMaxPerRoute = 10;		//每个路由最大连接数
	
	/*-----------------------------------连接池配置-----------------------------------------*/
	
	private int connectionRequestTimeout = 30000;//连接请求超时
	private int connectTimeout = 30000;			//连接超时
	private int socketTimeout = 30000;			//socket超时
	
	/*-----------------------------------爬虫配置-----------------------------------------*/
	private int numberOfCrawlers = 10;			//爬虫线程的数量

	
	
	public CrawlConfig() {}
	
	/**
	 * 利用反射机制初始化配置对象
	 * @param configFilePath
	 */
	public CrawlConfig(String configFilePath) {
		Properties properties = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(configFilePath);
			properties.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		Set<Entry<Object, Object>> entrySet = properties.entrySet();
		
		for (Entry entry : entrySet) {
			String fieldName = (String)entry.getKey();
			try {
				java.lang.reflect.Field field = CrawlConfig.class.getField(fieldName);
				field.setAccessible(true);
				
				if (field.getType() == String.class) {
					field.set(this, ((String)entry.getValue()).trim());
				}
				else if (field.getType() == int.class) {
					field.set(this, Integer.parseInt(((String)entry.getValue()).trim()));
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}  catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getDefaultMaxPerRoute() {
		return defaultMaxPerRoute;
	}

	public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
		this.defaultMaxPerRoute = defaultMaxPerRoute;
	}
	
	public int getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}

	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getNumberOfCrawlers() {
		return numberOfCrawlers;
	}

	public void setNumberOfCrawlers(int numberOfCrawlers) {
		this.numberOfCrawlers = numberOfCrawlers;
	}
	
}
