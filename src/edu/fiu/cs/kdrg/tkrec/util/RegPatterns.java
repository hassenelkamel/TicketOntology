package edu.fiu.cs.kdrg.tkrec.util;

/*
 * This class stores some common regular expression patterns for matching, such as url, file path.
 */
public class RegPatterns {

	public static String TOKENIZE_URL_REG = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	public static String TOKENIZE_SENTENCE_REG = "[:\\|\\*@]+|ProblemSolutionText|RCADescription" + "|" + TOKENIZE_URL_REG;
	
}