package Imitator.measure;

import java.util.HashMap;
import java.util.Map;

import Imitator.common.*;

/**
 * Count the number of pairs of adjacent words.
 * @author Erel Segal
 * @deprecated not used
 */
public class MeasureBigramsStrategy implements MeasureStrategy {
	
	protected Map<String,Integer> bigramCounts = new HashMap<String,Integer>();
	
	@Override public void writeToFiles(Map<String, Map<String,Integer>> dummy) {
		//FilterUtils.removeWrongWords(Props.getBaseDir(), Props.getCorpusName()); /* 19/5/09 - wasn't checked! */
		//FilterUtils.removeUncommonSequences(bigramCounts, MeasureProps.getMinimalSequenceFrequency());
		FileUtils.writeBigramCountsToFile(bigramCounts, MeasureProps.getCorpusDir());
	}

	@Override public void destroy()  {}
    
    @Override public void handleSentence(Map<String, Map<String,Integer>> outerMap, TaggedCorpusParserIfc taggedCorpusParser) {
		String previousWord = Commons.PRE_SENTENCE_SIGN;

		// Iterate over the words on the current sentence
		while (taggedCorpusParser.hasMoreWordsInSentence()) {
		    // get the actual word
		    String word = taggedCorpusParser.getNextWord().toLowerCase();
		    String bigram = previousWord+" "+word;
		    if (bigramCounts.get(bigram)==null)
		    	bigramCounts.put(bigram,1);
		    else
		    	bigramCounts.put(bigram, bigramCounts.get(bigram)+1);
		    previousWord = word;
		}
    }

    @Override public String getMessage() {
		return "Measuring 2-gram sequences";
    }

}
