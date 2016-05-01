package test.xzf.monitor;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;

public class IdleConnectionMonitor implements Runnable {
	private final HttpClientConnectionManager connMgr;  
    private boolean shutdown = false;
	
    
	
    public IdleConnectionMonitor(HttpClientConnectionManager connMgr) {  
    	this.connMgr = connMgr;
    }  
  
    @Override  
    public void run() {  
        try {  
            while (!shutdown) {
            	Thread.sleep(5000);
                // Close expired connections  
                connMgr.closeExpiredConnections();  
                // Optionally, close connections  
                // that have been idle longer than 30 sec  
                connMgr.closeIdleConnections(30, TimeUnit.SECONDS);  
            }  
        } catch (InterruptedException ex) {
        	ex.printStackTrace();
        }  
    }  
    
    public void shutdown() {  
        shutdown = true;  
    }
}
