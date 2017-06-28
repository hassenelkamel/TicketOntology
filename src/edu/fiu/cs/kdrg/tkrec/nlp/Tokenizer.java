package edu.fiu.cs.kdrg.tkrec.nlp;

import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;

public class Tokenizer {

	static TokenizerFactory<Word> tf;

	public static List<Word> getTokens(String sentence) {
		if (tf == null) tf = PTBTokenizer.factory();
		List<Word> tokens_words = tf.getTokenizer(new StringReader(sentence)).tokenize();
		return tokens_words;
	}
}
