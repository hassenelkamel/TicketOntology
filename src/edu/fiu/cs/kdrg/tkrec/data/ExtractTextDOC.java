package edu.fiu.cs.kdrg.tkrec.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

public class ExtractTextDOC {
	
	public static final String WORDDOC_FILE_NAME = "data2/automaton/GEN_UNIX_GEN_R_C_ITM Restart V2.0.doc";
	
	
	public static void extractTable(String filename) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(filename);
			WordExtractor extractor = new WordExtractor(fis);
			System.out.println(extractor.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ExtractTextDOC.extractTable(WORDDOC_FILE_NAME);
	}

}
