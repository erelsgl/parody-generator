package Imitator.sentenceCreator;

import java.io.File;
import java.io.IOException;
import java.util.*;

import dataStructures.ImitatorLogger;

import Imitator.common.Commons;
import Imitator.common.FileUtils;
import Imitator.mainTasks.SentenceCreator;
import Imitator.suffixArray.Range;
import Imitator.suffixArray.SuffixArraysUtils;
import Imitator.suffixArray.WordList;
import Imitator.suffixArray.WordsSuffixArray;

/**
 * The implementation of the n-gram calculation using suffix array
 *
 */
public class NGramsWithSuffixArrayImpl implements NGramsIfc {
    private WordsSuffixArray _mainSuffixArray;
    private WordsSuffixArray _mainReverseSuffixArray;
    private WordsSuffixArray _suppSuffixArray;
    private WordsSuffixArray _suppReverseSuffixArray;
    
    private int numberOfRelevantPreviosWordsInSentence;
	private static String[] slashes = new String[] {"/", "//", "///", "////"};
   
    private String corpusDir, supplementaryCorpusName; 
    public NGramsWithSuffixArrayImpl(String corpusDir, String supplementaryCorpusName) throws IOException {
    	this.corpusDir = corpusDir;
    	this.supplementaryCorpusName = supplementaryCorpusName;
    	readStatistics(null);
    	
    	//test(); System.exit(0);
    }


    private static String memory() {
    	final int unit = 1000000; // MB
    	long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    	long availableMemory = Runtime.getRuntime().maxMemory() - usedMemory;
    	return "Memory: "+" used="+usedMemory/unit+" available="+availableMemory/unit+" free="+(Runtime.getRuntime().freeMemory()/unit)+" total="+(Runtime.getRuntime().totalMemory()/unit)+" max="+(Runtime.getRuntime().maxMemory()/unit);
    }
  
    

    public static float averageWordLength(WordList wordList) {
		float sum=0;
		for (int i=0; i<wordList.size(); ++i) 
			sum += wordList.get(i).length();
		return sum/wordList.size();
	}
   
    @Override public void readStatistics(String unusedArgument) throws IOException  {
    	System.err.println(memory());
		System.err.println(" ## Reading suffix array files of main corpus ...");
		WordList mainCorpusWords = SuffixArraysUtils.readFileOfCorpusWords(FileUtils.getCorpusWordsFile(corpusDir));
		System.err.println("   mainCorpusWords.size="+mainCorpusWords.size()+". Average word length="+averageWordLength(mainCorpusWords)+" "+memory());
		int[] mainSuffixes = SuffixArraysUtils.readFileOfSuffixes(FileUtils.getSuffixArrayFile(corpusDir));
		
		System.gc();		System.err.println("   after gc: "+memory());
		System.err.println("   mainSuffixes.length="+mainSuffixes.length+". "+memory());
		_mainSuffixArray = new WordsSuffixArray(mainCorpusWords, mainSuffixes);
		System.err.println("   _mainSuffixArray.length="+_mainSuffixArray.getLength()+". "+memory());
		System.gc();		System.err.println("   after gc: "+memory());
	
		System.err.println(" ## Reading reverse suffix array files...");
		mainCorpusWords = SuffixArraysUtils.readFileOfCorpusWords(FileUtils.getReverseCorpusWordsFile(corpusDir));
		System.err.println("   mainCorpusWords.size="+mainCorpusWords.size()+". "+memory());
		int[] reverseSuffixes = SuffixArraysUtils.readFileOfSuffixes(FileUtils.getReverseSuffixArrayFile(corpusDir));
		System.err.println("   reverseSuffixes.length="+reverseSuffixes.length+". "+memory());
		_mainReverseSuffixArray = new WordsSuffixArray(mainCorpusWords, reverseSuffixes);
		System.err.println("   _mainReverseSuffixArray.length="+_mainReverseSuffixArray.getLength()+". "+memory());
		System.gc();
		System.err.println("   after gc: "+memory());
    }
    
    // Sample element: noun => {house => 2, road => 4, ...}
    private Map<String, Map<String, Long>> mapPosToMapWordToFrequency = new HashMap<String, Map<String, Long>>();

    
    
    @Override public String generateSentence(List<String> posTemplate, List<String> realSentence, boolean isReverse)  {
    	if (posTemplate.size()!=realSentence.size())
    		throw new IllegalArgumentException("POS template must be the same size as real sentence!");
		// the result will be set here
    	List<String> generatedSentenceWords = new ArrayList<String>();
    	generatedSentenceWords.add(0, Commons.PRE_SENTENCE_SIGN);
    	List<String> generatedSentenceAndExplanationWords = new ArrayList<String>();
		//StringBuffer generatedSentence = new StringBuffer("");
		//StringBuffer generatedSentenceAndExplanation = new StringBuffer("");

		int numberOfWordsReplaced = 0;
	
		// iterating the template and choose random word for each POS
		int index = 0;
		boolean copyFirstPart;
		if (realSentence.size() == 0) {
			ImitatorLogger.getInstance().addToLastSentence("------- Empty original sentence!");
			return null;
		}

		for (String currPOS : posTemplate) {
			String originalWord = realSentence.get(index);
		    String chosenWord;
		    String chosenWordKey;
		    String chosenWordAndExplanation;
		    ImitatorLogger.getInstance().addToLastSentence(":->) The POS of next word is: "+currPOS);
		    copyFirstPart = (isReverse && index > 0)? CreatorProps.getPositionsToCopyDirectly().contains(String.valueOf(index)) 
		    										 : CreatorProps.getPositionsToCopyDirectly().contains(String.valueOf(index+1));
		    
		    // check if a direct copy should be made
		    Collection<String> posToCopyDirectly = CreatorProps.getPosToCopyDirectly();
		    if (posToCopyDirectly.contains(currPOS) || copyFirstPart) { 
		    	chosenWord = chosenWordAndExplanation = originalWord;
				chosenWordKey = Commons.getKey(chosenWord, currPOS);
				ImitatorLogger.getInstance().addToLastSentence(":->) The choosen word is: "+chosenWord+"; copied directly from real sentence because of "+(copyFirstPart? "its position": "its POS"));
		    }  else  { // no direct copy, should calc frequencies
		        Map<String, Long> mapFromWordToCombinedFrequency = getCombinedFrequencyMap(generatedSentenceWords, isReverse, originalWord, currPOS);
		        if (mapFromWordToCombinedFrequency==null) {
			    	chosenWord = chosenWordAndExplanation = originalWord;
					chosenWordKey = Commons.getKey(chosenWord, currPOS);
					ImitatorLogger.getInstance().addToLastSentence(":->) The choosen word is: "+chosenWord+"; copied directly from real sentence because I couldn't find a replacement that matches both POS and sequence");
		        } else {
					// replace special characters back to the real character
					chosenWordKey = SentenceCreator.getRandomElementByAccuracies(mapFromWordToCombinedFrequency,true);
					chosenWord = Commons.getRealWord(chosenWordKey);
					if (!chosenWord.equals(originalWord)) {
						numberOfWordsReplaced++;
						chosenWordAndExplanation = originalWord+slashes[numberOfRelevantPreviosWordsInSentence-1]+chosenWord;
					} else {
						chosenWordAndExplanation = chosenWord;
					}
		        }
		    }
		    generatedSentenceWords.add(chosenWord);
		    generatedSentenceAndExplanationWords.add(chosenWordAndExplanation);
		    ImitatorLogger.getInstance().addToLastSentence(generatedSentenceWords.toString());
		    index++;
		}

	    ImitatorLogger.getInstance().addToLastSentence(":->) "+numberOfWordsReplaced+" out of "+realSentence.size()+" words replaced.");
	    if (numberOfWordsReplaced<=0) {
			ImitatorLogger.getInstance().addToLastSentence("------- NGramsWithSuffixArray: No word replaced!");
	    	return null;
	    } else {
	    	return join(generatedSentenceAndExplanationWords," ");
	    }
    }

    /**
     * @return a map from each word to its combined frequency (sequence * POS)
     */
    public Map<String, Long> getCombinedFrequencyMap(List<String> generatedSentenceWords, boolean isReverse, String originalWord, String currPOS) {
    	
		// reads the map that maps from the possible words of the current POS
		// to the number of accuracies in the corpus for each of those words
    	
    	Map<String, Long> mapFromWordToFrequencyByPOS = null;
    	if (currPOS!=null) {
	    	mapFromWordToFrequencyByPOS = mapPosToMapWordToFrequency.get(currPOS);
	    	if (mapFromWordToFrequencyByPOS==null) {
	    		String posFile = FileUtils.getPosFile(corpusDir, Commons.replaceSpecialCharacters(currPOS));
	    		System.out.println(" ## Reading file for POS "+currPOS+": "+posFile);
	    		mapFromWordToFrequencyByPOS = FileUtils.readPOS2WordsFile(new File(posFile));
	    		mapPosToMapWordToFrequency.put(currPOS, mapFromWordToFrequencyByPOS);
	    	}
	    	if (mapFromWordToFrequencyByPOS==null) {
				ImitatorLogger.getInstance().addToLastSentence("------- NGramsWithSuffixArray: frequency map for POS '" + currPOS + "' in the corpus!");
			    return null;
	    	}
    	}

		// get all the relevant next words from the suffix array. Copied from:
		// Map<String, Integer> wordFrequencyBySequence = getProbMapFromSuffixArray(generatedSentence.toString(), isReverse, true);
    	
			WordsSuffixArray relevantSuffixArray = (!isReverse? _mainSuffixArray: _mainReverseSuffixArray);
    		String sequenceToCompare = null;
    		Map<String, Long> mapFromWordToFrequencyAfterSequence=null, mapFromWordToCombinedFrequency=null;
			int sentenceLength = generatedSentenceWords.size();
			numberOfRelevantPreviosWordsInSentence = Math.min(sentenceLength, CreatorProps.getN())+1;
			for (;;) {  // loop until a new word is found:
				numberOfRelevantPreviosWordsInSentence--;
				if (numberOfRelevantPreviosWordsInSentence<=0) {
					ImitatorLogger.getInstance().addToLastSentence("------- NGramsWithSuffixArray: Cannot find any continuation to '" + sequenceToCompare + "' in the corpus!");
				    return null;
				}
				int startIndex = Math.max(sentenceLength - numberOfRelevantPreviosWordsInSentence, 0);
				sequenceToCompare = join(generatedSentenceWords, " ", startIndex, numberOfRelevantPreviosWordsInSentence);

				// find the range of entries in the suffix array that match to 'sequenceToCompare'
				Range range = SuffixArraysUtils.findRange(relevantSuffixArray, sequenceToCompare, numberOfRelevantPreviosWordsInSentence);
				ImitatorLogger.getInstance().addToLastSentence(":->) Looking for a continuation for the "+numberOfRelevantPreviosWordsInSentence+"-gram '"+sequenceToCompare+"'. Range is "+range);
				
				if (range==null) continue;
				if (range.getCount() < CreatorProps.getNumberOfNgramToReduceN()) continue;
				
				mapFromWordToFrequencyAfterSequence = SuffixArraysUtils.getNextWordProbMap(relevantSuffixArray, range, numberOfRelevantPreviosWordsInSentence, isReverse, originalWord);
				if (mapFromWordToFrequencyAfterSequence == null) {
					ImitatorLogger.getInstance().addToLastSentence("------- NGramsWithSuffixArray: Didn't find N-gram of '" + sequenceToCompare + "' in the corpus!");
				    return null;
				}
			    ImitatorLogger.getInstance().addToLastSentence(":->) wordFrequencyBySequence contains "+mapFromWordToFrequencyAfterSequence.size()+" possible words");

				mapFromWordToCombinedFrequency = currPOS==null? mapFromWordToFrequencyAfterSequence: product(mapFromWordToFrequencyAfterSequence, mapFromWordToFrequencyByPOS);
				if (mapFromWordToCombinedFrequency.size()>0)
					break;
			}
			return mapFromWordToCombinedFrequency;
			
		    /*
		    // in case that the number of examples is still too small, use the supplementary corpus
		    if (mapSize < CreatorProps.getNumberOfNgramToUseSupCorpus() && supplementaryCo					return mapFromWordToCombinedFrequency;
rpusName!=null) {
		    	ImitatorLogger.getInstance().addToLastSentence("## The size of results of the sequence '" + sequenceToCompare 
						+ "' is still too small (" + mapSize + "). Using supplementary corpus.");

				relevantSuffixArray = (!isReverse? _suppSuffixArray: _suppReverseSuffixArray);
				range = SuffixArraysUtils.findRange(relevantSuffixArray, sequenceToCompare, numberOfRelevantPreviosWordsInSentence);
				ImitatorLogger.getInstance().addToLastSentence(":->) Using supplementary corpus: Range is "+range);
				if (range != null)
			    	probMap = SuffixArraysUtils.getNextWordProbMap(relevantSuffixArray, range, numberOfRelevantPreviosWordsInSentence, isReverse);
			    mapSize = SentenceCreator.countAccuraciesInt(probMap);
		    }originalWord
			*/
    }

    /**
     * @return map from a word to an integer proportional to its probability. 
     */
    public Map<String, Long> getProbMapFromSuffixArray(String generatedSentence, boolean isReverse, String originalWord) {
		List<String> sentence = SentenceCreator.getListFromStrings(generatedSentence);
		sentence.add(0, Commons.PRE_SENTENCE_SIGN);
		int sentenceLength = sentence.size();
		int n = Math.max(CreatorProps.getN(), 2);
		String sequenceToCompare = new String();

		int numberOfRelevantPreviosWordsInSentence = Math.min(sentenceLength, n);

		int startIndex = Math.max(sentenceLength - numberOfRelevantPreviosWordsInSentence, 0);

		for (int i = startIndex; i < startIndex + numberOfRelevantPreviosWordsInSentence; i++) 
			sequenceToCompare += sentence.get(i) + " ";

		WordsSuffixArray relevantSuffixArray = (!isReverse? _mainSuffixArray: _mainReverseSuffixArray);
		Map<String, Long> probMap = null;
		long mapSize;

		// find the range of entries in the suffix array that match to 'sequenceToCompare'
		Range range = SuffixArraysUtils.findRange(relevantSuffixArray, sequenceToCompare, numberOfRelevantPreviosWordsInSentence);
		ImitatorLogger.getInstance().addToLastSentence(":->) N-Gram of next word is: "+numberOfRelevantPreviosWordsInSentence+" ("+sequenceToCompare+"). Range is "+range);

		int minNumberOfPreviousWords = 2;
		// in case that the number of examples is too small - reduce the order of n-gram
		while (numberOfRelevantPreviosWordsInSentence > minNumberOfPreviousWords && (range == null || (range.getCount() < CreatorProps.getNumberOfNgramToReduceN())) )	{
			numberOfRelevantPreviosWordsInSentence--; 
		    sequenceToCompare = new String();
		    startIndex = Math.max(sentenceLength - numberOfRelevantPreviosWordsInSentence, 0);
		    for (int i = startIndex; i < startIndex + numberOfRelevantPreviosWordsInSentence; i++) 
		    	sequenceToCompare += sentence.get(i) + " ";
		    range = SuffixArraysUtils.findRange(relevantSuffixArray, sequenceToCompare, numberOfRelevantPreviosWordsInSentence);
			ImitatorLogger.getInstance().addToLastSentence(":->) N-Gram now reduced to: "+numberOfRelevantPreviosWordsInSentence+" ("+sequenceToCompare+"). Range is "+range);
		}

		if (range != null)
	    	probMap = SuffixArraysUtils.getNextWordProbMap(relevantSuffixArray, range, numberOfRelevantPreviosWordsInSentence, isReverse, originalWord);
	    mapSize = SentenceCreator.countAccuracies(probMap);

	    // in case that the number of examples is still too small, use the supplementary corpus
	    if (mapSize < CreatorProps.getNumberOfNgramToUseSupCorpus() && supplementaryCorpusName!=null) {
	    	ImitatorLogger.getInstance().addToLastSentence("## The size of results of the sequence '" + sequenceToCompare 
					+ "' is still too small (" + mapSize + "). Using supplementary corpus.");

			relevantSuffixArray = (!isReverse? _suppSuffixArray: _suppReverseSuffixArray);
			range = SuffixArraysUtils.findRange(relevantSuffixArray, sequenceToCompare, numberOfRelevantPreviosWordsInSentence);
			ImitatorLogger.getInstance().addToLastSentence(":->) Using supplementary corpus: Range is "+range);
			if (range != null)
		    	probMap = SuffixArraysUtils.getNextWordProbMap(relevantSuffixArray, range, numberOfRelevantPreviosWordsInSentence, isReverse, originalWord);
		    mapSize = SentenceCreator.countAccuracies(probMap);
	    }

	    ImitatorLogger.getInstance().addToLastSentence(":->) probmap"+(probMap==null? "=null": " contains "+probMap.size()+" possible words"));
		return probMap;
    }
    
    /**
     * merges two sentences - they have to be built of the same number of words
     */
	@Override public String mergeTwoSentences(String firstPartOfsentence, String secondPartOfsentence, int[] possibleMergeLocations) {
		long start = System.currentTimeMillis();
		
		//int curMergeIndex = 0;

		ImitatorLogger.getInstance().addToLastSentence("**** Merging of: "+firstPartOfsentence + " and: "	+ secondPartOfsentence+ " ****");
		
		List<String> firstPartWords = SentenceCreator.getListFromStrings(firstPartOfsentence);
		firstPartWords.add(0, Commons.PRE_SENTENCE_SIGN);
		List<String> secondPartWords = SentenceCreator.getListFromStrings(secondPartOfsentence);
		secondPartWords.add(0, Commons.PRE_SENTENCE_SIGN);

		int wordCount = firstPartWords.size();
		if (secondPartWords.size()!=wordCount) {
			ImitatorLogger.getInstance().addToLastSentence("**** ERROR: first part has "+wordCount+" words and second part has "+secondPartWords.size()+" words!");
			if (wordCount>secondPartWords.size())
				wordCount = secondPartWords.size();
		}

		int maxMergeIndex = wordCount;
		
		if (possibleMergeLocations==null) {
			possibleMergeLocations = new int[wordCount-1];
			for (int i=0; i<wordCount-1; ++i)
				possibleMergeLocations[i]=i;
		}

		/* NEW VERSION BY EREL: */
		//System.out.println();
		float[] firstPartLikelihoods = new float[wordCount]; // firstPartLikelihoods[i] = likelihood of sentence[0..i]
		firstPartLikelihoods[0] = 0;
		for (int i=0; i<wordCount-1; ++i) {
			float likelihood = SuffixArraysUtils.bigramLikelihood(_mainSuffixArray, firstPartWords.get(i), firstPartWords.get(i+1));
			firstPartLikelihoods[i+1] = firstPartLikelihoods[i] + likelihood;
		}
		//System.out.println();
		float[] secondPartLikelihoods = new float[wordCount]; // secondPartLikelihoods[i] = likelihood of sentence[i..end]
		secondPartLikelihoods[wordCount-1] = 0;
		for (int i=wordCount-1; i>0; i--) {
			//float likelihood = SuffixArraysUtils.bigramLikelihood(_mainReverseSuffixArray, secondPartWords.get(i-1), secondPartWords.get(i));
			float likelihood = SuffixArraysUtils.bigramLikelihood(_mainSuffixArray, secondPartWords.get(i-1), secondPartWords.get(i));
			secondPartLikelihoods[i-1] = secondPartLikelihoods[i] + likelihood;
		}

		float maxLikelihood = 0;
		for (int curMergeIndex: possibleMergeLocations) {
			String curBigram12 = firstPartWords.get(curMergeIndex) +" "+ secondPartWords.get(curMergeIndex+1);
			float likelihoodFromStart = firstPartLikelihoods[curMergeIndex];
			float glueLikelihood = SuffixArraysUtils.bigramLikelihood(_mainSuffixArray, firstPartWords.get(curMergeIndex), secondPartWords.get(curMergeIndex+1));
			float likelihoodToEnd = secondPartLikelihoods[curMergeIndex+1];
			float curLikelihood = likelihoodFromStart+glueLikelihood+likelihoodToEnd;
			ImitatorLogger.getInstance().addToLastSentence("**** Trying to merge at "+curMergeIndex+":"+curBigram12+": "+likelihoodFromStart+" + "+glueLikelihood+" + "+likelihoodToEnd+" = "+curLikelihood);
			if (maxLikelihood==0||curLikelihood>maxLikelihood) {
				maxLikelihood = curLikelihood;
				maxMergeIndex = curMergeIndex;
			}
		}
		
		// Remove the Commons.PRE_SENTENCE_SIGN
		firstPartWords.remove(0);
		secondPartWords.remove(0);
		wordCount--;

		/* OLD VERSION BY EREL: *
		double maxLikelihood = measureSentenceLikelihood(firstPartWords); 
		
		List<String> mergedWords = SentenceCreator.getListFromStrings(firstPartOfsentence);
		for (curMergeIndex=wordCount-1; curMergeIndex>=0; --curMergeIndex) {
			ImitatorLogger.getInstance().addToLastSentence("**** Trying to merge at "+curMergeIndex);
			mergedWords.set(curMergeIndex, secondPartWords.get(curMergeIndex));
			double curLikelihood = measureSentenceLikelihood(mergedWords); 
			if (curLikelihood>maxLikelihood) {
				maxLikelihood = curLikelihood;
				maxMergeIndex = curMergeIndex;
			}
		}
		
		*/
		
		ImitatorLogger.getInstance().addToLastSentence("**** merge index: "+maxMergeIndex+" merge time: "+(System.currentTimeMillis()-start)/1000+" s");
		
		StringBuffer mergedSentence = new StringBuffer();
		int curMergeIndex;
		for (curMergeIndex=0; curMergeIndex<maxMergeIndex; ++curMergeIndex) {
			mergedSentence.append(firstPartWords.get(curMergeIndex));
			mergedSentence.append(' ');
		}
		for (; curMergeIndex<wordCount; ++curMergeIndex) {
			mergedSentence.append(secondPartWords.get(curMergeIndex));
			mergedSentence.append(' ');
		}

		ImitatorLogger.getInstance().addToLastSentence("**** 1st part from: "+firstPartOfsentence);
		ImitatorLogger.getInstance().addToLastSentence("**** 2nd part from: "+secondPartOfsentence);
		ImitatorLogger.getInstance().addToLastSentence("**** Best merge is at "+maxMergeIndex+": "+mergedSentence);

		return mergedSentence.toString();
		
		/* OLD-OLD VERSION: *
		String space = " ";
		double curEval = maxLikelihood;
		int curChecking = 0, index1 = 0,index2 = 0, indexTemp1 = 0,indexTemp2 = 0;

		StringBuffer firstCopy = new StringBuffer(firstPartOfsentence);
		StringBuffer secondCopy = new StringBuffer(secondPartOfsentence);
		StringBuffer testStr = new StringBuffer(secondPartOfsentence);

		index1 = 0;
		index2 = 0;
		int diff = 1;

		curChecking = 0;
		index1 = 0;
		index2 = 0;
		while (curChecking < curMergeIndex)
		{
			index1 = firstCopy.indexOf(space,index1+1);
			index2 = secondCopy.indexOf(space,index2+1);
			curChecking++;
		}
		if (index1!= -1 && index2!= -1)
		{
			String result = firstCopy.substring(0, index1)+secondCopy.substring(index2);
			ImitatorLogger.getInstance().addToLastSentence("**** End of merging; the result is: "+result+" with probability "+String.format("%.3f", maxLikelihood)+" ****");
			return result;
		}
		else
		{
			ImitatorLogger.getInstance().addToLastSentence("**** End of merging; the result is: "+firstPartOfsentence+" with probability "+String.format("%.3f", maxLikelihood)+" ****");
			return firstPartOfsentence;
		}
		*/
	}
	
	/**
	 * measures the total likelihood of the given list of tokens.
	 * @param sentenceList a list of tokens.
	 * @return the log-likelihood.
	 */
	@SuppressWarnings("unused")
	private double measureSentenceLikelihood(List<String> sentenceList) {
		ImitatorLogger.getInstance().addToLastSentence("**** :->) measuring sentence: "+sentenceList);
		int i;
		int sentenceLength = sentenceList.size();
		if (sentenceLength == 0)
			return 0;

		double sentenceProb = 0.0;
		Map<String, Long> probMap = null;

		StringBuffer strToTest = new StringBuffer(sentenceList.get(0));
		for (i = 1; i < sentenceLength; i++) {
			probMap = getProbMapFromSuffixArray(sentenceList.get(i)+" "+sentenceList.get(i+1), false, null);
			if (probMap != null && probMap.containsKey(sentenceList.get(i))) {
				String nextWord = sentenceList.get(i);
				long nextWordFrequency = probMap.get(nextWord);
				double nextWordProbability = (double)(nextWordFrequency)/countAccuracies(probMap);
				ImitatorLogger.getInstance().addToLastSentence("**** :->) next word is "+nextWord+" probability="+nextWordProbability);
				sentenceProb += Math.log(nextWordProbability);
			}
			strToTest.append(" " + sentenceList.get(i));
		}
		double sentenceProbPerWord = sentenceProb/((double)sentenceLength);
		ImitatorLogger.getInstance().addToLastSentence("**** :->) total logprob="+sentenceProb+", logprob per length="+sentenceProbPerWord);
//		Option 1:
//		return sentenceProbPerWord;
//		Option 2:
		return sentenceProb;
	}
	
	private double countAccuracies(Map<String, Long> posTemplatesFrequencies)  {
		int posTemplatesCount = 0;
		for (Long localCount : posTemplatesFrequencies.values()) {
		    if (localCount == null)
		    	continue;
		    posTemplatesCount += localCount.intValue();
		}
		return ((double)posTemplatesCount);
    }

	
	
	


	/**
	 * Create a string containing the string representations of all elements in the given collection, in order, glued with the given glue-string  
	 * @param glue - space, comma, newline, etc.
	 * @author Erel Segal
	 * @since 2011-10-27
	 */
	public static <T> String join(Iterable<T> elements, String glue) {
		StringBuffer result = new StringBuffer();
		for (T element: elements) {
			if (element==null) continue; 
			if (result.length()>0)  result.append(glue);
			result.append(element.toString());
		}
		return result.toString();
	}

	/**
	 * Create a string containing the string representations of all elements in the given collection, in order, glued with the given glue-string  
	 * @param glue - space, comma, newline, etc.
	 * @author Erel Segal
	 * @since 2011-10-27
	 */
	public static <T> String join(List<T> elements, String glue, int from, int length) {
		StringBuffer result = new StringBuffer();
		for (int i=0; i<length; ++i) {
			if (result.length()>0)  result.append(glue);
			result.append(elements.get(from+i).toString());
		}
		return result.toString();
	}

	/**
	 * <p>Calculate a map c, such that: for each key in BOTH a AND b:
	 * <p>c[key] = a[key] * b[key]
	 */
	public static Map<String,Long> product (Map<String,Long> a, Map<String,Long> b) {
		Map<String,Long> c = new HashMap<String,Long>();
		for (Map.Entry<String,Long> entry: b.entrySet()) {
			String key = entry.getKey();
			Long bValue = entry.getValue();
			Long aValue = a.get(key);
			if (aValue!=null) 
				c.put(key, aValue*bValue);
		}
		return c;
	}


	
	
	
	/*
	 * TEST ZONE
	 */
	
	public static void testMemory() {
		System.out.println(memory());
		int count = 1000000;
		int[] i = new int[count];  // 4
		System.out.println(memory());
		Integer[] I = new Integer[count];  // 4
		System.out.println(memory());
		String[] s = new String[count];  // 4
		System.out.println(memory());
		IntString[] is = new IntString[count];  // 4
		System.out.println(memory());
		System.out.println();
	
		for (int j=0; j<count; ++j)  i[j]=j;  // 0
		System.out.println(memory());
		for (int j=0; j<count; ++j)  I[j]=j;  // 16
		System.out.println(memory());
		for (int j=0; j<count; ++j)  s[j]="a"; // 0
		System.out.println(memory()); 
		for (int j=0; j<count; ++j)  is[j]=new IntString(j, String.valueOf(Math.random())); // 24
		System.out.println(memory());
		for (int j=0; j<count; ++j)  s[j]=String.valueOf(Math.random()); // 40(before)/13(after)
		System.out.println(memory()); 
	}
	
	public void testCombinedMap(String precedingSequence, String currentWord, String currentPos) {
		System.out.println(getCombinedFrequencyMap(
				Arrays.asList(precedingSequence.split(" ")),
				false,
				currentWord,
				currentPos
				));
	}
	
	public void testGenerate(String pos, String words) {
		System.out.println(generateSentence(
				Arrays.asList(pos.split(" ")),
				Arrays.asList(words.split(" ")),
				false
				));
	}
	
	public void test() {
		testCombinedMap("כל נעשה מאת", "ד'", null /* any pos*/);
		//testGenerate("conj prep noun-m-s def noun-f-s def pronoun prep noun-m-s quantifier noun-f-p def noun-f-s conj def noun-f-p punctuation sub conjunction sub def quantifier verb preposition noun-m-s preposition properName punctuation conjunction def quantifier verb verb noun-m-s noun-m-s preposition noun-f-s properName punctuation verb at-preposition noun-m-s adjective conj at-preposition properName preposition punctuation",
		//		"ו מ סוד ה השגחה ה זאת ב אות כל כוונות ה תורה ו ה מצוות , ש כמו ש ה כל נעשה מאת ד' בשביל ישראל , כן ה כל מכוון לעשות רצון ד' בשביל טובת ישראל , עבדו את ד' אלהיכם ו את ישראל עמו .");
		testGenerate("conj temp verb def noun-f-s conj def noun-f-s punctuation conjunction prep noun-f-s verb properName punctuation verb preposition noun-f-s at-preposition pronoun def noun-m-s def participle punctuation negation verb punctuation",
				"ו כש באה ה מסורת ו ה דת , אפילו ב צורתה היותר טהורה , לכבוש תחת ידה את זה ה חלק ה מזוקק , לא תצליח .");
		
		//testCombinedMap("ש תיכף ל", "חורבן", "noun-m-s");
		//testCombinedMap("ו תיכף ל", "סתירתם", "noun-f-s");
		//testGenerate("conjunction interjection adverb properName noun-m-s adjective punctuation sub adverb prep noun-m-s noun-m-p verb properName shel-preposition properName noun-m-p punctuation conj def noun-f-s copula adverb adverb participle preposition def noun-m-s punctuation conj adverb properName preposition quantifier def noun-m-s sub verb punctuation conj def noun-f-s prep participle at-preposition def noun-m-s shel-preposition def noun-m-s punctuation conj def noun-f-s participle at-preposition quantifier def noun-f-p shel-preposition def noun-m-s punctuation conj def noun-f-s prep properName at-preposition quantifier def noun-m-s prep sub noun-m-s punctuation conjunction noun-m-s adjective pronoun punctuation conjunction properName adjective pronoun punctuation negation existential adverb verb prep noun-m-s adjective def noun-m-s shel-preposition def noun-m-p prep sub noun-m-s def noun-f-s punctuation",
        //"אלמלא יש כאן אור חסד מקיף , ש תיכף ל חורבן עולמים בא אור של הויית עולמים , ו ה הויה היא בודאי יותר חשובה מן ה חורבן , ו יותר רוממה מן כל ה יש ש נחרב , ו ה הויה מ נחמת את ה אבל של ה חורבן , ו ה הויה משיבה את כל ה אבדות של ה חורבן , ו ה הויה מ חיה את כל ה מת ש ב חורבן , לולא חסד גדול זה , לולא רחמים רבים הללו , לא היה אפשר לעמוד ב פני יסורי ה חורבן של ה עולמים ש ב צער ה יצירה .");
		//testGenerate("verb sub pronoun verb pronoun at-preposition pronoun punctuation conj adverb prep noun-f-s def adjective",
        //"נמצא ש הם סותרים זה את זה , ו תיכף ל סתירתם ה בולטת");
		//testGenerate("conjunction noun-m-s def noun-f-s verb preposition adverb prep properName punctuation verb adverb verb prep noun-m-s pronoun at-preposition noun-m-s def noun-f-s",
		//	         "אם כשרון ה אמונה מתדלדל בהם באיזו מ דה , מכירים תיכף לספוג ב מדה זו את אור ה דעת");
	}
	
	/**
	 * demo program
	 */
	public static void main(String[] args) {
	}
}

/*
 * TEST ZONE
 */

class IntString {
	int i;
	String s;
	public IntString(int i, String s) {this.i=i; this.s=s;}
}
