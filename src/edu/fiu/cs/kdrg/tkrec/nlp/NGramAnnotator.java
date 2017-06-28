package edu.fiu.cs.kdrg.tkrec.nlp;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Count the frequency of NGram in a sentence. Please tokenize or lemalize the
 * sentence first, and separate by space
 *
 */
public class NGramAnnotator implements Annotator{

	int minN;
	int maxN;
	
	public NGramAnnotator(int minN, int maxN) {
		// TODO Auto-generated constructor stub
		this.minN = minN;
		this.maxN = maxN;
	}
	
	
	@Override
	public PhrasePool annotate(PhrasePool phrases) {
		// TODO Auto-generated method stub
		PhrasePool pool = new PhrasePool();
		for(String P : phrases.allPhraseOriginalText()){
			pool.merge(ngram(phrases.getPhrase(P)));
		}
		return pool;
	}
	

	
	/*
	 * Split a long phrase into  smaller phrase. 
	 * This phrase is a unit and can only occur in one document
	 */
	public PhrasePool ngram(Phrase phrase){
		assert(phrase.getDocumentLocs().size() == 1);
		Map<String, Integer> phrases = genNGram(phrase.getOriginalText(), minN, maxN);
		Set<Integer> documentLocs = phrase.getDocumentLocs();
		
		PhrasePool pool = new PhrasePool();
		for(String p : phrases.keySet()){
			Phrase newPhrase = new Phrase(p, documentLocs);
			newPhrase.setTermFrequency(phrases.get(p));
			pool.add(newPhrase);
		}
		return pool;
	}
	
	
	
	
	/*
	 * Generate NGrams for a list of sentences.
	 */
	public Map<String, Integer> genNGram(List<String> sentences, int minN, int maxN){
		
		Map<String, Integer> all = new HashMap<String, Integer>();
		for(String sentence : sentences){
			mergeMap(all, genNGram(sentence, minN, maxN));
		}
		return all;
	}
	
	
	
	/*
	 * Generate NGrams range from [minN, maxN], all inclusive
	 */
	public Map<String, Integer> genNGram(String sent, int minN, int maxN){
		
		Map<String, Integer> all = new HashMap<String, Integer>();
		for(int n = minN; n <= maxN; n ++ ){
			mergeMap(all, genNGram(sent, n));
		}
		return all;
	}
	
	
	
	/*
	 * count the occurrence of n-gram in one sentence
	 */
	public Map<String, Integer> genNGram(String sent, int N) {
		String[] tokens = sent.split("\\s+"); // split sentence into tokens
		return sentenceNGram(Arrays.asList(tokens), N);
	}
	
	
	/**************Perfect Separating Line*****************/
	
	
	
	/*
	 * Generate NGrams for lists of sentence represented by string array "words" range from [minN, maxN], all inclusive
	 */
	public Map<String, Integer> documentNGram(List<List<String>> documentInWords, int minN, int maxN){
		Map<String, Integer> all = new HashMap<String, Integer>();
		for(List<String> sentenceInWords : documentInWords){
			mergeMap(all, sentenceNGram(sentenceInWords, minN, maxN));
		}
		return all;
	}

	
	
	
	/*
	 * Generate NGrams for one sentence represented by string array "words" range from [minN, maxN], all inclusive
	 */
	public Map<String, Integer> sentenceNGram(List<String> setenceInWords, int minN, int maxN){
		Map<String, Integer> all = new HashMap<String, Integer>();
		for(int n = minN; n <= maxN; n ++){
			mergeMap(all, sentenceNGram(setenceInWords, n));
		}
		return all;
	}
	
	
	
	
	/*
	 * count the occurrence of n-gram in one sentence represented by string array "words"
	 */
	public Map<String, Integer> sentenceNGram(List<String> sentenceInWords, int N){

		List<String> tokens = sentenceInWords;
		
		Map<String, Integer> ngramCounter = new HashMap<String, Integer>();
		// GENERATE THE N-GRAMS
		for (int k = 0; k < (tokens.size() - N + 1); k++) {
			String s = "";
			int start = k;
			int end = k + N;
			for (int j = start; j < end; j++) {
				s = s + " " + tokens.get(j);
			}
			// Add n-gram to a list
			if(!ngramCounter.containsKey(s)){
				ngramCounter.put(s, 0);
			}
			ngramCounter.put(s, ngramCounter.get(s) + 1);
		}
		return ngramCounter;
		
	}
	
	
	
	/*
	 * merge two maps storing the ngram counts.
	 */
	private void mergeMap(Map<String, Integer> dest, Map<String, Integer> source){
		
		for(String key : source.keySet()){
			if(!dest.containsKey(key)){
				dest.put(key, 0);
			}
			dest.put(key, dest.get(key) + source.get(key));
		}
	}

	

}
