package edu.fiu.cs.kdrg.tkrec.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.fiu.cs.kdrg.tkrec.nlp.StanfordNLPAnnotator;
import edu.fiu.cs.kdrg.tkrec.nlp.Tokenizer;
import edu.stanford.nlp.ling.Word;

/**
 * Build a domain dictionary by using Word-Level LZW compression algorithm
 * @author qingwang
 */
public class WordLZW {
    /** Compress a string to a list of output symbols. */
    public static Map<String,Integer> compress(List<Word> words) {
        // Build the dictionary.
        int dictSize = 0;
        Map<String,Integer> dictionary = new HashMap<String,Integer>();

        String w = "";
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i< words.size()-1;i++) {
        	Word c = words.get(i);
            String curStr = c.toString().toLowerCase();
        	String wc = w + " " + curStr;
            if (dictionary.containsKey(wc))
                w = wc;
            else {
                result.add(dictionary.get(w));
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);
                w = "" + curStr;
            }
        }
 
        // Output the code for w.
        if (!w.equals(""))
            result.add(dictionary.get(w));
        return dictionary;
    }
 
    /**
     * Test
     * @param args
     */
//    public static void main(String[] args) {
//		StanfordNLPAnnotator annotator = new StanfordNLPAnnotator().getInstance();
//    	List<String> sentences = annotator.extractSentences("certificates will be renewed when received from danske certificates will not.");
//		List<Word> words = Tokenizer.getTokens(sentences.get(0));
//        
//		Map<String, Integer> compressedDic = compress(words);
//        System.out.println(compressedDic);
//    }
}