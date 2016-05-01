package test.xzf.fetcher;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import test.xzf.crawler.CrawlConfig;
import test.xzf.page.Page;

public class PageFetcher {
	
	private CloseableHttpClient httpClient;
	private HttpClientContext httpContext;
	private PoolingHttpClientConnectionManager cm;
	
	public PageFetcher(CrawlConfig config) {
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
								.<ConnectionSocketFactory> create()
								.register("http", plainsf)
								.register("https", sslsf)
								.build();
		
		cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		cm.setMaxTotal(config.getMaxTotal());
		cm.setDefaultMaxPerRoute(config.getDefaultMaxPerRoute());
		
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
												  .setConnectionRequestTimeout(config.getConnectionRequestTimeout())
									              .setConnectTimeout(config.getConnectTimeout())
									              .setSocketTimeout(config.getSocketTimeout())
		        								  .build(); 
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setConnectionManager(cm)
						 .setRetryHandler(httpRequestRetryHandler)
						 .setDefaultRequestConfig(globalConfig);
		httpClient = httpClientBuilder.build();
	}
	
	/**
	 * 下载page 并把结果封装到page对象中
	 * @param page
	 */
	public void fetchPage(Page page) {
		HttpGet httpGet = null;
		CloseableHttpResponse httpResponse = null;
		
		try {
			httpGet = new HttpGet(page.getPageUrl().getUrl().toString());
			httpResponse = httpClient.execute(httpGet);
			processResponse(page, httpResponse);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpResponse != null)
				httpResponse.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void fetchPage(Page page, HttpClientContext httpContext) {
		HttpGet httpGet = null;
		CloseableHttpResponse httpResponse = null;
		
		try {
			httpGet = new HttpGet(page.getPageUrl().getUrl().toString());
			httpResponse = httpClient.execute(httpGet, httpContext);
			processResponse(page, httpResponse);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpResponse != null)
				httpResponse.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void processResponse(Page page, CloseableHttpResponse httpResponse) {
		HttpEntity httpEntity = httpResponse.getEntity();
		
		try {
			page.setContentData(EntityUtils.toByteArray(httpEntity));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (httpEntity == null || 
			httpEntity.getContentEncoding() == null || 
			httpEntity.getContentLength() == 0 ||
			httpResponse.getStatusLine() == null) {
			return ;
		}
		page.setContentEncoding(httpEntity.getContentEncoding().getValue());
		page.setContentLength(httpEntity.getContentLength());
		page.setContentType((httpEntity.getContentType().getValue()));
		page.setContentLength(httpEntity.getContentLength());
		page.setStatusCode(httpResponse.getStatusLine().getStatusCode()); 
		page.setPageHeaders(httpResponse.getAllHeaders());
	}

	
	
	
	
	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	
}
