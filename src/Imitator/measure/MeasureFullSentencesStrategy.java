package Imitator.measure;

import java.util.*;

import Imitator.common.FileUtils;

/**
 * Strategy to create the file fullSentences.txt: 
 * For each i, line 2*i is a sentence, and line 2*i+1 is the pos sequence of the sentence. 
 * @author erelsgl
 * @date 20/07/2011
 */
public class MeasureFullSentencesStrategy implements MeasureStrategy {
	List<String> sentences = new ArrayList<String>();

    @Override public void handleSentence(Map<String, Map<String,Integer>> pos2Words, TaggedCorpusParserIfc taggedCorpusParser) {
    	StringBuffer sentenceWords = new StringBuffer();
    	StringBuffer sentencePoses = new StringBuffer();
		// Iterate over the words on the current sentence
		while(taggedCorpusParser.hasMoreWordsInSentence()){
		    String word = taggedCorpusParser.getNextWord();
		    String pos = taggedCorpusParser.getPOS();
		    sentenceWords.append(word).append(' ');
		    sentencePoses.append(pos).append(' ');
		}
		sentences.add(sentenceWords.toString());
		sentences.add(sentencePoses.toString());
    }

    @Override public void writeToFiles(Map<String, Map<String,Integer>> words) {
		FileUtils.writeFullSentencesFile(sentences, MeasureProps.getCorpusDir());
    }

    @Override public String getMessage() {
    	return "Creating full sentences";
    }

	@Override public void destroy() {
	}
}
