package edu.fiu.cs.kdrg.tkrec.nlp;

import java.util.ArrayList;
import java.util.List;

public class Preprocesser {

	public static List<String> removeAchorWords(List<String> instances){
		String achorReg = "(rcadescription)|(problemsolutiontext)";
		
		List<String> rtn = new ArrayList<String>();
		for(String instance : instances){
			String newInst = instance.replaceAll(achorReg, " ");
			rtn.add(newInst);
		}
		return rtn;
	}
	
	public static String removeAchorWords(String instance){
		String achorReg = "(rcadescription)|(problemsolutiontext)";
		String newInst = instance.replaceAll(achorReg, " ");
		return newInst;
	}
	
	public static void main(String[] args) {
		String str = "rcadescription:eigce. problemsolutiontext:987ue";
		System.out.print(Preprocesser.removeAchorWords(str));
	}
}
