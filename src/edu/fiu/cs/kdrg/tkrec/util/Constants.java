package edu.fiu.cs.kdrg.tkrec.util;

public class Constants {

	public static String[] nonEventAttrNames = { "TICKETSTATUS", "TARGETIPMS", "RESOLUTIONCODE", "RESOLUTION",
			"LASTUPDATE", "IPCCUSTOMER", "FAILURECODE", "CAUSE", "ACTIONABLE", "CUSTOMERCODE", "TICKETNUMBER" };

	public static String[] nonEventImpAttrNames = { "TICKETSTATUS", "CAUSE", "RESOLUTION", "ACTIONABLE" };

	public static String[] eventImpAttrName = { "IDENTIFIER", "ALERTKEY", "SUMMARY", "SEVERITY", "NODE", "ALERTGROUP",
			"TALLY", "ORIGINALSEVERITY", "COMPONENT", "COMPONENTTYPE", "OSTYPE" };

	public static String[] selectedAttrs = { "IDENTIFIER", "ALERTKEY", "SUMMARY", "SEVERITY", "NODE", "ALERTGROUP",
			"TALLY", "ORIGINALSEVERITY", "COMPONENT", "COMPONENTTYPE", "OSTYPE", "LASTUPDATE", "FAILURECODE",
			"TICKETSTATUS", "CAUSE", "RESOLUTION", "ACTIONABLE" };

	public static String splitter = "[^a-zA-Z]+";

	public static double labelRedundantSimThreshold = 0.5;

	public static String labelName = "RESOLUTION";

	public static String descAttrName = "SUMMARY";

	public static String alertTypeAttrName = "ACTIONABLE";

	public static String timeAttr = "LASTUPDATE";

	public static String CAUSE_ATTR = "CAUSE";
	
	public static String[] comparedAttributes = {"IDENTIFIER", "ALERTKEY", "SUMMARY"};
	
	public static int k = 10;
	
	public static int topK = 5;

}