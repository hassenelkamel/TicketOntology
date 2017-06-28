package edu.fiu.cs.kdrg.tkrec.nlp;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.stream.XMLStreamException;

import org.dom4j.DocumentException;

import edu.fiu.cs.kdrg.tkrec.core.Event;
import edu.fiu.cs.kdrg.tkrec.util.XMLEventLoader;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;



/*
 * This class provide the factory for some advanced natural language processing via StanfordNLP tool.
 * It includes tokenize, sentence split, Part-of-Speech Tag, 
 */
public class StanfordNLPAnnotator implements Annotator{
	
	private static StanfordNLPAnnotator stanfordNLPAnnotator = new StanfordNLPAnnotator();
	private StanfordCoreNLP pipeline;
	
	public StanfordNLPAnnotator() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		this.pipeline = new StanfordCoreNLP(props);
	}

	
	
	/*
	 * Singleton Pattern.
	 */
	public static StanfordNLPAnnotator getInstance(){
		return stanfordNLPAnnotator;
	}
	
	
	
	@Override
	public PhrasePool annotate(PhrasePool phrases) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	/*
	 * extract sentences from a document.
	 */
	public List<String> extractSentences(String documentText){
		
		List<String> sentences = new ArrayList<String>();
		//create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);
		
		//run all annotators on this text
		this.pipeline.annotate(document);
		
		// Iterate over all of the sentence found
		List<CoreMap> sents = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sents){
			sentences.add(sentence.get(TextAnnotation.class));
		}
		return sentences;
	}
	
	
	
	/*
	 * extract lemas from a document 
	 */
	public List<String> extractLemmas(String documentText){
		
		List<String> lemmas = new ArrayList<String>();
		
		//create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);
		
		//run all annotators on this text
		this.pipeline.annotate(document);
		
		
		// Iterate over all of the sentence found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences){
			for(CoreLabel token: sentence.get(TokensAnnotation.class)){
				lemmas.add(token.get(LemmaAnnotation.class));
			}
		}
		return lemmas;
	}
	
	/*
	 * Extract Part-of-Speech Tags for a document
	 */
	public List<String> extractPartOfSpeechTags(String documentText){
		
		List<String> tags = new ArrayList<String>();
		
		//create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);
		
		//run all annotators on this text
		this.pipeline.annotate(document);
		
		// Iterate over all of the sentence found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences){
			for(CoreLabel token: sentence.get(TokensAnnotation.class)){
				tags.add(token.get(PartOfSpeechAnnotation.class));
			}
		}
		return tags;
	}
	
	public static void main(String[] args) {
		String filename = "data2/raw_data.xml";
		StanfordNLPAnnotator annotator = new StanfordNLPAnnotator();
		XMLEventLoader xmlLoader = new XMLEventLoader(); 
		try {
			List<Event> events = xmlLoader.loadEvents(filename, 10);
			for(Event event: events) {
				System.out.println("=========");
				if(event.getValue("ALERTGROUP").equals("LFA_Event")) {
					String resolution = event.getValue("RESOLUTION") + ". " + event.getValue("SUMMARY");
					List<String> sentences = annotator.extractSentences(resolution);
					for(String sentence: sentences) {
						System.out.println(sentence);
						List<String> posTags = annotator.extractPartOfSpeechTags(sentence);
						for(String postag: posTags) System.out.print(postag + " "); 
					}
					System.out.println("=========");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	} 

}

