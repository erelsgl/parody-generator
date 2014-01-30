package Imitator.measure;

import java.util.HashMap;
import java.util.Map;

import Imitator.common.Commons;
import Imitator.common.FileUtils;
import Imitator.common.FilterUtils;

public class measurePOSStrategy implements MeasureStrategy 
{

    @Override public void handleSentence(Map<String, Map<String,Integer>> pos2Words, TaggedCorpusParserIfc taggedCorpusParser) {
		// Iterate over the words on the current sentence
    	StringBuffer sentence = new StringBuffer(); // for testing
		while(taggedCorpusParser.hasMoreWordsInSentence()){
		    String word = taggedCorpusParser.getNextWord();
		    String pos = taggedCorpusParser.getPOS();
		    String wordKey = Commons.getKey(word, pos);
		    
		    sentence.append(word).append(' ');

		    //if (wordKey.length()<3 && pos.contains("noun")) // prevent nouns that are too short. 
		   // 	continue;

		    // handle pos2Words map
		    addNewPosWord(MeasureProps.getCorpusName(), pos2Words, Commons.replaceSpecialCharacters(pos), wordKey);
		}
    }

    @Override public void writeToFiles(Map<String, Map<String,Integer>> pos2Words) {
		FilterUtils.removeUncommonWords(pos2Words, MeasureProps.getMinimalPOSFrequency());
		FilterUtils.removeWordsThatAreMoreCommonWithAnotherPos(pos2Words, 2);
		FileUtils.writePos2WordMapToFiles(MeasureProps.getCorpusDir(), pos2Words);
    }
    
    
    private void addNewPosWord(String corpusName, Map<String, Map<String,Integer>> pos2Words, String pos, String word) {
		Map<String, Integer> probMapOfPos = pos2Words.get(pos);
	
		if (probMapOfPos != null) {
		    Integer count = probMapOfPos.get(word);
		    probMapOfPos.put(word, count != null? count + 1: 1);
		} else { // probMap == null
			probMapOfPos = createInnerMap(word);
		    pos2Words.put(pos, probMapOfPos);
		}
    }
    
   
    private Map<String, Integer> createInnerMap(String word) 
    {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put(word, 1);
		return map;
    }

    @Override
    public String getMessage() 
    {
    	return "Measuring POS to words";
    }

	@Override
	public void destroy() 
	{
		
	}
}
