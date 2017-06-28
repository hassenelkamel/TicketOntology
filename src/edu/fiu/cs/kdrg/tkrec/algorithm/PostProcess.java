package edu.fiu.cs.kdrg.tkrec.algorithm;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.fiu.cs.kdrg.tkrec.util.IOUtil;

/**
 * Post process the tagged ticket
 * @author qingwang
 *
 */
public class PostProcess {
	
	public static final String PROBLEMCONDITION_ENTITY_INNER = "\\(\\(\\w+\\)/\\(ProblemCondition\\)(\\s?\\w+\\s?)\\)\\/\\(Entity\\)";
	public static final String PROBLEMCONDITION_FAILED_JOB = "(failed job)/(Entity)";
	public static final String CLASS_TAGGER_FILENAME = "data2/result_statistics_classtagger";
	public static final String OUTPUT_FILE = "data2/result_postprocess_classtagger";
	
	/**
	 * Load class tagger file for postprocessing
	 * @param filename
	 * @return
	 */
	public static List<String> loadClassTaggerFile(String filename) {
		String content = IOUtil.readString(filename, IOUtil.defaultCharset);
		String[] sentences = content.split("\n");
		return Arrays.asList(sentences);
	}
	/**
	 * Remove problemCondition entity inner
	 * @param taggedSource
	 * @return
	 */
	public static String removeProblemEntityInner(String taggedSource, String replaceStr) {
		Pattern r = Pattern.compile(PROBLEMCONDITION_ENTITY_INNER);
		Matcher m = r.matcher(taggedSource);
		
		if(m.find()) {
			String res = m.group(0);
			taggedSource = taggedSource.replace(res, replaceStr);
		}
		return taggedSource.trim();
	}
	
	public static void experiment(String inputfile, String outputfile) {
		List<String> sentencesList = loadClassTaggerFile(inputfile);
		System.out.println(sentencesList.size());
		StringBuffer sb = new StringBuffer();
		for(String sentence: sentencesList) {
			String res = removeProblemEntityInner(sentence, PROBLEMCONDITION_FAILED_JOB);
			sb.append(res + "\n");
		}

		System.out.println("start writing to file...");
		IOUtil.writeString(sb.toString(), outputfile, IOUtil.defaultCharset);
		System.out.println("finish writing to file...");
		
	}
}
