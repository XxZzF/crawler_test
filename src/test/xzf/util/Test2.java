package test.xzf.util;

import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Test2 {
	public static void main(String[] args) {
		CloseableHttpClient httpclient = HttpClients.createDefault();  
		HttpClientContext context = HttpClientContext.create();  
		HttpGet httpget = new HttpGet("http://www.sina.com/");  
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpget, context);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		try {  
		    HttpHost target = context.getTargetHost();  
		    List<URI> redirectLocations = context.getRedirectLocations();  
		    URI location = null;
		    
		    System.out.println(httpget.getURI());
		    System.out.println(target);
		    System.out.println(redirectLocations);
		    
			try {
				location = URIUtils.resolve(httpget.getURI(), target, redirectLocations);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		    System.out.println("Final HTTP location: " + location.toASCIIString());  
		    // Expected to be an absolute URI  
		} finally {  
		    try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}  
	}

}
