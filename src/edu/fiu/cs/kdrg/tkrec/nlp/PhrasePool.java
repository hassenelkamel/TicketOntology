package edu.fiu.cs.kdrg.tkrec.nlp;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * List of Phrases
 */
public class PhrasePool {
	
	private Map<String, Phrase> pool = new HashMap<String,Phrase>();
	
	/*
	 * Because of the content documents, they can not have char same as Phrase delimiter.
	 * This function should not be a constructor here. It should be an annotator
	 */
	public PhrasePool(List<String> documents) {
		// TODO Auto-generated constructor stub
		for(int i = 0; i < documents.size(); i ++){
			
			String key = documents.get(i);
			Phrase iPhrase = new Phrase(key,i);
			
			if(pool.containsKey(key)){//merge
				pool.get(key).merge(iPhrase);
			}else{
				pool.put(key, iPhrase);
			}
		}
	}
	
	
	public PhrasePool() {
		// TODO Auto-generated constructor stub
	}
	
	
	/*
	 * Add a phrase to this pool.
	 * if it already exists, then merge those two same phrases into one,
	 * otherwise add it to this pool
	 */
	public void add(Phrase phrase){
		String key = phrase.getOriginalText();
		if(pool.containsKey(key)){
			pool.get(key).merge(phrase);
		}else{
			pool.put(key, phrase);
		}
	}
	
	
	
	/*
	 * merge two PhrasePool
	 */
	public void merge(PhrasePool anotherPool){
		for(String phrase : anotherPool.allPhraseOriginalText()){
			this.add(anotherPool.getPhrase(phrase));
		}
	}
	
	
	public Map<String, Phrase> getPool() {
		return pool;
	}


	public Set<String> allPhraseOriginalText(){
		return getPool().keySet();
	}
	
	
	public Phrase getPhrase(String rawPhrase){
		return getPool().get(rawPhrase);
	}
	
	
	public boolean contains(String phrase){
		return getPool().containsKey(phrase);
	}
	
}
