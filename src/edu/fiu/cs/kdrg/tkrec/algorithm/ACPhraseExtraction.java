package edu.fiu.cs.kdrg.tkrec.algorithm;

import java.util.*;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;

 /**
  * Aho–Corasick algorithm
  * @author qing wang
  */

public class ACPhraseExtraction {

	public static List<String> keywords = null;

	public ACPhraseExtraction(List<String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * Build Trie
	 * 
	 * @param keywords
	 * @return Trie
	 */
	public static Trie buildTrie() {
		TrieBuilder trieBuilder = Trie.builder().removeOverlaps();
//		TrieBuilder trieBuilder = Trie.builder();
		for (String keyword : keywords) {
			trieBuilder.addKeyword(keyword);
		}
		Trie trie = trieBuilder.build();
		return trie;
	}

	/**
	 * 
	 * @param contains keyword phrase
	 * @param sentence
	 * @return
	 */
	public static Set<String> containKeywords(Trie trie, String sentence) {
		Set<String> keywords = new HashSet<String>();
		Collection<Emit> emits = trie.parseText(sentence);

		Iterator iter = emits.iterator();
		while (iter.hasNext()) {
			Emit emit = (Emit) iter.next();
			keywords.add(emit.getKeyword());
		}
		return keywords;
	}

	/**
	 * 
	 * Sentence: "Los Angeles Lakers visited Washington State last week"
	 * Dictionary: {Los Angeles, Lakers, Los Angeles Lakers, Washington, State, Washington State University}
	 * 
	 * 
	 * test
	 * @param args
	 */
//	public static void main(String[] args) {
//		List<String> keywords = new ArrayList<String>();
//		keywords.add("database");
//		keywords.add("deadlock");
//		keywords.add("database deadlock");
//		keywords.add("job");
//		keywords.add("failed");
//
//		Trie trie = new ACPhraseExtraction(keywords).buildTrie();
//		String sentence = "job failed due to the database deadlock. database rerun";
//		Set<String> result = containKeywords(trie, sentence);
//		System.out.println(result);
//
//	}
}
