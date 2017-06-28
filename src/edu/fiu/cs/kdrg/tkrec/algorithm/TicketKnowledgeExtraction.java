package edu.fiu.cs.kdrg.tkrec.algorithm;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.fiu.cs.kdrg.tkrec.util.IOUtil;

public class TicketKnowledgeExtraction {
	public static final String FILENAME = "data2/result_postprocess_classtagger";

	public static int WORDS_BEFORE = 8;
	public static int WORDS_AFTER = 8;

	public void extractProblem(Map<String, List<String>> conditionDict) {
		if(conditionDict.size() == 0) return;
		
		for (String key : conditionDict.keySet()) {
			StringBuffer sb = new StringBuffer();
			if (conditionDict.get(key) != null) {
				sb.append("Problems" + " - ");
				List<String> strs = conditionDict.get(key);
				String problem = key.split("-")[0];
				sb.append("{" + problem + " : ");

				for (int i = 0; i < strs.size() - 1; i++) {
					String entity = strs.get(i).split("-")[0];
					sb.append(entity + ", ");
				}

				sb.append(strs.get(strs.size() - 1).split("-")[0] + "}");
			}
			sb.append("\n");
			System.out.print(sb.toString());
		}
	}

	// extract activities
	public void extractActivities(Map<String, List<String>> activityDict) {
		if (activityDict.size() == 0) return;
		for (String key : activityDict.keySet()) {
			if (key.split("-")[1].equals("Activity")) {
				StringBuffer sb = new StringBuffer();
				if (activityDict.get(key) != null) {
					sb.append("Activities" + " - ");
					List<String> strs = activityDict.get(key);
					String problem = key.split("-")[0];
					sb.append("{" + problem + " : ");

					for (int i = 0; i < strs.size() - 1; i++) {
						String entity = strs.get(i).split("-")[0];
						sb.append(entity + ", ");
					}

					sb.append(strs.get(strs.size() - 1).split("-")[0] + "}");
				}
				sb.append("\n");
				System.out.print(sb.toString());
			}
		}
	}

	// extract action
	public void extractAction(Map<String, List<String>> actionDict) {
		if (actionDict.size() == 0) return;
		for (String key : actionDict.keySet()) {
			if (key.split("-")[1].equals("Action")) {
				StringBuffer sb = new StringBuffer();
				if (actionDict.get(key) != null) {
					sb.append("Actions" + " - ");
					List<String> strs = actionDict.get(key);
					String problem = key.split("-")[0];
					sb.append("{" + problem + " : ");

					for (int i = 0; i < strs.size() - 1; i++) {
						String entity = strs.get(i).split("-")[0];
						sb.append(entity + ", ");
					}

					sb.append(strs.get(strs.size() - 1).split("-")[0] + "}");
				}
				sb.append("\n");
				System.out.print(sb.toString());
			}
		}
	}
	
	public void extractSupportTeam(List<String> supportTeamList) {
		if (supportTeamList.size() == 0) return;
		StringBuffer sb = new StringBuffer();
		sb.append("Support Team" + " - {");
		int i;
		for(i=0; i<supportTeamList.size()-1; i++) {
			sb.append(supportTeamList.get(i) + ", ");
		}
		sb.append(supportTeamList.get(i) + "}" );
		sb.append("\n");
		System.out.print(sb.toString());
		
	}

	public void experiment(String inputfile) {
		String taggedSources = IOUtil.readString(inputfile, IOUtil.defaultCharset);
		String[] taggedArray = taggedSources.split("\n");
		for (String taggedSentence : taggedArray) {
			System.out.println("Tagged Ticket: " + taggedSentence);
			try {
				buildOntology(taggedSentence);
			} catch (Exception e) {
				IOUtil.writeString(e.toString(), "data2/result_log_extraction_error", IOUtil.defaultCharset);
			}
		}

	}

	public void buildOntology(String taggedSource) {
		Map<String, List<String>> conditionDict = new HashMap<String, List<String>>();
		Map<String, List<String>> activityDict = new HashMap<String, List<String>>();
		Map<String, List<String>> actionDict = new HashMap<String, List<String>>();
		List<String> supportTeamList = new ArrayList<String>();

		// (word)/(tag) tagged phrase form
		// Contains the Condition or Action tagged phrases.
		if (isValidTicket(taggedSource)) {

			System.out.println("========Extract Problem, Activity, Action, Support Team!========");
			String[] phrases = getPatternSource(taggedSource).split(" ");

			for (int i = 0; i < phrases.length; i++) {
				if (isConditionPhrase(phrases[i])) {
					String[] wordTag = getWordTag(phrases[i]);
					List<String> entitiesCondition = findNeighborhood(i, phrases, 4, 4);
					conditionDict.put(wordTag[0] + "-" + wordTag[1], entitiesCondition);

				}

				if (isActivityPhrase(phrases[i])) {
					String[] wordTag = getWordTag(phrases[i]);
					List<String> entitiesActivity = findNeighborhood(i, phrases, 2, 4);
					activityDict.put(wordTag[0] + "-" + wordTag[1], entitiesActivity);
				}

				if (isActionPhrase(phrases[i])) {
					String[] wordTag = getWordTag(phrases[i]);
					List<String> entitiesAction = findNeighborhood(i, phrases, 2, 4);
					actionDict.put(wordTag[0] + "-" + wordTag[1], entitiesAction);
				}
				
				if(isSupportTeamPhrase(phrases[i])) {
					String[] wordTag = getWordTag(phrases[i]);
					supportTeamList.add(wordTag[0]);
				}
			}
		} else {
			System.out.println("NO ACTION!");
		}

		extractProblem(conditionDict);
		extractActivities(activityDict);
		extractAction(actionDict);
		extractSupportTeam(supportTeamList);
	}

	// Set the neighborhood
	public List<String> findNeighborhood(int index, String[] phrases, int wordsBefore, int wordsAfter) {
		List<String> entities = new ArrayList<String>();
		int start = -1;
		int end = -1;

		if (index - wordsBefore < 0) {
			start = 0;
		} else {
			start = index - wordsBefore;
		}

		if (index + wordsAfter > phrases.length - 1) {
			end = phrases.length - 1;
		} else {
			end = index + wordsAfter;
		}

		for (int i = start; i <= end; i++) {
			if (isEntityPhrase(phrases[i])) {
				String[] strs = getWordTag(phrases[i]);
				if(!entities.contains(strs[0] + "-" + strs[1])) entities.add(strs[0] + "-" + strs[1]);
			}
		}
		return entities;
	}

	// Index: Word: 0; Tag: 1
	public String[] getWordTag(String phrase) {
		String[] strs = phrase.split("/");
		String[] result = new String[2];

		for (int i = 0; i < strs.length; i++) {
			Pattern m = Pattern.compile("\\(([^)]+)\\)");
			Matcher matcher = m.matcher(strs[i]);
			if (matcher.find()) {
				result[i] = matcher.group(1);
			}
		}
		return result;
	}

	/**
	 * get entity, problemCondition, activity and action, support team
	 * 
	 * @param taggedSource
	 * @return tagged phrase
	 */
	// change source (certificates test)/(Entity) =>
	// (certificates_test)/(Entity)
	public String getPatternSource(String taggedSource) {
		String pattern = "\\((\\w+\\s*)+\\)\\/\\(\\w+\\)";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(taggedSource);
		List<String> result = new ArrayList<String>();
		while (matcher.find()) {
			result.add(matcher.group().toString());
		}

		for (String s : result) {
			taggedSource = taggedSource.replace(s, s.replace(" ", "_"));
		}

		return taggedSource;
	}

	// Whether the ticket contains knowledge
	public boolean isValidTicket(String taggedSource) {
		return Pattern.compile("(ProblemCondition)+").matcher(taggedSource).find()
				|| Pattern.compile("(Action)+").matcher(taggedSource).find();
	}

	public boolean isActionPhrase(String taggedPhrase) {
		return Pattern.compile("(Action)+").matcher(taggedPhrase).find();
	}

	public boolean isActivityPhrase(String taggedPhrase) {
		return Pattern.compile("(Activity)+").matcher(taggedPhrase).find();
	}

	public boolean isConditionPhrase(String taggedPhrase) {
		return Pattern.compile("(ProblemCondition)+").matcher(taggedPhrase).find();
	}

	public boolean isEntityPhrase(String taggedPhrase) {
		return Pattern.compile("(Entity)+").matcher(taggedPhrase).find();
	}
	
	public boolean isSupportTeamPhrase(String taggedPhrase) {
		return Pattern.compile("(SupportTeam)+").matcher(taggedPhrase).find();
	}

//	public static void main(String[] args) {
//		TicketKnowledgeExtraction exp = new TicketKnowledgeExtraction();
//		exp.experiment();
//	}
}
