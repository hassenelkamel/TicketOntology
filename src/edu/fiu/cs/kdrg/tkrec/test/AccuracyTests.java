package edu.fiu.cs.kdrg.tkrec.test;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import au.com.bytecode.opencsv.CSVWriter;
import edu.fiu.cs.kdrg.tkrec.Recommender;
import edu.fiu.cs.kdrg.tkrec.RecommenderFactory;
import edu.fiu.cs.kdrg.tkrec.core.Event;
import edu.fiu.cs.kdrg.tkrec.core.TextTermSimilarity;
import edu.fiu.cs.kdrg.tkrec.util.CountMap;
import edu.fiu.cs.kdrg.tkrec.util.Pair;
import edu.fiu.cs.kdrg.tkrec.util.XMLEventInputStreamReader;
import edu.fiu.cs.kdrg.tkrec.util.XMLEventLoader;
import edu.fiu.cs.kdrg.tkrec.util.XMLEventWriter;


public class AccuracyTests {
	
	static int numThreads = 5;
	
	static List<Event> trainEvents;
	static List<Event> testEvents;
	static String labelAttrName;
	static HashMap<String, Event> labelEventMap;
	
	public static List<Event> sortEvents(List<Event> eventList, final String timestampAttrName) {
		Collections.sort(eventList, new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				// TODO Auto-generated method stub
				long t1 = Long.parseLong(o1.getValue(timestampAttrName));
				long t2 = Long.parseLong(o2.getValue(timestampAttrName));
				if (t1 > t2) {
					return 1;
				}
				else if (t2 > t1) {
					return -1;
				}
				else {
					return 0;
				}
			}			
		});
		return eventList;
	}
	
	
	static class TestTask implements Callable<String> {
		String algorithmDesc;
		String labelAttrName;
		int k;
		int numCandidate;
		double realAlertImportance;
		String errorFileName;
		
		public TestTask(String algorithmDesc,String labelAttrName, int k, int numCandidate, double realAlertImportance, String errorFileName) {
			this.algorithmDesc = algorithmDesc;
			this.labelAttrName = labelAttrName;
			this.k = k;
			this.numCandidate = numCandidate;
			this.realAlertImportance = realAlertImportance;
			this.errorFileName = errorFileName;
		}

		@Override
		public String call() throws Exception {
			// TODO Auto-generated method stub
			Recommender rec = RecommenderFactory.create(algorithmDesc, trainEvents, k);
			int numHits = 0;
			TextTermSimilarity labelSimFunc = new TextTermSimilarity();
			
			List<Event> errorEvents = new ArrayList<Event>();
			
			int numRealHits = 0;
			int numRealTickets = 0;
			double overallCost = 0;
			for (int testEventIndex=0; testEventIndex < testEvents.size(); testEventIndex++) {
				Event event = testEvents.get(testEventIndex);
				List<String> results = rec.recommend(event, numCandidate);
				//add by qwang
				System.out.println(results);
				
				String trueLabel = event.getValue(labelAttrName);
				boolean isFalseTicket = event.getValue("IS_FALSE").equals("1");
				boolean hasHit = false;
				double cost = 0;
				for (int recIndex=0; recIndex<results.size(); recIndex++) {
					String recLabel = results.get(recIndex);
					if (hasHit == false) {
						double sim = labelSimFunc.sim(recLabel, trueLabel);
						if (sim >= 0.5) {
							numHits++;
							if (!isFalseTicket) {
								numRealHits++;
							}
							hasHit = true;
						}
					}
					Event recEvent = labelEventMap.get(recLabel);
					if (isFalseTicket) {
						if (recEvent.getValue("IS_FALSE").equals("0")) {
							cost += 1.0 - realAlertImportance;
						}
					}
					else {
						if (recEvent.getValue("IS_FALSE").equals("1")) {
							cost += realAlertImportance;
						}
					}
				}
				
				if (hasHit == false) {
					if (!isFalseTicket) {
						StringBuffer buf = new StringBuffer();
						for (String recLabel: results) {
							buf.append(recLabel);
							buf.append(" ----------------- ");
						}
						// System.out.println("truth: "+trueLabel+" , recommended: " + buf.toString());
						event.setValue("recommend", buf.toString());
						errorEvents.add(event);
					}
				}
				if (!isFalseTicket) {
					numRealTickets++;
				}
				
				rec.addLabeledEvent(event);
				overallCost += cost;
			}
			
			double absAccuracy =  ((double)numHits)/testEvents.size();
			double accuracy = (numRealHits*realAlertImportance+(numHits-numRealHits)*(1-realAlertImportance))
					/(numRealTickets*realAlertImportance+(testEvents.size()-numRealTickets)*(1-realAlertImportance));
			double real_accuracy = ((double)numRealHits)/numRealTickets;
			double false_accuracy = ((double)(numHits-numRealHits))/(testEvents.size()-numRealTickets);
			
			double overallScore = absAccuracy*testEvents.size() / overallCost;
			
			String recName = rec.getIdentifier();
			String result = recName+": absAccuracy = "+numHits+"/"+testEvents.size()+"="+absAccuracy
					+" , accuracy = "+accuracy
					+" , real_acc = "+numRealHits+"/"+numRealTickets+"="+real_accuracy
					+" , false_acc = "+(numHits-numRealHits)+"/"+(testEvents.size()-numRealTickets)+"="+false_accuracy
					+" , overallScore = "+overallScore
					+ " , cost="+overallCost;
			
			
			XMLEventWriter.write(errorEvents, recName+"_"+errorFileName);
			
			return result;
		}
		
	}
	
	private static void test(String eventFileName, String labelAttrName, int dataSize, int k,
			int numCandidate, double realAlertImportance, String errorFileName) throws Exception {
		System.out.println("k = "+k+" , numCandidate = "+numCandidate);
		double testingRatio = 0.1;
		List<Event> events = XMLEventLoader.loadEvents(eventFileName, dataSize);
		List<Event> sortedEvents = sortEvents(events, "EventTimestamp");
		int numTrainEvents = (int)(sortedEvents.size()*(1-testingRatio));
		trainEvents = new ArrayList<Event>(numTrainEvents);
		testEvents = new ArrayList<Event>(sortedEvents.size() - numTrainEvents);
		labelEventMap = new HashMap<String, Event>();
		for (int i=0; i<sortedEvents.size(); i++) {
			Event event = sortedEvents.get(i);
			if (i < numTrainEvents) {
				trainEvents.add(event);
			}
			else {
				testEvents.add(event);
			}
			labelEventMap.put(event.getValue(labelAttrName), event);			
		}
		
		
		String[] algorithmDescs = new String[] {
				"KNN",
				"WeightedKNN",
				"DivideKNN:"+realAlertImportance,
				"FusionKNN:"+realAlertImportance,
		};
		
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		List<Future<String>> futures = new ArrayList<Future<String>>();
		System.out.println("Created "+numThreads+" threahds.");
		for (String algorithmDesc : algorithmDescs) {
			futures.add(executor.submit(new TestTask(algorithmDesc, labelAttrName, k, numCandidate, realAlertImportance, errorFileName)));
		}
		for (Future<String> future: futures) {
			String result = future.get();
			System.out.println(result);
		}
		executor.shutdown();
	}
	
	private static void statEventsInfo(String fileName, String timestampAttrName, String outputFileName, int topKRepeated) throws Exception{		
		String labelAttrName = RecommenderFactory.labelAttrName;
		XMLEventInputStreamReader reader = new XMLEventInputStreamReader(fileName);
		CountMap<String> instLabels = new CountMap<String>();
		int numEvents = 0;
		Event event = null;
		long minTimestamp = -1;
		long maxTimestamp = -1;
		String minTimeStr = null;
		String maxTimeStr = null;
		Set<String> hostNameSet = new HashSet<String>();
		while((event = reader.readNext()) != null) {
			instLabels.add(event.getValue(labelAttrName));
			long timestamp = Long.parseLong(event.getValue(timestampAttrName));
			if (minTimestamp < 0 || minTimestamp > timestamp) {
				minTimestamp = timestamp;
				minTimeStr = event.getValue("OpenDateTime");
			}
			if (maxTimestamp < 0 || maxTimestamp < timestamp) {
				maxTimestamp = timestamp;
				maxTimeStr = event.getValue("OpenDateTime");
			}
			hostNameSet.add(event.getValue("origin"));
			
			numEvents++;
		}
		reader.close();
		String[] labels = new String[instLabels.size()];
		instLabels.keySet().toArray(labels);
		
		CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFileName));
		
		// Write the number of servers
		csvWriter.writeNext(new String[]{"num_servers", ""+hostNameSet.size()});
		
		// Write the start and end timestamps
		csvWriter.writeNext(new String[]{minTimeStr, maxTimeStr});
		
		// Write the number of events and distinct number of resolutions
		csvWriter.writeNext(new String[]{numEvents+"", instLabels.size()+""});
		
		// Find top repeated labels
		List<Pair<String, Integer>> labelCountPairs = instLabels.sortByCount(false);
		int numTopPairs = Math.min(topKRepeated, labelCountPairs.size());
		for (int i=0; i<numTopPairs; i++) {
			Pair<String, Integer> pair = labelCountPairs.get(i);
			String[] outLine = new String[]{
					pair.getFirst(),
					pair.getSecond()+"",
			};
			csvWriter.writeNext(outLine);
			System.out.println(Arrays.toString(outLine));
		}
		
		csvWriter.close();
	}
	
	private static void computeSimDistribution(String fileName, int dataSize, String outputFileName) throws Exception{	
		String labelAttrName = RecommenderFactory.labelAttrName;
		XMLEventInputStreamReader reader = new XMLEventInputStreamReader(fileName);
		List<String> resolutionList = new ArrayList<String>();
		Event event = null;		
		while((event = reader.readNext()) != null) {
			resolutionList.add(event.getValue(labelAttrName));
			if (resolutionList.size() >= dataSize) {
				break;
			}
		}
		reader.close();
		
		CountMap<Integer> similarityCounts= new CountMap<Integer>();
		TextTermSimilarity textSim = new TextTermSimilarity();
		int numUnit = 10;
		double scale_unit = 1.0 / numUnit;
		for (int i=0; i<resolutionList.size(); i++) {
			String r1 = resolutionList.get(i);
			for (int j=i+1; j<resolutionList.size(); j++) {
				String r2 = resolutionList.get(j);
				double sim = textSim.sim(r1, r2);
				int normSim = (int)(sim / scale_unit);
				if (normSim == numUnit) {
					normSim--;
				}
				similarityCounts.add(normSim);
			}
		}
		
		int totalCount = resolutionList.size() * (resolutionList.size()-1) / 2;
		
		CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFileName));
		// Write the number of servers
		for (int normSim : similarityCounts.keySet()) {
			double sim = normSim * scale_unit;
			int count = similarityCounts.getCount(normSim);
			double prob = ((double)count) / totalCount;
			csvWriter.writeNext(new String[]{""+sim, ""+prob, ""+count});
		}
		csvWriter.close();
		
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// String fileName = "data/Amp_Labeled_Events_Oct_Dec_10.xml";			
			// test(fileName, "SOLUTION_DESC", 5000, 10, 5, 0.9, "errorEvents.xml");
			// test(fileName, "SOLUTION_DESC", 15000, 10, 3, 0.9, "errorEvents.xml");
			// test(fileName, "SOLUTION_DESC", 15000, 10, 1, 0.9, "errorEvents.xml");
			// statEventsInfo("data/Amp_Labeled_Events_Oct_Dec_10.xml", "EventTimestamp", "data/amp_repeated_resolutions.csv", 100);
			// statEventsInfo("testfiles/rec/azoac_events_labeled_Apr11.xml", "date_reception", "data/azaoc_repeated_resolutions.csv");
			// statEventsInfo("testfiles/rec/disney_events_labeled_Apr11.xml", "date_reception", "data/disney_repeated_resolutions.csv");
			// computeSimDistribution("data/Amp_Labeled_Events_Oct_Dec_10.xml", 10000, "data/amp_resolution_sim.csv");
			// computeSimDistribution("data/azoac_events_labeled_Apr11.xml", 10000, "data/azaoc_resolution_sim.csv");
			// computeSimDistribution("data/disney_events_labeled_Apr11.xml", 10000, "data/disney_resolution_sim.csv");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
