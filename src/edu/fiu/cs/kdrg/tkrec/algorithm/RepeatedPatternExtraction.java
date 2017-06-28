package edu.fiu.cs.kdrg.tkrec.algorithm;

import java.io.IOException;
import java.util.*;

import javax.xml.stream.XMLStreamException;

import org.ahocorasick.trie.Trie;
import org.apache.commons.collections4.ListUtils;
import org.dom4j.DocumentException;

import edu.fiu.cs.kdrg.tkrec.core.Event;
import edu.fiu.cs.kdrg.tkrec.nlp.StanfordNLPAnnotator;
import edu.fiu.cs.kdrg.tkrec.nlp.Tokenizer;
import edu.fiu.cs.kdrg.tkrec.util.IOUtil;
import edu.fiu.cs.kdrg.tkrec.util.RegPatterns;
import edu.fiu.cs.kdrg.tkrec.util.XMLEventLoader;
import edu.stanford.nlp.ling.Word;

/**
 * Build a domain-specific dictionary: use WLZW algorithm to find the all n-gram phrases and the whole tickets as input.
 * Constructing a finite state automaton: build a trie based on the domain-specific dictionary and construct the finite state automaton based on the trie.
 * Find all sub-phrases moving through automaton: reading each word from the whole tickets as input, storing the finded phrases and its frequent into one dictionary.
 * @author qingwang
 */
public class RepeatedPatternExtraction {

	public static List<String> eSol = null;
	public static StanfordNLPAnnotator annotator = new StanfordNLPAnnotator().getInstance();
	
	/**
	 * tokenizer
	 * @param rawString
	 * @param regex
	 * @return
	 */
	public static String tokenizedString(String rawString, String regex) {
		if (null == rawString)
			return null;
		return rawString.replaceAll(regex, " ");
	}

	/**
	 * Load resolutions
	 * @param eventFileName
	 * @return
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws DocumentException
	 */
	public static List<String> loadResolutions(String eventFileName)
			throws IOException, XMLStreamException, DocumentException {

		List<Event> events = XMLEventLoader.loadEvents(eventFileName, -1);
		List<String> eSol = new ArrayList<String>(events.size());
		for (Event e : events) {
			StringBuffer sb = new StringBuffer();
			System.out.println(e.getValue("RESOLUTION"));
			if (e.getValue("RESOLUTION") == null) continue;
			String event = tokenizedString(e.getValue("RESOLUTION"), RegPatterns.TOKENIZE_SENTENCE_REG);
			List<String> sentences = annotator.extractSentences(event.trim().toLowerCase());
			for(String sentence: sentences) sb.append(sentence + " ");
			eSol.add(sb.toString().trim());
		}
		events = null;
		return eSol;
	}

	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws DocumentException
	 */
	public static List<String> buildDict(String filename) throws IOException, XMLStreamException, DocumentException {
		eSol = loadResolutions(filename);
		List<Word> wordsList1 = new ArrayList<Word>();
		
		for (int i = 0; i < eSol.size(); i++) {
			List<String> sentences = annotator.extractSentences(eSol.get(i).trim().toLowerCase());
			for (int j = 0; j < sentences.size(); j++) {
				String sentence = sentences.get(j);
				if(0==i && 0==j) {
					wordsList1 = Tokenizer.getTokens(sentence);
				} else {
					List<Word> wordsList2 = Tokenizer.getTokens(sentence);
					wordsList1 = ListUtils.union(wordsList1, wordsList2);
				}
			}
		}

		Map<String, Integer> compressedDict = WordLZW.compress(wordsList1);
		return new ArrayList<String>(compressedDict.keySet());
	}

	public static void experiment(String inputFileName, String outputFileName) throws IOException, XMLStreamException, DocumentException {
		Map<String, Integer> keywordFrequent = new HashMap<String, Integer>();
		List<String> keywordDict = RepeatedPatternExtraction.buildDict(inputFileName);
		Trie trie  = new ACPhraseExtraction(keywordDict).buildTrie();
		Set<String> keywords = new HashSet<String>();
		for(String sol : eSol) {
			keywords = ACPhraseExtraction.containKeywords(trie, sol);
			for(String keyword: keywords) {
				if(keywordFrequent.containsKey(keyword)) {
					keywordFrequent.put(keyword, keywordFrequent.get(keyword) + 1);
				} else {
					keywordFrequent.put(keyword, 1);
				}
			}
		}
		
		System.out.println("RepeatedPatternExtraction.java - writing the domain keywords file...");
		Map<String, Integer> treeMap = new TreeMap<String, Integer>(keywordFrequent);
		StringBuffer sb = new StringBuffer();
		for(String keyword: treeMap.keySet()) {
//			sb.append(keyword + " " + treeMap.get(keyword) + "\n");
			if(treeMap.get(keyword) >= 10) {
				if(keyword.startsWith(".")) {
					sb.append(keyword.substring(1, keyword.length()).trim() + ":" + treeMap.get(keyword) + "\n");
				} else {
					sb.append(keyword.trim() + ":" + treeMap.get(keyword) + "\n");
				}
			}
			
		}
		IOUtil.writeString(sb.toString(), outputFileName, IOUtil.defaultCharset);
		System.out.println("RepeatedPatternExtraction.java - writing finished...");
	}

}
