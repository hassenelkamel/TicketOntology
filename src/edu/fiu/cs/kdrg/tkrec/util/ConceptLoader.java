package edu.fiu.cs.kdrg.tkrec.util;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.fiu.cs.kdrg.tkrec.core.Concept;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Chunqiu Zeng
 * 
 */
public class ConceptLoader {

	private HashSet<Concept> concepts = new HashSet<Concept>();

	private HashSet<String> terms = new HashSet<String>();
	
	private Concept rootConcept = null;

	public ConceptLoader() {
	}

	/**
	 * @return the concepts
	 */
	public HashSet<Concept> getConcepts() {
		return concepts;
	}
	
	/**
	 * 
	 * @return the concepts without the root concept
	 */
	public Set<Concept> getConceptsWithoutRoot() {
		Set<Concept> conceptsNoRoot = new HashSet<Concept>();
		conceptsNoRoot.addAll(concepts);
		conceptsNoRoot.remove(rootConcept);
		return conceptsNoRoot;
	}

	/**
	 * @param concepts
	 *            the concepts to set
	 */
	public void setConcepts(HashSet<Concept> concepts) {
		this.concepts = concepts;
	}

	/**
	 * @return the terms
	 */
	public HashSet<String> getTerms() {
		return terms;
	}

	/**
	 * @param terms
	 *            the terms to set
	 */
	public void setTerms(HashSet<String> terms) {
		this.terms = terms;
	}

	public void addConcept(Concept c) {
		concepts.add(c);
	}

	public void addTerms(String t) {
		terms.add(t);
	}

	public void load(String xmlFilePath) throws DocumentException {
		File xmlFile = new File(xmlFilePath);
		SAXReader xmlReader = new SAXReader();
		Document doc = xmlReader.read(xmlFile);
		Element root = doc.getRootElement();
		rootConcept = recursiveConstruct(root);
	}

	public Concept recursiveConstruct(Element element) {
		String name = element.getName();
		Concept ret = null;
		if (name.equals("concept")) {
			ret = new Concept(element.attributeValue("name"));
			List<Element> eles = element.elements();
			for (Element e : eles) {
				Concept c = recursiveConstruct(e);
				if (c != null) {
					ret.addChildConcept(c);
				} else {
					String ts = e.getTextTrim();
					String[] tArray = ts.split(",");
					for (String s : tArray) {
						ret.addTerm(s.trim());
						terms.add(s.trim());
					}
				}
			}
			concepts.add(ret);
		}
		return ret;
	}

	/**
	 * For test
	 * 
	 * @param args
	 * @throws DocumentException
	 */
	public static void main(String args[]) throws DocumentException {
		ConceptLoader loader = new ConceptLoader();
		loader.load("data/taxonomy.xml");
		HashSet<Concept> cs = loader.getConcepts();
		HashSet<String> ts = loader.getTerms();
		for (Concept c : cs)
			System.out.println(c);
		System.out.println("==================");
		for (String t : ts)
			System.out.println(t);
	}

}
