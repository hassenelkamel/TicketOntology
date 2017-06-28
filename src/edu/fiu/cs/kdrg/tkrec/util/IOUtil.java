package edu.fiu.cs.kdrg.tkrec.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class IOUtil {

	public static String defaultCharset = "UTF-8";

	/*
	 * write string to a file
	 */
	public static boolean writeString(String content, String destination, String charSet) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(destination)), charSet));
			bw.write(content);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * read string from a file
	 */
	public static String readString(String source, String charSet) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(source)));
			String str = "";
			while (null != (str = br.readLine())) {
				sb.append(str + "\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String readString(String source) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(source)));
			String str = "";
			while (null != (str = br.readLine())) {
				sb.append(str + "\n");
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void writeMap2File(Map<?, ?> map, String outputfilename) {

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(outputfilename)));
			String key = "";
			int value;
			for (Entry<?, ?> entry : map.entrySet()) {
				key = entry.getKey().toString();
				value = Integer.valueOf(entry.getValue().toString());
				bw.write(value + "\t" + key + "\n");
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeTreeMap2File(TreeMap<String, Integer> map, String outputfilename) {
		BufferedWriter bw = null;
		
		System.out.println(DateUtil.getDate() + " ========= Start Writing to file: " + outputfilename + " ========");
		try {
			bw = new BufferedWriter(new FileWriter(new File(outputfilename)));
			String key = "";
			int value;
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
				bw.write(value + "\t" + key + "\n");
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(DateUtil.getDate() + " ========= Finish Writing to file: " + outputfilename + " ========");
	}
}
