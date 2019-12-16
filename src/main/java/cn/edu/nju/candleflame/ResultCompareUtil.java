package cn.edu.nju.candleflame;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ResultCompareUtil {
	public static void main(String[] args) throws IOException {
		File file1 = new File("/Users/liweimin/Documents/学习资料/高级算法/作业/附件1/tgs_2000");
		FileReader fr = new FileReader(file1);
		BufferedReader bf = new BufferedReader(fr);
		Set<String> fileLine1 = new HashSet<>();
		String str;
		// 按行读取字符串
		while ((str = bf.readLine()) != null) {
			fileLine1.add(str.trim());
		}
		File file2 = new File("/Users/liweimin/Documents/学习资料/高级算法/作业/附件1/lwm_540");
		FileReader fr2 = new FileReader(file2);
		BufferedReader bf2 = new BufferedReader(fr2);
		Set<String> fileLine2 = new HashSet<>();
		String str2;
		// 按行读取字符串
		while ((str2 = bf2.readLine()) != null) {
			fileLine2.add(str2.trim());
		}

		System.out.println(fileLine1.size());
		System.out.println(fileLine2.size());

//		int count = 0;
//		for (String line :fileLine1){
//			if (line.split("4").length>2){
//				System.out.println(line);
//				count++;
//			}
//		}
//		System.out.println(count);
		for (String line:fileLine1){
			if (!fileLine2.contains(line)){
				System.out.println(line);
			}
		}
	}
}
