package Imitator.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Imitator.mainTasks.SentenceCreator;

public class FilterUtils {

    /**
     * The method gets map of POS templates and the number of its accuracies and filter non frequent 
     * and too much short templates.
     * For example, if 'minimalSentenceLength' is 3, all the templates with length of 1 or 2 words will be filtered.
     * 
     * @param originalPosTemplates
     * @param minimalSentenceLength
     * @param minimalFrequency - minimal number of accuracies
     * @return the filtered map
     */
    public static Map<String, Long> removeUncommonTemplates(Map<String, Long> originalPosTemplates, int minimalSentenceLength, int minimalFrequency) {

	Map<String, Long> newPosTemplates = new HashMap<String, Long>();

	for (String template : originalPosTemplates.keySet()) {
	    long numOfAccuracies = originalPosTemplates.get(template);
	    
	    // filter non-frequent templates
	    if(numOfAccuracies < minimalFrequency)
		continue;
	    
	    // filter too-short templates
	    List<String> templateWords = SentenceCreator.getListFromStrings(template);
	    if (templateWords.size() < minimalSentenceLength)
		continue;

	    // filter wrong templates
	    if(templateWords.get(0).equals(":") || templateWords.get(0).equals("."))
		continue;
	    
	    // else: 
	    newPosTemplates.put(template, numOfAccuracies);
	}

	return newPosTemplates;
    }
    
    /**
     * The method check if the given sentence is shorter than the given length
     * @param sentence
     * @param minimalSentenceLength
     * @return true if shorter, and false otherwise
     */
    public static boolean isTemplateTooShort(String sentence, int minimalSentenceLength) {
	List<String> templateWords = SentenceCreator.getListFromStrings(sentence);
	
	if (templateWords.size() < minimalSentenceLength)
	    return true;

	return false;
    }

    /**
     * Remove from 'map' words that appear less times then 'minimalNumberOfAppearences'
     * @param pos2Words
     * @param minimalAppearences
     */
    public static void removeUncommonWords(Map<String, Map<String,Integer>> map, int minimalNumberOfAppearences) {
    	for (String pos : map.keySet()) {
    		Map<String, Integer> probMap = map.get(pos);
    		for (Iterator<String> iterator = probMap.keySet().iterator(); iterator.hasNext();) {
   				String word = iterator.next();
   				if (probMap.get(word) < minimalNumberOfAppearences)
   					iterator.remove();
    		}
    	}
    }


    /**
     * Remove from 'map' words that appear less times in their current POS than in another POS, if the difference is with a factor of frequencyFactor or more.
     * @param pos2Words
     * @param frequencyFactor
     */
    public static void removeWordsThatAreMoreCommonWithAnotherPos(Map<String, Map<String,Integer>> map, int frequencyFactor) {
    	for (Map.Entry<String,Map<String, Integer>> checkedSubmapEntry: map.entrySet()) {
    		String checkedPos = checkedSubmapEntry.getKey();
    		Map<String,Integer> checkedSubmap = checkedSubmapEntry.getValue();
    		for (Iterator<String> iterator = checkedSubmap.keySet().iterator(); iterator.hasNext();) {
   				String checkedWord = iterator.next();
   				Integer checkedWordFrequency = checkedSubmap.get(checkedWord);
   				int checkedWordFactoredFrequency = checkedWordFrequency;
   				for (Map.Entry<String,Map<String, Integer>> otherSubmapEntry: map.entrySet()) {
   		    		Map<String,Integer> otherSubmap = otherSubmapEntry.getValue();
  					Integer otherFrequency = otherSubmap.get(checkedWord);
	   				if (otherFrequency!=null && otherFrequency>checkedWordFactoredFrequency) {
	   					iterator.remove();
	   					System.out.println("Removed "+checkedWord+" from "+checkedPos+"("+checkedWordFrequency+") because it appears in "+otherSubmapEntry.getKey()+"("+otherFrequency+")");
	   					break;
	   				}
   				}
    		}
    	}
    }
    
    /**
     * Remove from 'pos2Words' words that appear less times then 'minimalAppearences'
     * @param pos2Words
     * @param minimalAppearences
     *
    public static void removeUncommonSequences(Map<String, Map<String,Integer>> outerMap, int minimalNumberOfAppearences) {
		for (String pos : outerMap.keySet()) {
			Map<String, Map<String,Integer>> innerMap = outerMap.get(pos);
		    removeUncommonWords(innerMap, minimalNumberOfAppearences);
		}
    }*/

    /**
     * Remove words that for some reason were thought to appear in the first position of the sentence,
     * but it make no sense
     */
//    public static void removeWrongWords(String baseDir, String corpusName) {
//	    Map<String, Map> innerMap = FileUtils.readTripplesFile(baseDir, corpusName, Commons.SECOND_PRE_SENTENCE_SIGN);
//	    
//	    Map<String, Integer> probMap = innerMap.get(Commons.PRE_SENTENCE_SIGN);
//	    
//	    // the first word in the sentence should make sense for this position
//	    int sizeBefore = probMap.size();
//	    if (probMap != null) {
//		probMap.remove(".");
//		probMap.remove("!");
//		probMap.remove("BBB");
//		probMap.remove("AAA");
//		probMap.remove(",");
//		probMap.remove("+");
//		probMap.remove("~");
//	    }
//	    System.out.println("size of " + Commons.SECOND_PRE_SENTENCE_SIGN + ".txt reduced by: " + (probMap.size() - sizeBefore));
//	    
//	    String wordsDirPath = FileUtils.getTripplesDirectory(baseDir, corpusName);
//	    
//	    FileWriter writer = new FileWriter(directoryPath + "/Sequences.txt");
//	    FileUtils.writeSingleTrippleFile(writer, innerMap, wordsDirPath, Commons.SECOND_PRE_SENTENCE_SIGN);
//    }


}
