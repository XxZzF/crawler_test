package test.xzf.login;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class ZhiHuLogin {
	
	public static CloseableHttpClient httpClient;
	public static HttpClientContext context;
	
	public static HttpPost httpPost = null;
	public static HttpGet httpGet = null;
	
	public static CloseableHttpResponse response = null;
	public static HttpEntity httpEntity = null;
	
	public static String coding = "UTF-8";
	public static String email = "623996689@qq.com";
	public static String password = "ranbai526";
	public static String remember_me = "true";
	public static String _xsrf = null;
	public static String captcha = null;
	
	public ZhiHuLogin(CloseableHttpClient httpClient, HttpClientContext context) {
		ZhiHuLogin.httpClient = httpClient;
		ZhiHuLogin.context = context;
	}
	
	/**
	 * 从cookie中获得_xsrf的值
	 */
	public void get_Xsrf() {
		
		httpGet = new HttpGet("https://www.zhihu.com/signin");
		HttpClientUtil.config(httpGet);
		
		try {
			response = httpClient.execute(httpGet, context);
			System.out.println("已经抓取到get");
			CookieStore cookieStore = context.getCookieStore();
			List<Cookie> cookieList = cookieStore.getCookies();
			for (Cookie cookie : cookieList) {
				if (cookie.getName().equals("_xsrf")) {
					_xsrf = cookie.getValue();
					break;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取验证码
	 */
	public void getCaptcha() {
		String url = "https://www.zhihu.com/captcha.gif";
		String r = "";
		Random random = new Random();
		FileOutputStream out = null;
		InputStream in = null;
		
		for (int i = 0; i < 13; i++) {
			r += random.nextInt() % 10;
		}
		url += "?r=" + r + "&type=login";
		httpGet = new HttpGet(url);
		HttpClientUtil.config(httpGet);
		
		try {
			response = httpClient.execute(httpGet, context);
			httpEntity = response.getEntity();
			in = httpEntity.getContent();
			out = new FileOutputStream("F:\\captcha.gif");
			byte[] b = new byte[1024];
			int t = 0;
			while ((t = in.read(b)) != -1) {
				out.write(b);
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 手动输入验证码
	 */
	public void inputCaptcha() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			captcha = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 提交信息
	 */
	public void postInfo() {
		httpPost = new HttpPost("http://www.zhihu.com/login/email");
		HttpClientUtil.config(httpPost);
		InputStream in = null;
		
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("email", email));  
		formparams.add(new BasicNameValuePair("password", password));
		formparams.add(new BasicNameValuePair("remember_me", remember_me));  
		formparams.add(new BasicNameValuePair("_xsrf", _xsrf)); 
		formparams.add(new BasicNameValuePair("captcha", captcha));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);  
		httpPost.setEntity(entity);
		
		try {
			response = httpClient.execute(httpPost, context);
			CookieStore cookieStore = context.getCookieStore();
			List<Cookie> cookieList = cookieStore.getCookies();
			for (Cookie cookie : cookieList) {
				System.out.println(cookie.getName() + " " + cookie.getValue());
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void login() {
		get_Xsrf();
		System.out.println("获得验证码中");
		getCaptcha();
		System.out.println("请输入验证码");
		inputCaptcha();
		System.out.println("提交数据中");
		postInfo();
		System.out.println("登陆完成");
	}
}
