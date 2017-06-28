package edu.fiu.cs.kdrg.tkrec.nlp;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhrasePosFilter implements Annotator {

	
	/*
	 * This is Corrected regular expression according to our test and 
	 * <Paper>Technical terminology:Some linguistic Properties and an algorithm for identification in text<Paper>
	 */
	public static String LEGAL_POS_REG_CORRECTED = 
			"JJ NN(S|PS)?|NN(S|PS)? NN(S|PS)?|JJ JJ NN(S|PS)?|JJ NN(S|PS)? NN(S|PS)?|"
		  + "NN(S|PS)? JJ NN(S|PS)?|NN(S|PS)? VBN NN(S|PS)?|NN(S|PS)? NN(S|PS)? NN(S|PS)?|NN(S|PS)? IN NN(S|PS)?";
	
	public static Pattern LEGAL_POS_PATTERN = Pattern.compile(LEGAL_POS_REG_CORRECTED);
	
	
	@Override
	public PhrasePool annotate(PhrasePool phrases) {
		// TODO Auto-generated method stub
		PhrasePool left = new PhrasePool();
		for(String phrase : phrases.allPhraseOriginalText()){
			Phrase p = phrases.getPhrase(phrase);
			String pos = String.join(" ", p.getPoSTags());
			Matcher m = LEGAL_POS_PATTERN.matcher(pos);
			if(m.matches()){
				left.add(p);
			}
		}
		return left;
	}

}
