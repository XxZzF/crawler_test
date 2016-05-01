package test.xzf.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

/**
 * 单例模式创建HttpClient(懒汉式)
 * @author xzf
 *
 */
public class HttpClientUtil {
	
	private static CloseableHttpClient httpClient = null;
	private static HttpClientContext context = null;
	public static PoolingHttpClientConnectionManager cm = null;
	private static final int maxTotal = 100;
	private static final int defaultMaxPerRoute = 20;
	private static final int maxPerRoute = 50;
	private static final String hostName = "https://www.zhihu.com/";
	private static final int port = 80;
	private static final int timeOut = 3 * 1000;
	
	//线程安全
	public static CloseableHttpClient getHttpClient() {
		if (httpClient == null) {
			synchronized (HttpClient.class) {
				if(httpClient == null) {
					createHttpClient();
				}
			}
		}
		return httpClient;
	}
	
	private static void createHttpClient() {
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", plainsf)
					.register("https", sslsf)
					.build();
		
		//连接池创建
		cm = new PoolingHttpClientConnectionManager(registry);
		
		//设置连接池最大连接数
		cm.setMaxTotal(maxTotal);
		//设置每个路由的默认最大连接数
		cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
		//设置连接到具体一个主机的最大连接数
		cm.setMaxPerRoute(new HttpRoute(new HttpHost(hostName, port)), maxPerRoute);
		
		//请求重试处理
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
		};
		
		//设置cookie规范
		RequestConfig globalConfig = RequestConfig.custom()  
												  .setCookieSpec(CookieSpecs.STANDARD)
												  .setConnectionRequestTimeout(timeOut)
									              .setConnectTimeout(timeOut)
									              .setSocketTimeout(timeOut)
		        								  .build(); 
		
		httpClient = HttpClients.custom()
								.setConnectionManager(cm)
								.setRetryHandler(httpRequestRetryHandler)
								.setDefaultRequestConfig(globalConfig)
								.build();
	}

	public static HttpClientContext getContext() {
		if (context == null) {
			synchronized (HttpClientUtil.class) {
				if (context == null)
					context = HttpClientContext.create();
			}
		}
		return context;
	}
	
	public static void config(HttpRequestBase httpRequestBase) {
		httpRequestBase.setHeader("host", "www.zhihu.com");
		httpRequestBase.setHeader("method", "GET");
		httpRequestBase.setHeader("scheme", "https");
		httpRequestBase.setHeader("version", "HTTP/1.1");
		httpRequestBase.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpRequestBase.setHeader("accept-encoding", "gzip, deflate, sdch");
		httpRequestBase.setHeader("accept-language", "zh-CN,zh;q=0.8");
		httpRequestBase.setHeader("upgrade-insecure-requests", "1");
		httpRequestBase.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 "
				+ "(KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
	}
	
	public static String get(String url) {
		if (url == null) 
			return null;
		
		getHttpClient();
		
		String result = "";
		String str = null;
		BufferedReader bf = null;
		CloseableHttpResponse httpResponse = null;
		HttpGet httpGet = new HttpGet(url);
		config(httpGet);
		
		try {
			httpResponse = httpClient.execute(httpGet, new HttpClientContext(getContext()));	//这个地方new了一个新的context对象 context对象不是线程安全的
			bf = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
			while ((str = bf.readLine()) != null) {
				result += str + "\n";
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bf.close();
				httpResponse.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
