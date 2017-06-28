package edu.fiu.cs.kdrg.tkrec.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;


import edu.fiu.cs.kdrg.tkrec.core.Concept;
import edu.fiu.cs.kdrg.tkrec.core.Event;
import edu.fiu.cs.kdrg.tkrec.nlp.Preprocesser;
import edu.fiu.cs.kdrg.tkrec.nlp.StanfordNLPAnnotator;
import edu.fiu.cs.kdrg.tkrec.util.CollectionUtil;
import edu.fiu.cs.kdrg.tkrec.util.ConceptLoader;
import edu.fiu.cs.kdrg.tkrec.util.CountMap;
import edu.fiu.cs.kdrg.tkrec.util.IOUtil;
import edu.fiu.cs.kdrg.tkrec.util.Pair;
import edu.fiu.cs.kdrg.tkrec.util.RegPatterns;
import edu.fiu.cs.kdrg.tkrec.util.XMLEventLoader;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Extract concepts
 * 
 * @author qingwang
 *
 */
public class ConceptExtract {
	
	public static Set<String> loadStopWords(String stopWordFileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(stopWordFileName));
		String line = null;
		Set<String> words = new HashSet<String>();
		while((line = reader.readLine()) != null) {
			words.add(line.toLowerCase());
		}
		reader.close();
		return words;
	}
	
	public static String tokenizedString(String rawString, String regex) {
		if (null == rawString) return null;
		return rawString.replaceAll(regex, " ");
	}
	
	public static List<String> loadResolutions(String eventFileName) throws IOException, XMLStreamException,
			DocumentException {
		List<Event> events = XMLEventLoader.loadEvents(eventFileName, -1);
		List<String> eSol = new ArrayList<String>(events.size());
		for (Event e : events) {
			eSol.add(tokenizedString(e.getValue("RESOLUTION"), RegPatterns.TOKENIZE_SENTENCE_REG));
		}
		events = null;
		return eSol;
	}
	
	/**
	 * stop word filter
	 * @param resolutions
	 * @param stopWords
	 * @return
	 */
	public static List<String> stopWordFilter(List<String> resolutions, Set<String> stopWords) {
		
		List<String> results =Preprocesser.removeAchorWords(resolutions);
		List<String> eResults =  new ArrayList<String>(results.size());
	
		for (String text: results) {
			text = text.toLowerCase();
			String[] tokens = text.split("\\s+");
			StringBuffer sb = new StringBuffer();
			for (String token : tokens) {
				if (stopWords.contains(token)) {
					continue;
				}
				sb.append(token + " ");
			}
			eResults.add(sb.toString());
		}	
		return eResults;
	} 

	
	/**
	 * pos tag filter for noun and verb
	 * @param resolutions
	 * @return
	 */
	
	public final static String[] LEGAL_POS = new String[]{
		"NN",  //noun, singular or mass 
		"NNS", //noun, plural
		"NNP", //proper noun, singular
		"NNPS", // proper noun, plural
		"NP", // noun phrase
		"VP", // verb phrase
		"VB", // verb, base form
		"VBD", // verb, past tense
		"VBG", // verb, gerund
		"VBN", // verb, past participle
		"VBP", // verb, non-3rd person singular present
		"VBZ"  // verb, 3rd person singular present
	};
	
	public static List<String> posTagFilter(List<String> resolutions) {
		List<String> eResults =  new ArrayList<String>(resolutions.size());

		List<String> tagList = Arrays.asList(LEGAL_POS);
		MaxentTagger tagger =  new MaxentTagger("tagger/english-left3words-distsim.tagger");
		
		for(String text: resolutions) {
			String tagged = tagger.tagString(text);
			String[] tokens = tagged.split("\\s+");
			StringBuffer sb = new StringBuffer();
			for(String token: tokens) {
				String[] words = token.split("_");
				if(words.length < 2) continue;
				if(tagList.contains(words[1])) {
					StanfordNLPAnnotator nlpAnnotator = new StanfordNLPAnnotator().getInstance();
					List<String> lemmaWords = nlpAnnotator.extractLemmas(words[0]);
					sb.append(lemmaWords.get(0) + "_" + words[1] + " ");
				}
			}
			eResults.add(sb.toString());
		}
		
		return eResults;
	}
	
	
	/**
	 * tfidf
	 * @param resolutions
	 * @return
	 */
	public final static String[] NOUN_POS = new String[]{
			"NN",  //noun, singular or mass 
			"NNS", //noun, plural
			"NNP", //proper noun, singular
			"NNPS", // proper noun, plural
			"NP", // noun phrase
	};
	public static Set<String> generateTFIDFTerms(List<String> resolutions, String outFile) {
		List<String> tagList = Arrays.asList(NOUN_POS);
		// Count each word counts in the cluster
		CountMap<String> wordCounts = new CountMap<String>();
		int totalWords = 0;
		int nounWords = 0;
		for (String text: resolutions) {
			String[] tokens = text.split("\\s+");
			totalWords += tokens.length;
			for (String token : tokens) {
				String[] words = token.split("_");
				if(words.length < 2) continue;
				if (!tagList.contains(words[1])) {
					continue;
				}
				
				wordCounts.add(words[0]);
				nounWords += 1;
			}
		}
		
		// Compute the tf-idf score for each noun word
		List<Pair<String,Double>> termScoreList = new ArrayList<Pair<String,Double>>(wordCounts.size()); 
		for (String term: wordCounts.keySet()) {
			int countTerm = wordCounts.get(term);
			double tf = ((double)countTerm) / nounWords;
			double idf = Math.log(totalWords / ((double)countTerm));
			double tfidf = tf*idf;
			if (countTerm == 0 ){
				tfidf = 0;
			}
			termScoreList.add(new Pair<String,Double>(term, tfidf));
		}
		System.out.println("The number of noun terms is : " + wordCounts.size());
		
		// Ranking by tf-idf score
		Collections.sort(termScoreList, new Comparator<Pair<String,Double>>() {
			@Override
			public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
	 			// TODO Auto-generated method stub
				return o1.getSecond().compareTo(o2.getSecond());
			}
			
		});
		Collections.reverse(termScoreList);
		
		// Print top terms
		Set<String> tfidfConceptTerms = new HashSet<String>();
		for (int i=0; i<1000; i++) {
			if (i >= termScoreList.size()) {
				break;
			}
			Pair<String,Double> termScore = termScoreList.get(i);
			System.out.println(termScore.getFirst()+" : "+termScore.getSecond());
			tfidfConceptTerms.add(termScore.getFirst());
		}
		
		// output result
		String[] output = tfidfConceptTerms.toArray(new String[tfidfConceptTerms.size()]);
		StringBuffer sb = new StringBuffer();
		for(String item: output) {
			if (item.length()<3) continue;
			sb.append(item+"\n");
		}
		
		IOUtil.writeString(sb.toString(), outFile, IOUtil.defaultCharset);
			
		return tfidfConceptTerms;
	} 
	
	/**
	 * Extract Episode
	 * @param resolutions
	 * @return
	 */
	public static List<String> extractEpisode(List<String> resolutions) {
		for(String text: resolutions) {
			
		}
		
		return null;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			
			System.out.println("loading data file....");
			List<String> resolutionTexts = ConceptExtract.loadResolutions("data2/raw_data.xml");
			
			System.out.println("loading english stop words...");
			Set<String> stopWords = ConceptExtract.loadStopWords("data/english.stop");
			
			System.out.println("starting stop word filtering...");
			List<String> resultOfStopWordFilter = ConceptExtract.stopWordFilter(resolutionTexts, stopWords);
			
			System.out.println("starting POS tags filtering...");
			List<String> resultOfPosTagFilter = ConceptExtract.posTagFilter(resultOfStopWordFilter);
			
			System.out.println("starting TFIDF...");
			Set<String> importantTerms = ConceptExtract.generateTFIDFTerms(resultOfPosTagFilter, "data2/tfidf.txt");
			
			System.out.println("extracting the episode...");
			List<String> resultOfExtractEpisode = ConceptExtract.extractEpisode(resultOfStopWordFilter);

		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
