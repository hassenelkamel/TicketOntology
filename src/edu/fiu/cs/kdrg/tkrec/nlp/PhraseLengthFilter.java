package edu.fiu.cs.kdrg.tkrec.nlp;


public class PhraseLengthFilter implements Annotator{

	int min;
	int max;
	
	
	public PhraseLengthFilter(int minL, int maxL) {
		// TODO Auto-generated constructor stub
		this.min = minL;
		this.max = maxL;
	}
	
	
	@Override
	public PhrasePool annotate(PhrasePool phrases) {
		// TODO Auto-generated method stub
		PhrasePool left = new PhrasePool();
		for(String phrase : phrases.allPhraseOriginalText()){
			Phrase p = phrases.getPhrase(phrase);
			int length = p.getOriginalText().split(" ").length;
			if(length >= min && length <= max){
				left.add(p);
			}
		}
		
		return left;
	}
	
}
