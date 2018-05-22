package edu.fiu.cs.kdrg.tkrec.automaton.test;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.dom4j.DocumentException;

import edu.fiu.cs.kdrg.tkrec.algorithm.ClassTagger;
import edu.fiu.cs.kdrg.tkrec.algorithm.PhraseFilters;
import edu.fiu.cs.kdrg.tkrec.algorithm.PostProcess;
import edu.fiu.cs.kdrg.tkrec.algorithm.RepeatedPatternExtraction;
import edu.fiu.cs.kdrg.tkrec.algorithm.TicketKnowledgeExtraction;
import edu.fiu.cs.kdrg.tkrec.data.DBEventLoader;
import edu.fiu.cs.kdrg.tkrec.util.DateUtil;

/**
 * The first phrase to build knowledge phrases, and the second phrase 
 * @author qingwang
 *
 */

public class AutomatonpatternExtractionExp {
	
	public static final String DB_EVENT_XML_FILENMAE = "data2/automaton/raw_automaton.xml";
	public static final String REPEATED_PATTERN_PHRASE_FILENAME = "data2/automaton/result_pattern_extraction_10";
	public static final String ENTITY_PHRASES_FILENAME = "data2/automaton/result_entity_phrases";
	public static final String ACTTION_PHRASES_FILENAME = "data2/automaton/result_action_phrases";
	public static final String STATISTICS_CLASSTAGGER_FILENAME = "data2/automaton/result_statistics_classtagger";
	public static final String POSTPROCESS_CLASSTAGGER_FILENAME = "data2/result_postprocess_classtagger";
	
	
	/**
	 * Run demo
	 */
	public static void run() {
		
		try {
			System.out.println("1. start loading DB data to XML file...");
			DBEventLoader.loadDBData2File(DB_EVENT_XML_FILENMAE);
			System.out.println("1. finish loading DB data...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(DateUtil.getDate() + " ===================================");
		try {
			System.out.println("2. start extracting repeated pattern phrases...");
			RepeatedPatternExtraction.experiment(DB_EVENT_XML_FILENMAE, REPEATED_PATTERN_PHRASE_FILENAME);
			System.out.println("2. finish extracting...");
		} catch (IOException | XMLStreamException | DocumentException e) {
			e.printStackTrace();
		}
		System.out.println(DateUtil.getDate() + " ===================================");
		
		System.out.println("===================================");
		System.out.println("3. start filtering repeated pattern phrases...");
		PhraseFilters.filters(REPEATED_PATTERN_PHRASE_FILENAME, ENTITY_PHRASES_FILENAME, ACTTION_PHRASES_FILENAME);
		System.out.println("3. finish filtering...");
		
		System.out.println("===================================");
		try {
			System.out.println("4. start tagging tickets...");
			ClassTagger.experiment(DB_EVENT_XML_FILENMAE, STATISTICS_CLASSTAGGER_FILENAME);
			System.out.println("4. finsh tagging...");
		} catch (IOException | XMLStreamException | DocumentException e) {
			e.printStackTrace();
		}
		
		System.out.println("===================================");
		System.out.println("5. start postprocessing tagged tickets...");
		PostProcess.experiment(STATISTICS_CLASSTAGGER_FILENAME, POSTPROCESS_CLASSTAGGER_FILENAME);
		System.out.println("5. finish postprocessing...");
		
		System.out.println("===================================");
		System.out.println("6. start extracting problem, activity and action...");
		TicketKnowledgeExtraction extraction =  new TicketKnowledgeExtraction();
		extraction.experiment(POSTPROCESS_CLASSTAGGER_FILENAME);
		System.out.println("6. finish extracting...");
		
	} 
	
	public static void main(String[] args) {
		run();
	}
}
