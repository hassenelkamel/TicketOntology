package edu.fiu.cs.kdrg.tkrec.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import edu.fiu.cs.kdrg.tkrec.util.CollectionUtil;
import edu.fiu.cs.kdrg.tkrec.util.ConceptLoader;
import edu.fiu.cs.kdrg.tkrec.util.CountMap;
import edu.fiu.cs.kdrg.tkrec.util.Pair;
import edu.fiu.cs.kdrg.tkrec.util.XMLEventLoader;

public class RConceptMatch {

	public static Set<String> loadStopWords(String stopWordFileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(stopWordFileName));
		String line = null;
		Set<String> words = new HashSet<String>();
		while ((line = reader.readLine()) != null) {
			words.add(line.toLowerCase());
		}
		reader.close();
		return words;
	}

	public static List<Event> loadEvents(String eventFileName) throws IOException, XMLStreamException,
			DocumentException {
		List<Event> events = XMLEventLoader.loadEvents(eventFileName, -1);
		return events;
	}

	
	public static List<List<Event>> cluster(List<Concept> concepts, Set<String> conceptTerms, List<Event> events) {
		// The key is the term, the value is the list of concept index having
		// this term
		Map<String, List<Integer>> conceptTermDict = new HashMap<String, List<Integer>>();

		// Build the concept term dictionary
		for (String conceptTerm : conceptTerms) {
			conceptTerm = conceptTerm.toLowerCase();
			for (int conceptIdx = 0; conceptIdx < concepts.size(); conceptIdx++) {
				Concept concept = concepts.get(conceptIdx);
				if (concept.isContainedTerm(conceptTerm)) {
					List<Integer> coveredConcepts = conceptTermDict.get(conceptTerm);
					if (coveredConcepts == null) {
						coveredConcepts = new ArrayList<Integer>();
						conceptTermDict.put(conceptTerm, coveredConcepts);
					}
					coveredConcepts.add(conceptIdx);
				}
			}
		}

		// The key is the concept index, the value is the matched text list
		Map<Integer, List<Event>> conceptClusters = new HashMap<Integer, List<Event>>();
		// Remaining text
		List<Event> remainingCluster = new ArrayList<Event>();

		for (Event e : events) {
			String lowerText = e.getValue("SOLUTION_DESC").toLowerCase();
			boolean isMatched = false;
			for (String conceptTerm : conceptTerms) {
				int matchCount = StringUtils.countMatches(lowerText, conceptTerm);
				if (matchCount == 0) {
					continue;
				}
				List<Integer> coveredConcepts = conceptTermDict.get(conceptTerm);
				if (coveredConcepts == null || coveredConcepts.size() == 0) {
					continue;
				}
				isMatched = true;
				for (Integer conceptIndex : coveredConcepts) {
					List<Event> textCluster = conceptClusters.get(conceptIndex);
					if (textCluster == null) {
						textCluster = new ArrayList<Event>();
						conceptClusters.put(conceptIndex, textCluster);
					}
					textCluster.add(e);
				}
			}

			if (isMatched == false) {
				remainingCluster.add(e);
			}
		}

		List<List<Event>> clusters = new ArrayList<List<Event>>();
		System.out.println("+++++++++++++++++++++++++++");
		for (Integer key : conceptClusters.keySet()) {
			System.out.println(concepts.get(key).getName());
			clusters.add(conceptClusters.get(key));
		}
		System.out.println("+++++++++++++++++++++++++++");
		// for (List<Event> cluster: conceptClusters.values()) {
		// clusters.add(cluster);
		// }
		clusters.add(remainingCluster);

		return clusters;
	}

	public static Set<String> generateNewConceptTerms(List<Event> cluster, List<Event> events, Set<String> stopWords) {
		// Count each word counts in the cluster
		CountMap<String> wordCounts = new CountMap<String>();
		for (Event etext : cluster) {
			String text = etext.getValue("INCID_DESC").toLowerCase();
			String[] tokens = text.split("\\s+");
			for (String token : tokens) {
				if (stopWords.contains(token)) {
					continue;
				}
				wordCounts.add(token);
			}
		}

		// Count each word in all texts' count
		CountMap<String> globalWordCounts = new CountMap<String>();
		for (Event e : events) {
			String text = e.getValue("INCID_DESC").toLowerCase();
			String[] tokens = text.split("\\s+");
			for (String token : tokens) {
				if (stopWords.contains(token)) {
					continue;
				}
				globalWordCounts.add(token);
			}
		}

		// Compute the if-idf score for each word
		List<Pair<String, Double>> termScoreList = new ArrayList<Pair<String, Double>>(wordCounts.size());
		for (String term : wordCounts.keySet()) {
			int countInCluster = wordCounts.get(term);
			double tf = ((double) countInCluster) / cluster.size();
			int countInAll = globalWordCounts.get(term);
			double idf = Math.log(events.size() / ((double) countInAll));
			double tfidf = tf * idf;
			if (countInCluster == 0 || countInAll == 0) {
				tfidf = 0;
			}
			termScoreList.add(new Pair<String, Double>(term, tfidf));
		}

		// Ranking by tf-idf score
		Collections.sort(termScoreList, new Comparator<Pair<String, Double>>() {
			@Override
			public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
				// TODO Auto-generated method stub
				return o1.getSecond().compareTo(o2.getSecond());
			}

		});
		Collections.reverse(termScoreList);

		// Print top terms
		Set<String> newConceptTerms = new HashSet<String>();
		for (int i = 0; i < 20; i++) {
			if (i >= termScoreList.size()) {
				break;
			}
			Pair<String, Double> termScore = termScoreList.get(i);
			System.out.println(termScore.getFirst() + " : " + termScore.getSecond());
			newConceptTerms.add(termScore.getFirst());
		}
		return newConceptTerms;
	}

	/**
	 * Save the clustering results into a file
	 * 
	 * @param clusters
	 * @param fileName
	 * @throws IOException
	 */
	public static void saveClusters(List<List<Event>> clusters, String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		for (List<Event> cluster : clusters) {
			writer.write("==============cluster ==============\n");
			for (Event resolution : cluster) {
				writer.write(resolution.getValue("ID"));
				writer.write("\t" + resolution.getValue("SOLUTION_DESC").trim());
				writer.write("\t" + resolution.getValue("INCID_DESC").trim());
				writer.write("\n");
			}
		}
		writer.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.out.println("loading data file....");
			// List<String> resolutionTexts =
			// ConceptMatch.loadResolutions("data/Amp_Labeled_Events_Oct_Dec_10.xml");
			List<Event> resolutionTexts = RConceptMatch.loadEvents("data/data.xml");
			System.out.println("loading taxonomy...");
			ConceptLoader loader = new ConceptLoader();
//			 loader.load("data/ret.xml");
			loader.load("data/taxonomy.xml");

			System.out.println("loading english stop words...");
			Set<String> stopWords = RConceptMatch.loadStopWords("data/english.stop");

			System.out.println("starting clustering...");
			List<Concept> concepts = CollectionUtil.asList(loader.getConceptsWithoutRoot());
			List<List<Event>> clusters = RConceptMatch.cluster(concepts, loader.getTerms(), resolutionTexts);
			RConceptMatch.saveClusters(clusters, "rclusters.txt");

			System.out.println("generating new concept term candidates....");
			Set<String> newConceptTerms = RConceptMatch.generateNewConceptTerms(clusters.get(clusters.size() - 1),
					resolutionTexts, stopWords);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
