package edu.fiu.cs.kdrg.tkrec.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

 
/**
 * Wu & Palmer – Words Similarity
 * The Wu & Palmer calculates relatedness by considering the depths of 
 * the two synsets in the WordNet taxonomies, along with the depth of the LCS (Least Common Subsumer).
 * The formula is score = 2 * depth (lcs) / (depth (s1) + depth (s2)).
 *
 *
 */
public class WordSimilarity {
 
	private static ILexicalDatabase db = new NictWordNet();
	private static RelatednessCalculator rc = new WuPalmer(db);
	
	//available options of metrics
	private static RelatednessCalculator[] rcs = { new HirstStOnge(db),
			new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
			new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db) };
	
	public static double calculator(RelatednessCalculator rc, String word1, POS posWord1, String word2, POS posWord2) {
	    double maxScore = 0D;
		try {
			WS4JConfiguration.getInstance().setMFS(true);
			List<Concept> synsets1 = (List<Concept>) db.getAllConcepts(word1, posWord1.name());
			List<Concept> synsets2 = (List<Concept>) db.getAllConcepts(word2, posWord2.name());
			for (Concept synset1 : synsets1) {
				for (Concept synset2 : synsets2) {
					Relatedness relatedness = rc.calcRelatednessOfSynset(synset1, synset2);
					double score = relatedness.getScore();
					if (score > maxScore) {
						maxScore = score;
					}
				}
			}
//			System.out.println("Similarity score of " + word1 + " & " + word2 + " : " + maxScore);
			} catch (Exception e) {
				System.out.println("Exception : "+ e);
			}
			return maxScore;
		}
 
	
	private static double computeWupalmer(String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(true);
		double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
		return s;
	}
	
	private static double computePath(String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(true);
		double s = new Path(db).calcRelatednessOfWords(word1, word2);
		return s;
	}
	
	private static double computeLin(String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(true);
		double s = new Lin(db).calcRelatednessOfWords(word1, word2);
		return s;
	}
	
	public static double combineSimilarity(String word1, String word2) {
		double w_1 = 0.3;
		double w_2 = 0.3;
		double w_3 = 0.4;
		
		double path = computePath(word1, word2);
		double lin = computeLin(word1, word2);
		double wupalmer = computeWupalmer(word1, word2);
		
//		return lin;
		
		return path * w_1 + lin * w_2 + wupalmer * w_3;
	}
	
	public static void main(String[] args) {
		String[] words = {"time", "hour"};
		System.out.println(combineSimilarity(words[0], words[1]));
	}
}