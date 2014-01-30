package Imitator.measure;

import java.util.*;

import Imitator.common.Commons;
import Imitator.common.FileUtils;
import Imitator.mainTasks.MeasureFrequencies;
import Imitator.suffixArray.SuffixArraysUtils;
import Imitator.suffixArray.WordsSuffixArray;

public class ReverseSuffixArrayStrategy extends SuffixArrayStrategy{

    @Override public String getMessage() {
    	return "Creating reverse suffix arrays";
    }
    
    @Override public void handleSentence(Map<String, Map<String,Integer>> word2Words, TaggedCorpusParserIfc taggedCorpusParser) 
    {
		List<String> sentenceWords = new ArrayList<String>();
		
		// Iterate over the words on the current sentence
		while(taggedCorpusParser.hasMoreWordsInSentence()){
		    
		    String word = taggedCorpusParser.getNextWord();
	
		    String pos = taggedCorpusParser.getPOS();
	
		    sentenceWords.add(Commons.getKey(word, pos));
		}

		//add words in the reverse order
		int len = sentenceWords.size();
		for (int i = len-1; i >= 0; i--)
		{
			_corpusAsArray.add(sentenceWords.get(i));
		}
		_corpusAsArray.add(Commons.PRE_SENTENCE_SIGN);
		
    }
    
    @Override public void writeToFiles(Map<String, Map<String,Integer>> words) 
    {
		System.out.println(MeasureFrequencies._timer.getFormattedTimeFromBegin());
		System.out.println(" ## Creating reverse suffix array");
		
		WordsSuffixArray reverseSuffixArray = new WordsSuffixArray(_corpusAsArray);
		
		System.out.println(" ## writing reverse suffixes into files");
		// write the suffix array files 
		reverseSuffixArray.writeFileOfSuffixes(FileUtils.getReverseSuffixArrayFile(
				MeasureProps.getCorpusDir()));
		
		System.out.println(" ## writing words into files");
		// write the reverse corpus words array files 
		SuffixArraysUtils.writeFileOfCorpusWords(reverseSuffixArray.getCorpusWords(), 
				FileUtils.getReverseCorpusWordsFile(MeasureProps.getCorpusDir()));
    }


}
