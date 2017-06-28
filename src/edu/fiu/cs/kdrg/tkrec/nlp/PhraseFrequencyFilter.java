package edu.fiu.cs.kdrg.tkrec.nlp;


public class PhraseFrequencyFilter implements Annotator {

	int min;
	int max;
	
	
	public PhraseFrequencyFilter(int min, int max) {
		// TODO Auto-generated constructor stub
		this.min = min;
		this.max = max;
	}
	
	
	
	@Override
	public PhrasePool annotate(PhrasePool phrases) {
		// TODO Auto-generated method stub
		PhrasePool left = new PhrasePool();
		for(String phrase : phrases.allPhraseOriginalText()){
			Phrase p = phrases.getPhrase(phrase);
			int frequency = p.getTermFrequency();
			if(frequency >= min && frequency <= max){
				left.add(p);
			}
		}
		
		return left;
	}

}
