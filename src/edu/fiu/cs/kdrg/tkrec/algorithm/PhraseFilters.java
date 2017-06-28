package edu.fiu.cs.kdrg.tkrec.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.fiu.cs.kdrg.tkrec.util.IOUtil;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Length Filter and PoSTag Filter
 * @author qingwang
 *
 */
public class PhraseFilters {
	// frequency > 10 phrases
	// public static String LEGAL_POS_REG_CORRECTED =
	// "JJ NN(S|PS)?|NN(S|PS)? NN(S|PS)?|JJ JJ NN(S|PS)?|JJ NN(S|PS)?
	// NN(S|PS)?|"
	// + "NN(S|PS)? JJ NN(S|PS)?|NN(S|PS)? VBN NN(S|PS)?|NN(S|PS)? NN(S|PS)?
	// NN(S|PS)?|NN(S|PS)? IN NN(S|PS)?";

	public static String LEGAL_POS_REG_ENTITY_CORRECTED = "\\w+_JJ \\w+_NN(S|PS)?|\\w+_NN(S|PS)? \\w+_NN(S|PS)?|\\w+_JJ \\w+_JJ \\w+_NN(S|PS)?|\\w+_JJ \\w+_NN(S|PS)? \\w+_NN(S|PS)?|"
			+ "\\w+_NN(S|PS)? \\w+_JJ \\w+_NN(S|PS)?|\\w+_NN(S|PS)? \\w+_VBN \\w+_NN(S|PS)?|\\w+_NN(S|PS)? \\w+_NN(S|PS)? \\w+_NN(S|PS)?|\\w+_NN(S|PS)? \\w+_IN \\w+_NN(S|PS)?";

	public static String LEGAL_POS_REG_ACTION_CORRECTED = "\\w+_VB(D|G|N|P|Z)?";

	public static Pattern LEGAL_POS_ENTITY_PATTERN = Pattern.compile(LEGAL_POS_REG_ENTITY_CORRECTED);
	public static Pattern LEGAL_POS_ACTION_PATTERN = Pattern.compile(LEGAL_POS_REG_ACTION_CORRECTED);

	MaxentTagger tagger = new MaxentTagger("tagger/english-left3words-distsim.tagger");

	/**
	 * Contain number
	 * 
	 * @param phrase
	 * @return
	 */
	public boolean isContainNumber(String phrase) {
//		return phrase.matches(".*\\d+.*");
		return phrase.matches("[0-9]\\s+");
	}

	/**
	 * length and number filter
	 * 
	 * @param length
	 * @return
	 */
	public List<String> lengthNumberFilter(String filename, int length) {
		List<String> result = new ArrayList<String>();

		String content = IOUtil.readString(filename);
		String[] phrases = content.split("\n");
		for (String phrase : phrases) {
			// 0:phrase 1:frequent
			String[] tokens = phrase.split(":");
			if (tokens[0].length() >= 20 && !isContainNumber(tokens[0])) {
				result.add(tokens[0]);
			}
		}
		return result;
	}

	/**
	 * POS tag Entity filter
	 * 
	 * @param resolutions
	 * @return
	 */
	public Map<String, String> posTagEntityFilter(List<String> phrases) {
		Map<String, String> entityPhraseTag = new HashMap<String, String>();
		StringBuffer wordSB = new StringBuffer();
		StringBuffer tagSB = new StringBuffer();
		for (String text : phrases) {
			text = text.replace("%", "");
			text = text.replace("#", " ");
			text = text.replaceAll("-", " ");
			text = text.trim();
			Matcher m = LEGAL_POS_ENTITY_PATTERN.matcher(tagger.tagString(text));
			while (m.find()) {
				String tagphrases = m.group();
				String[] segs = tagphrases.split(" ");
				for (String tagphrase : segs) {
					String[] tokens = tagphrase.split("_");
					wordSB.append(tokens[0] + " ");
					tagSB.append(tokens[1] + " ");
				}
				if (!entityPhraseTag.containsKey(wordSB.toString().trim())) {
					entityPhraseTag.put(wordSB.toString().trim(), tagSB.toString().trim());
				}
				// empty string buffer
				wordSB.setLength(0);
				tagSB.setLength(0);
			}
		}
		return entityPhraseTag;
	}

	/**
	 * pos tag action filter
	 * 
	 * @param phrases
	 * @return
	 */
	public Map<String, String> posTagActionFilter(List<String> phrases) {
		Map<String, String> actionPhraseTag = new HashMap<String, String>();
		StringBuffer wordSB = new StringBuffer();
		StringBuffer tagSB = new StringBuffer();
		for (String text : phrases) {
			text = text.replace("%", "");
			Matcher m = LEGAL_POS_ACTION_PATTERN.matcher(tagger.tagString(text));
			while (m.find()) {
				String tagphrases = m.group();
				String[] segs = tagphrases.split(" ");
				for (String tagphrase : segs) {
					String[] tokens = tagphrase.split("_");
					wordSB.append(tokens[0] + " ");
					tagSB.append(tokens[1] + " ");
				}
				if (!actionPhraseTag.containsKey(wordSB.toString().trim())) {
					actionPhraseTag.put(wordSB.toString().trim(), tagSB.toString().trim());
				}
				// empty string buffer
				wordSB.setLength(0);
				tagSB.setLength(0);
			}
		}
		return actionPhraseTag;
	}

	/**
	 * Write to file
	 * 
	 * @param phrases
	 * @param desFile
	 */
	public void write2File(Set<String> phrases, String desFile) {
		List<String> phrasesList = new ArrayList<String>(phrases);
		Collections.sort(phrasesList);
		StringBuffer sb = new StringBuffer();
		for (String phrase : phrasesList) {
			sb.append(phrase + "\n");
		}
		IOUtil.writeString(sb.toString(), desFile, IOUtil.defaultCharset);
	}

	public static void filters(String inputFileName, String outputEntityFileName, String outputActionFilename) {
		PhraseFilters exp = new PhraseFilters();
		System.out.println();
		System.out.println("PhraseFilters.java - starting length filter...");
		List<String> resOfLengthNubmerFilter = exp.lengthNumberFilter(inputFileName, 20);
		System.out.println(resOfLengthNubmerFilter.size());
		System.out.println("PhraseFilters.java - starting pos tag entity filter...");
		Map<String, String> resOfPosTagEntityFilter = exp.posTagEntityFilter(resOfLengthNubmerFilter);
		// write to file
		exp.write2File(resOfPosTagEntityFilter.keySet(), outputEntityFileName);

		System.out.println("PhraseFilters.java - starting pos tag action filter...");
		Map<String, String> resOfPosTagActionFilter = exp.posTagActionFilter(resOfLengthNubmerFilter);
		// write to file
		exp.write2File(resOfPosTagActionFilter.keySet(), outputActionFilename);
		System.out.println("PhraseFilters.java - finish...");
	}
}
