package test.xzf.frontier;

import test.xzf.util.BloomFilter;

public class Visited {
	private static BloomFilter bloomFilter = new BloomFilter(10000000, 0.0001);
	
	public void add(String url) {
		bloomFilter.add(url);
	}
	
	public boolean isExit(String url) {
		return bloomFilter.isExit(url);
	}
}
