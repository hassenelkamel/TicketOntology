package edu.fiu.cs.kdrg.tkrec.nlp;


public interface Annotator {

	/*
	 * transform a list of phrases into another PhrasePool with rich information. 
	 * It can also use filters. Here transformation just means ruling out
	 */
	PhrasePool annotate(PhrasePool phrases);
	
}
