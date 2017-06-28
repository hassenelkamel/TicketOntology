package edu.fiu.cs.kdrg.tkrec.nlp;


import java.util.HashSet;
import java.util.Set;

import edu.fiu.cs.kdrg.tkrec.core.SimilarityFunction;

/**
 * Calculating similarity between two <tt>String</tt> objects.
 * Jaccard Similarity approach has been proposed here. 
 * 
 * @author zhouwubai
 * @date Apr 25, 2014
 * @email zhouwubai@gmail.com
 * Apache Licence 2.0
 */
public class TextTermSimilarity implements SimilarityFunction<String> {

	String splitter = "\\s+";//preprocess the string before compare.
	
	@Override
	public double sim(String o1, String o2) {
		
		if(o1 == null || o2 == null) return 0;
		// TODO Auto-generated method stub
		String[] tokens1 = o1.split(splitter);
		String[] tokens2 = o2.split(splitter);
		Set<String> tokenSet1 = new HashSet<String>();
		Set<String> tokenSet2 = new HashSet<String>();
		for (int i=0; i<tokens1.length; i++) {
			if(!tokens1[i].trim().isEmpty())
				tokenSet1.add(tokens1[i].toLowerCase());
		}
		for (int i=0; i<tokens2.length; i++) {
			if(!tokens2[i].trim().isEmpty())
				tokenSet2.add(tokens2[i].toLowerCase());
		}
		int common = 0;
		for(String token1: tokenSet1) {
			if (tokenSet2.contains(token1)) {
				common++;
			}
		}
		if (tokenSet1.size()+tokenSet2.size()-common == 0) {
			return 0;
		}
		else {
			return (double)(common) / ((double)(tokenSet1.size()+tokenSet2.size()-common));
		}
	}

	@Override
	public double maxValue() {
		// TODO Auto-generated method stub
		return 1.0;
	}

	@Override
	public double minValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	public static void main(String[] args) {
		
		String str1 = "gsd1u903c mpw_prcmiss_xuxc_tsm[(SYSTEM.SEARCH(UNIXPS.UCMD,MISSING,N/opt//tivoli//tsm//server//bin//dsm";
		String str2 = "gsd1u903c mpw_prcmiss_xuxc_tsm[(SYSTEM.SEARCH(UNIXPS.UCMD,abc,N/opt//ccdd//tsm//'server//bin//dsm";
		
		TextTermSimilarity simFunc = new TextTermSimilarity();
		
		System.out.println(simFunc.sim(str1, str2));
		
	}

}
