package edu.fiu.cs.kdrg.tkrec.nlp;


public class PhraseRIDFFilter implements Annotator {

	double min;
	double max;
	
	public PhraseRIDFFilter(double min, double max) {
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
			double rIDF = p.getResidualIDF();
			if(rIDF >= min && rIDF <= max){
				left.add(p);
			}
		}
		return left;
	}

}
