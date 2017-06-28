package edu.fiu.cs.kdrg.tkrec.algorithm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import javax.xml.stream.XMLStreamException;

import org.ahocorasick.trie.Trie;
import org.dom4j.DocumentException;

import au.com.bytecode.opencsv.CSVReader;
import edu.fiu.cs.kdrg.tkrec.nlp.StanfordNLPAnnotator;
import edu.fiu.cs.kdrg.tkrec.util.IOUtil;

public class ClassTagger {

	public static Set<String> entityDict = new HashSet<String>();
	public static Set<String> problemConditionDict = new HashSet<String>();
	public static Set<String> activityDict = new HashSet<String>();
	public static Set<String> actionDict = new HashSet<String>();
	//add for team extraction
	public static Set<String> supportTeamDict = new HashSet<String>();
	
	public static final String OMIT_WORD = "OMITWORD";
	public static final String TAGGER_PHRASE_FILENAME = "data2/taggedPhrases.csv";
	public static Map<String, Integer> statisticTaggedPhrasesMap = new HashMap<String, Integer>(); 

	/**
	 * Column A: Entity; Column B: Activity; Column C: Action; Column D: Problem Column E
	 * Condition
	 */
	public void loadDict() {
		try {
			CSVReader csvReader = new CSVReader(new FileReader(TAGGER_PHRASE_FILENAME));
			String[] header;
			header = csvReader.readNext();

			System.out.println("load dictionary...");
			for (String column : header) {
				System.out.print(column + " ");
			}
			System.out.println();
			// read column values to dict
			String[] values;
			while ((values = csvReader.readNext()) != null) {
				for (int col = 0; col < 5; col++) {
					if (!values[col].equals("")) {
						String value = values[col].trim().toLowerCase();
						if (col == 0) {
							entityDict.add(value);
							continue;
						}
						if (col == 1) {
							activityDict.add(value);
							continue;
						}
						if (col == 2) {
							actionDict.add(value);
							continue;
						}
						if (col == 3) {
							problemConditionDict.add(value);
							continue;
						}
						if (col == 4) {
							supportTeamDict.add(value);
						}
					}
				}
			}

			System.out.println("load dictionary finished...");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Loading stop words/omit words
	 * 
	 * @param stopWordFileName
	 * @return
	 * @throws IOException
	 */
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
	
	
	/**
	 * Tag support team in the ticket
	 * @param supportTeamSet
	 * @param sol
	 * @return
	 */
	public String getTaggedSupportTeam(Set<String> supportTeamSet, String sol) {
		List<String> problemConditionDict = new ArrayList<String>(supportTeamSet);
		Trie trie = new ACPhraseExtraction(problemConditionDict).buildTrie();
		Set<String> problemConditionPhrases = ACPhraseExtraction.containKeywords(trie, sol);
		if (null == problemConditionPhrases)
			throw new NullPointerException();
		for (String problemConditionPhrase : problemConditionPhrases) {
			sol = sol.replace(problemConditionPhrase, "(" + problemConditionPhrase + ")/(SupportTeam)");
		}
		return sol;
	}

	/**
	 * Tag problem condition
	 * 
	 * @param problemConditionSet
	 * @param sol
	 * @return
	 */
	public String getTaggedProblemCondition(Set<String> problemConditionSet, String sol) {
		List<String> problemConditionDict = new ArrayList<String>(problemConditionSet);
		Trie trie = new ACPhraseExtraction(problemConditionDict).buildTrie();
		Set<String> problemConditionPhrases = ACPhraseExtraction.containKeywords(trie, sol);
		if (null == problemConditionPhrases)
			throw new NullPointerException();
		for (String problemConditionPhrase : problemConditionPhrases) {
			sol = sol.replace(problemConditionPhrase, "(" + problemConditionPhrase + ")/(ProblemCondition)");
		}
		return sol;
	}
	
	
	/**
	 * Tag activity 
	 * 
	 * @param problemConditionSet
	 * @param sol
	 * @return
	 */
	public String getTaggedPhrasesActivity(Set<String> activitySet, String sol) {
		List<String> activityDict = new ArrayList<String>(activitySet);
		Trie trie = new ACPhraseExtraction(activityDict).buildTrie();
		Set<String> activityPhrases = ACPhraseExtraction.containKeywords(trie, sol);
		if (null == activityPhrases)
			throw new NullPointerException();
		for (String activityPhrase : activityPhrases) {
			sol = sol.replace(activityPhrase, "(" + activityPhrase + ")/(Activity)");
		}
		return sol;
	}

	/**
	 * find longest string match
	 * 
	 * @param entityDict
	 * @param sol
	 * @return
	 */
	public String getTaggedPhrasesEntity(Set<String> entityDictSet, String sol) {
		List<String> entityDict = new ArrayList<String>(entityDictSet);
		Trie trie = new ACPhraseExtraction(entityDict).buildTrie();
		Set<String> entityPhrasesSet = ACPhraseExtraction.containKeywords(trie, sol);
		List<String> phrases = new ArrayList<String>();
		//convert set to array
		String[] entityPhrases = entityPhrasesSet.toArray(new String[entityPhrasesSet.size()]);
		Arrays.sort(entityPhrases, new StringKorterSortComparatorUitl());
		
		if (entityPhrases == null)
			throw new NullPointerException();
		for (String entityPhrase : entityPhrases) {
			phrases.add(entityPhrase);
			sol = sol.replaceAll(entityPhrase, "(" + entityPhrase + ")" + "/(Entity)");
		}
		// (the same (job)/(Entity))/(Entity) -> (the same job)/(Entity)
		String first = "";
		String second = "";
		for (int i = 0; i < phrases.size() - 1; i++) {
			for (int j = i + 1; j < phrases.size(); j++) {
				first = phrases.get(i);
				second = phrases.get(j);
				if (first.contains(second)) {
					int start = first.indexOf(second);
					int end = first.indexOf(second) + second.length();
					String prefix = first.substring(0, start);
					String result = prefix + "(" + second + ")/(Entity)";
					sol = sol.replace(result, prefix + second);
				}
			}
		}
		return sol;
	}

	public String getTaggedSol(Trie trie, String sol) {
		StanfordNLPAnnotator nlpAnnotator = new StanfordNLPAnnotator().getInstance();
		sol = sol.toLowerCase();
		try {
			Set<String> omitWords = loadStopWords("data/english.stop");
			Set<String> keywords = ACPhraseExtraction.containKeywords(trie, sol);
			Set<String> usedWords = new HashSet<String>();
			// tagged longest match entity phrase
			sol = getTaggedSupportTeam(supportTeamDict, sol);
			sol = getTaggedPhrasesEntity(entityDict, sol);
			sol = getTaggedProblemCondition(problemConditionDict, sol);
			// ["will be", "and gbic replacement"]
			for (String keyword : keywords) {
				String[] words = keyword.split(" ");
				for (String word : words) {
					String wordLemmas = nlpAnnotator.extractLemmas(word).get(0);
					// only new word can be tagged.
					if (usedWords.contains(wordLemmas))
						continue;
					else
						usedWords.add(wordLemmas);
					if (omitWords.contains(word) || omitWords.contains(wordLemmas)) {
						continue;
					}
					if (actionDict.contains(word) || actionDict.contains(wordLemmas)) {
						sol = sol.replaceAll(word, "(" + word + ")" + "/(Action)");
						continue;
					}
					if (activityDict.contains(word) || activityDict.contains(wordLemmas)) {
						sol = sol.replaceAll(word, "(" + word + ")" + "/(Activity)");
						continue;
					}
					// if (problemConditionDict.contains(word) ||
					// problemConditionDict.contains(wordLemmas)) {
					// {
					// sol = sol.replaceAll(word, "(" + word + ")" +
					// "/(ProblemCondition)");
					// }
					// continue;
					// }
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sol;
	}

	/**
	 * statistic the tagged phrases
	 * @param sol
	 */
	public static void statTaggedPhrases(String sol) {
		if(!statisticTaggedPhrasesMap.containsKey(sol)) {
			statisticTaggedPhrasesMap.put(sol, 1);
		} else {
			statisticTaggedPhrasesMap.put(sol, statisticTaggedPhrasesMap.get(sol) + 1);
		}
	}
	
	/**
	 * Output the file
	 * @param sortedStatMap
	 * @param outputFile
	 */
	public static void outputMap2File(Map<String, Integer> sortedStatMap, String outputFile) {
		StringBuffer sb = new StringBuffer();
		for(String taggedSentence: sortedStatMap.keySet()) {
			sb.append(taggedSentence);
			sb.append("\n");
		}
		IOUtil.writeString(sb.toString(), outputFile, IOUtil.defaultCharset);
	}
	
	public static void experiment(String inputFileName, String outputStatFileName) throws IOException, XMLStreamException, DocumentException {
		ClassTagger classTagger = new ClassTagger();
		System.out.println("start loading specific domain dictionary...");
		classTagger.loadDict();
		List<String> keywordDict = RepeatedPatternExtraction.buildDict(inputFileName);
		Trie trie = new ACPhraseExtraction(keywordDict).buildTrie();
		List<String> eSol = RepeatedPatternExtraction.eSol;
		List<String> taggedSol = new ArrayList<String>();
		System.out.println("start writing to file...");

		StringBuffer sb = new StringBuffer();
		for (String sol : eSol) {
			if (sol.matches("\\d+"))
				continue;
			sol = classTagger.getTaggedSol(trie, sol);

			// write the tickets contains ProblemCondition and Entity Tags
			if (sol.contains("ProblemCondition")){
				statTaggedPhrases(sol);
				sb.append(sol + "\n");
			}
			else continue;
		}

		Map<String, Integer> sortedResultMap = MapValueSortComparatorUtil.outputSortedMap(statisticTaggedPhrasesMap);
		
//		outputMap2File(sortedResultMap, "data2/result_statistics_classtagger");
		outputMap2File(sortedResultMap, outputStatFileName);
//		IOUtil.writeString(sb.toString(), "data2/result_classtagger", IOUtil.defaultCharset);
		System.out.println("finish writing to file...");
	}
}
