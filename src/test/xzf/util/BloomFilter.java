package test.xzf.util;

import java.util.BitSet;
import java.util.Random;

/**
 * 布隆过滤器 使用murmurHash
 * @author xzf
 *
 */
public class BloomFilter {
	private static final int SEED_NUM = 32;
	private static int[] seeds = new int[]{3, 5, 7, 11, 
										   13, 17, 19, 23, 
										   29, 31, 37, 39, 
										   41, 43, 47, 49,
										   51, 53, 57, 59,
										   61, 63, 67, 69,
										   71, 73, 79, 83,
										   87, 89, 91, 93};
	
	private int N;		//数据总数
	private double P;	//错误率
	private int K;		//hash函数个数
	private int size = 0;	//过滤器的位数
	private BitSet bitSet = null;
	
	private boolean available = false;
	
	public BloomFilter(int N, double P) {
		this.N = N;
		this.P = P;
		init();
	}
	
	public void init() {
		long tempSize = (long)(-N * Math.log(P) / (Math.log(2.0) * Math.log(2.0)));
		
		if (tempSize > (long)Integer.MAX_VALUE) {
			this.available = false;
			System.out.println("过滤器初始化失败 大小超限 请调整数据总数和错误率");
			return ;
		}
		this.size = (int)tempSize;
		
		this.K = (int)(size * Math.log(2.0) / N);
		if (K > SEED_NUM) {
			this.available = false;
			System.out.println("过滤器初始化失败  错误率太小");
			return ;
		}
		
		this.bitSet = new BitSet(size);
		this.available = true;
		System.out.println("过滤器初始化成功");
//		System.out.println(N);
//		System.out.println(P);
//		System.out.println(K);
//		System.out.println(size);
//		System.out.println("--------------------");
	}
	
	public synchronized void add(String value) {
		int len = value.length();
		for (int i = 0, hash; i < K; i++) {
			hash = MurmurHash3.murmurhash3_x86_32(value, 0, len, BloomFilter.seeds[i]);
			hash = hash >= 0 ? hash : -hash;
			hash = hash % size;
			bitSet.set(hash, true);
		}
	}
	
	public synchronized boolean isExit(String value){  
		int len = value.length();
		for (int i = 0, hash; i < K; i++) {
			hash = MurmurHash3.murmurhash3_x86_32(value, 0, len, BloomFilter.seeds[i]);
			hash = hash >= 0 ? hash : -hash;
			hash = hash % size;
			if (!bitSet.get(hash))
				return false;
		}
		return true;
    } 

	public boolean isAvailable() {
		return available;
	}
	
//	public static void main(String[] args) {
//		BloomFilter filter = new BloomFilter(10000000, 0.00001);
//
//		
//		long start = System.currentTimeMillis();
//		String str;
//		Random random = new Random();
//		for (int i = 0; i < 1000000; i++) {
//			str = "" + random.nextLong();
//			filter.add(str);
//		}
//		System.out.println(System.currentTimeMillis() - start);
//	}

}
