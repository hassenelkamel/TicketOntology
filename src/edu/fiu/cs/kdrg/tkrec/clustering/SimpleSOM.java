package edu.fiu.cs.kdrg.tkrec.clustering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.encog.Encog;
import org.encog.ml.data.MLData;

/*
 * Encog(tm) Java Examples v3.3 
 * http://www.heatonresearch.com/encog/ 
 * https://github.com/encog/encog-java-examples 
 * 
 * Copyright 2008-2014 Heaton Research, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *    
 * For more information on Heaton Research copyrights, licenses  
 * and trademarks visit: 
 * http://www.heatonresearch.com/copyright 
 */

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.som.SOM;
import org.encog.neural.som.training.basic.BasicTrainSOM;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodSingle;

import edu.fiu.cs.kdrg.tkrec.data.WordSimilarity;

/**
 * Implement a simple SOM using Encog.
 * 
 * 
 */
public class SimpleSOM {

	public static double[][] SOM_INPUT;
	public static String[] wordsArr;
	public static Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
	
	public static Set<String> loadWords(String FileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(FileName));
		String line = null;
		
		Set<String> words = new HashSet<String>();
		while ((line = reader.readLine()) != null) {
			words.add(line.toLowerCase());
		}
		reader.close();
		return words;
	}
	
	public static double[][] constructArray(String filename) {
		try {
			Set<String> words = loadWords(filename);
			wordsArr = words.toArray(new String[words.size()]);
			SOM_INPUT = new double[wordsArr.length][wordsArr.length];
			for(int i = 0; i < words.size()-1; i++) {
				for(int j = i+1; j < words.size(); j++) {
					SOM_INPUT[i][j] = WordSimilarity.combineSimilarity(wordsArr[i], wordsArr[j]);
				}
			}	
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SOM_INPUT;
	}

	public static void main(String args[]) {
		// construct som input
		String filename = "data2/tfidf.txt";
		System.out.println("starting to construct input array...");
		constructArray(filename);
		
		// create the training set
		MLDataSet training = new BasicMLDataSet(SOM_INPUT, null);

		// Create the neural network.
		SOM network = new SOM(SOM_INPUT.length, SOM_INPUT.length);
		network.reset();

		System.out.println("starting to training SOM network...");
		BasicTrainSOM train = new BasicTrainSOM(network, 0.4, training, new NeighborhoodSingle());

		int iteration = 0;

		for (iteration = 0; iteration <= 20; iteration++) {
			train.iteration();
			System.out.println("Iteration: " + iteration + ", Error:" + train.getError());
		}
		
		for(int wordIndex = 0; wordIndex < SOM_INPUT.length; wordIndex++) {
			MLData data = new BasicMLData(SOM_INPUT[wordIndex]);
			int key = network.classify(data);
			//statistics
			if(map.containsKey(key)) {
				map.get(key).add(wordIndex);
			} else{
				List list = new ArrayList();
				list.add(wordIndex);
				map.put(key, list);
			}
			
//			System.out.println("Pattern" + wordIndex + "winner: " + network.classify(data));
		}
		
		for(int key : map.keySet()) {
			List<Integer> wordsInd = map.get(key);
			System.out.print("Pattern " + key + " : ");
			for(int wordInd: wordsInd) {
				String word = wordsArr[wordInd];
				System.out.print(word + ", ");
			}
			System.out.println();
		}
		
		
		Encog.getInstance().shutdown();
	}
}