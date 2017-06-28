package edu.fiu.cs.kdrg.tkrec.nlp;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 
 *A map of key, value storing possible property value for this phrase. 
 *Phrase can be instantiated as document, paragraph, sentence, phrase, words. 
 *It property varies from document id, term frequency, document frequency, residual IDF, words number, PoS tags.
 *For simplicity, all the property value are materialized as string. 
 *
 */
public class Phrase {

	private String originalText;
	private Set<Integer> documentLocs = new HashSet<>(); // documents occurred
	private List<String> lemmas = new ArrayList<>();
	private int corpusSize;
	private int termFrequency;
	private int documentFrequency;
    private double ResidualIDF;
    private List<String> PoSTags = new ArrayList<>();
    
    
    public Phrase(String rawText, int documentLoc){
    	originalText = rawText;
    	documentLocs.add(documentLoc);
    }
    
    
    public Phrase(String rawText, Set<Integer> documentLocs) {
		// TODO Auto-generated constructor stub
    	originalText = rawText;
    	documentLocs.addAll(documentLocs);
	}
    
    
	/*
	 * merge two same phrases occur differently. 
	 * It is only necessary to merge those attributes that might be different.
	 */
	public void merge(Phrase another){
		assert(this.equals(another));
		this.updateDocumentLocs(another.getDocumentLocs());
		this.setTermFrequency(this.getTermFrequency() + another.getTermFrequency());
		this.setDocumentFrequency(this.getDocumentFrequency() + another.getDocumentFrequency());
	}
    
    
	public String getOriginalText() {
		return originalText;
	}


	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}


	public int getCorpusSize() {
		return corpusSize;
	}


	public void setCorpusSize(int corpusSize) {
		this.corpusSize = corpusSize;
	}


	public Set<Integer> getDocumentLocs() {
		return documentLocs;
	}


	public void updateDocumentLocs(Set<Integer> documentLocs){
		this.getDocumentLocs().addAll(documentLocs);
	}
	
	public List<String> getLemmas() {
		return lemmas;
	}


	public void setLemmas(List<String> lemmas) {
		this.lemmas = lemmas;
	}


	public int getTermFrequency() {
		return termFrequency;
	}


	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}


	public int getDocumentFrequency() {
		return documentFrequency;
	}


	public void setDocumentFrequency(int documentFrequency) {
		this.documentFrequency = documentFrequency;
	}


	public double getResidualIDF() {
		return ResidualIDF;
	}


	public void setResidualIDF(double residualIDF) {
		ResidualIDF = residualIDF;
	}


	public List<String> getPoSTags() {
		return PoSTags;
	}


	public void setPoSTags(List<String> poSTags) {
		PoSTags = poSTags;
	}


	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return getOriginalText().hashCode();
	}
	
}
