package Imitator.measure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Imitator.common.Commons;
import Imitator.common.FileUtils;
import Imitator.mainTasks.MeasureFrequencies;
import Imitator.suffixArray.*;

public class SuffixArrayStrategy implements MeasureStrategy
{

    protected WordList _corpusAsArray;
    
    public SuffixArrayStrategy() {
	    _corpusAsArray = new WordListWithChunks(1); // single-word chunk is faster. Memory is unlimited.
	    _corpusAsArray.add(Commons.PRE_SENTENCE_SIGN);
    }

    @Override public String getMessage() 
    {
    	return "Creating suffix arrays";
    }

    @Override
    public void handleSentence(Map<String, Map<String,Integer>> word2Words, TaggedCorpusParserIfc taggedCorpusParser) 
    {
		List<String> sentenceWords = new ArrayList<String>();
		
		// Iterate over the words on the current sentence
		while(taggedCorpusParser.hasMoreWordsInSentence()){
		    String word = taggedCorpusParser.getNextWord();
	
		    String pos = taggedCorpusParser.getPOS();
	
		    sentenceWords.add(Commons.getKey(word, pos));
		}
		
		_corpusAsArray.addAll(sentenceWords);
		_corpusAsArray.add(Commons.PRE_SENTENCE_SIGN);
		
    }

    @Override public void writeToFiles(Map<String, Map<String,Integer>> words) {
		System.out.println(MeasureFrequencies._timer.getFormattedTimeFromBegin());
		System.out.println(" ## Creating suffix array");
	
		// create the suffix array
		WordsSuffixArray suffixArray = new WordsSuffixArray(_corpusAsArray);
		
		MeasureFrequencies.logTime();
		
		System.out.println(" ## writing suffixes into files");
		// write the suffix array files 
		suffixArray.writeFileOfSuffixes(FileUtils.getSuffixArrayFile(MeasureProps.getCorpusDir()));
		
		
		MeasureFrequencies.logTime();
		System.out.println(" ## writing words into files");
		// write the corpus words array files 
		SuffixArraysUtils.writeFileOfCorpusWords(suffixArray.getCorpusWords(), 
				FileUtils.getCorpusWordsFile(MeasureProps.getCorpusDir()));
		
		MeasureFrequencies.logTime();
	
    }

	@Override public void destroy() {
		_corpusAsArray.clear();
		_corpusAsArray = null;
		System.gc();
	}
}
