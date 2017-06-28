package edu.fiu.cs.kdrg.tkrec.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import edu.fiu.cs.kdrg.tkrec.clustering.KMeans;
import edu.fiu.cs.kdrg.tkrec.core.Concept;
import edu.fiu.cs.kdrg.tkrec.core.CosineSimilarity;
import edu.fiu.cs.kdrg.tkrec.core.Event;
import edu.fiu.cs.kdrg.tkrec.core.SparseVector;
import edu.fiu.cs.kdrg.tkrec.util.ConceptLoader;
import edu.fiu.cs.kdrg.tkrec.util.Pair;
import edu.fiu.cs.kdrg.tkrec.util.XMLEventLoader;

public class ConceptCluster {

	/**
	 * @param eventFileName
	 * @return
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws DocumentException
	 */
	public static List<Pair<String, String>> loadSolution(String eventFileName) throws IOException, XMLStreamException,
			DocumentException {
		List<Event> events = XMLEventLoader.loadEvents(eventFileName, -1);
		List<Pair<String, String>> eSol = new ArrayList<Pair<String, String>>();
		for (Event e : events) {
			Pair<String, String> p = new Pair<String, String>(e.getValue("EventID"), e.getValue("SOLUTION_DESC"));
			eSol.add(p);
		}
		events = null;
		return eSol;
	}

	/**
	 * @param src
	 * @param term
	 * @return
	 */
	public static int getFreq(String src, String term) {
		if (src == null)
			return 0;
		return StringUtils.countMatches(src.toLowerCase(), term.toLowerCase());
	}

	/**
	 * @param eventTickets
	 * @param conceptSet
	 * @param conceptTermSet
	 * @return
	 */
	public static SparseVector[] transform(List<Pair<String, String>> eventTickets, Set<Concept> conceptSet, 
			Set<String> conceptTermSet) {
		
		// Convert the set into array
		Object[] objs = conceptSet.toArray();
		Concept[] concepts = new Concept[objs.length];
		for (int index = 0; index < objs.length; index++) {
			concepts[index] = (Concept) objs[index];
		}

		objs = conceptTermSet.toArray();
		String[] conceptTerms = new String[objs.length];
		for (int index = 0; index < objs.length; index++) {
			conceptTerms[index] = (String) objs[index];
		}
		
		// Build the term dictionary for the raw textual content of ticket resolutions
		Map<String, Integer> termDict = new HashMap<String, Integer>();
		
		// The key is the term, the value is the list of concept index having this term
		Map<String, List<Integer>> conceptTermDict = new HashMap<String, List<Integer>>();
		
		// Build the concept term dictionary
		for (String conceptTerm: conceptTermSet) {
			conceptTerm = conceptTerm.toLowerCase();
			for (int conceptIdx=0; conceptIdx<concepts.length; conceptIdx++) {
				Concept concept = concepts[conceptIdx];				
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
		
		SparseVector[] vectors = new SparseVector[eventTickets.size()];
		
		// Convert to raw terms into the sparse vector
		for (int i = 0; i < eventTickets.size(); i++) {						
			Pair<String,String> eventTicket = eventTickets.get(i);
			
			// Create a sparse vector
			vectors[i] = new SparseVector();
			
			// Get the resolution text
			String resolution = eventTicket.getSecond();
			resolution = resolution.toLowerCase();
			
			// Split it to terms
			String[] tokens = resolution.split("\\s+");
			
			for (String token : tokens) {
				Integer termIndex = termDict.get(token);
				if (termIndex == null) {
					termIndex = termDict.size();
					termDict.put(token, termIndex);
				}
				vectors[i].addValue(termIndex, 1);
			}
		}
		
		// Convert matched concepts into the sparse vector
		for (int i = 0; i < eventTickets.size(); i++) {						
			Pair<String,String> eventTicket = eventTickets.get(i);
						
			// Get the resolution text
			String resolution = eventTicket.getSecond();
			resolution = resolution.toLowerCase();
			
			// Split it to terms
			for (String conceptTerm : conceptTerms) {
				int count = StringUtils.countMatches(resolution, conceptTerm);
				if (count == 0) {
					continue;
				}
				List<Integer> conceptIndices = conceptTermDict.get(conceptTerm);
				for (Integer conceptIdx: conceptIndices) {
					vectors[i].addValue(termDict.size() + conceptIdx, 10*count);
				}
			}
		}
		
		// Normalize the vectors
		for (int i=0; i<vectors.length; i++) {
			vectors[i] = vectors[i].normalize();
		}

		return vectors;
	}

	/**
	 * @param instLabels
	 * @param pairs
	 * @param k
	 * @return
	 */
	public static List<List<Pair<String,String>>> mappingEventID(int[] instLabels, List<Pair<String, String>> pairs, int k) {
		List<List<Pair<String,String>>> ret = new ArrayList<List<Pair<String,String>>>();
		for (int i = 0; i < k; i++) {
			ret.add(new ArrayList<Pair<String,String>>());
		}
		for (int j = 0; j < instLabels.length; j++) {
			try {
				ret.get(instLabels[j]).add(pairs.get(j));
			} catch (Exception ex) {
				System.out.println(instLabels[j] + " " + pairs.get(j).getFirst());
			}
		}
		return ret;

	}

	/**
	 * @param pairs
	 * @param concepts
	 * @param terms
	 * @param k
	 * @return
	 */
	public static List<List<Pair<String,String>>> ontologyCluster(List<Pair<String, String>> pairs, Set<Concept> concepts,
			Set<String> terms, int k) {
		System.out.println("transform sparse vectors...");
		SparseVector[] vectors = ConceptCluster.transform(pairs, concepts, terms);
		CosineSimilarity cosSim = new CosineSimilarity();
		System.out.println("do k-means clustering...");
		KMeans kmeans = new KMeans(vectors, k, cosSim);
		int instLabels[] = kmeans.build();
		return mappingEventID(instLabels, pairs, k);
	}

	/**
	 * @param args
	 * @throws DocumentException
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, XMLStreamException, DocumentException {
		System.out.println("loading data file....");
		List<Pair<String, String>> eventTickets = ConceptCluster.loadSolution("data/Amp_Labeled_Events_Oct_Dec_10.xml");
		System.out.println("loading taxonomy...");
		ConceptLoader loader = new ConceptLoader();
		loader.load("data/taxonomy.xml");
		int k = 5;
		List<List<Pair<String,String>>> ret = ConceptCluster.ontologyCluster(eventTickets, loader.getConceptsWithoutRoot(), loader.getTerms(), k);
		for (List<Pair<String,String>> cluster : ret) {
			System.out.println("==============cluster ==============");
			for (Pair<String,String> eventTicket: cluster) {
				System.out.println(eventTicket.getFirst()+"  :  "+eventTicket.getSecond());
			}
		}
	}
}
