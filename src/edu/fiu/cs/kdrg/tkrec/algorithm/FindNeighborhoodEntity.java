package edu.fiu.cs.kdrg.tkrec.algorithm;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.ahocorasick.trie.Trie;
import org.dom4j.DocumentException;

public class FindNeighborhoodEntity {

	public static void main(String[] args) throws IOException, XMLStreamException, DocumentException {
		String test = "post loading failed due to plc issue." + " updated the gft after proper validation and "
				+ "processed the job and completed successfully.";
		String test1 = "ProblemSolutionText:The server has "
				+ "2 connections established with HSM. RCADescription:The server has 2 connections established with HSM.";
		
		Map<String, Integer> keywordFrequent = new HashMap<String, Integer>();
		List<String> keywordDict = RepeatedPatternExtraction.buildDict("data2/result_pattern_extraction_10");
		Trie trie = new ACPhraseExtraction(keywordDict).buildTrie();
		Set<String> keywords = new HashSet<String>();
		keywords = ACPhraseExtraction.containKeywords(trie, test);
		Set<String> keywords2 = ACPhraseExtraction.containKeywords(trie, test1);
		System.out.println(keywords);
		System.out.println(keywords2);
	}
}
