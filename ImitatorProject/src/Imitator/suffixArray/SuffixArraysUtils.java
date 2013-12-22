package Imitator.suffixArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import Imitator.common.FileUtils;

/**
 * 
 * @author Roni Vered
 *
 */
public class SuffixArraysUtils {
    
    /**
     * Search for a string in suffix array using binary search.
     * 
     * @param suffixArray the suffix array to search in
     * @param targetString the string that we want to find in the suffix array
     * @param numberOfRelevantPreviosWords the number of words that we want to compare from the suffix array.
     * 		For example, if we want to find the string "The boy went to school", and the number of words to compare is 3
     * 		so we will stop when we will find the place of "The boy went" in the suffix array.
     * @return the index in the suffix array that direct to string like 'targetString'
     * 		or -1 if the string wasn't found in the given suffix array
     */
    public static int find(WordsSuffixArray suffixArray, String targetString, int numberOfRelevantPreviosWords) {
		int[] suffixes = suffixArray.getSuffixes();
		WordList corpusWords = suffixArray.getCorpusWords();
		
		if (suffixArray.getLength() == 0) {
			return targetString.length() == 0 ? 0 : -1;
		}
		
		String stringToCompare = null;
		
		int low = 0;
		int high = suffixes.length - 1;
		while (low < high) 
		{
			int middle = low + (high - low) / 2;
		    stringToCompare = getStringToCompare(corpusWords, suffixes, middle, numberOfRelevantPreviosWords); 
		    
		    //TODO case sensitive
		    if (stringToCompare.compareTo/*IgnoreCase*/(targetString) < 0) 
		    	low = middle + 1;
		    else 
		    	high = middle;
		}
		
		stringToCompare = getStringToCompare(corpusWords, suffixes, low, numberOfRelevantPreviosWords);
		if (stringToCompare.startsWith(targetString) || targetString.startsWith(stringToCompare)) 
		{
			return low;
		}
		
		return -1; // no match
    }

    /**
     * Method that builds a string with sequence of words from given point at the corpus
     * @param corpusWords list with the corpus words
     * @param suffixes the suffixes indexes
     * @param index the start index to build the string from
     * @param numberOfRelevantPreviosWords the number of words to concatenate to the string
     * @return
     */
    private static String getStringToCompare(WordList corpusWords, int[] suffixes, int index, int numberOfRelevantPreviosWords) {
    	int len = suffixes.length;

    	//no need now to handle backward string in reverse - have duplicate suffix arrays
		return getForwardString(corpusWords, suffixes, index, numberOfRelevantPreviosWords, len);
    }
	
    private static String getForwardString(WordList corpusWords, int[] suffixes, int index, int numberOfRelevantPreviosWords, int len) {
    	String stringToCompare = new String();
    	
    	int nextIndex = len;
    	if (suffixes[index] != -1)
    		nextIndex = suffixes[index];
    	
    	for (int i = 0; i < numberOfRelevantPreviosWords; i++) {
    		// check if we arrived to the end of the corpus
    		if(nextIndex < len)
    			stringToCompare += corpusWords.get(nextIndex) + " ";
    		nextIndex++;
    	}
    	
    	return stringToCompare;
    }

    /**
     * Search for the range of suffixes that starts with the given string
     * 
     * @param targetString - the string to search her range in the suffix array
     * @return startIndex...endIndex - the indexes of the first and last suffixes that starts with 'targetString'
     */
    public static Range findRange(WordsSuffixArray suffixArray, String targetString, int numberOfRelevantPreviosWords) {
		int[] suffixes = suffixArray.getSuffixes();
		WordList corpusWords = suffixArray.getCorpusWords();

		// find one result of the string in the suffix array
		int middleIndex = find(suffixArray, targetString, numberOfRelevantPreviosWords);

		if(middleIndex == -1)
		    return null;

		int startIndex = -1;
		int endIndex = -1;

		// search for the index of the last suffix that starts with targetString
		for (int i = middleIndex; i < suffixArray.getLength(); i++) {
		    String stringToCompare = getStringToCompare(corpusWords, suffixes, i, numberOfRelevantPreviosWords); 
		    //TODO case sensitive
			if (!stringToCompare.startsWith(targetString/*.toLowerCase()*/)) {
				endIndex = i - 1;
				break;
			}
		}

		// didn't found different string from targetString, meaning that this is the last string in the suffix array
		if (endIndex == -1)
		    endIndex = suffixArray.getLength() - 1;

		// search for the index of the first suffix that starts with targetString
		for (int i = middleIndex; i > 0; i--) {
		    String stringToCompare = getStringToCompare(corpusWords, suffixes, i, numberOfRelevantPreviosWords); 
			if (!stringToCompare.startsWith(targetString)) {
				startIndex = i + 1;
				break;
			}
		}

		// didn't found different string from targetString, meaning that this is the first string in the suffix array
		if (startIndex == -1)
		    startIndex = 0;
		
		return new Range(startIndex, endIndex);
    }

    /**
     * The conditional probability of seeing the second word after the first word.
     * To guarantee that there are no zeros, we add 1 to both nominator and denominator (it is not accurate)
     * @param suffixArray
     * @param firstWord
     * @param secondWord
     * @return
     */
    public static float bigramProbability(WordsSuffixArray suffixArray, String firstWord, String secondWord) {
    	Range range1 = findRange(suffixArray, firstWord, 1);
    	Range range2 = findRange(suffixArray, firstWord+" "+secondWord, 2);
    	int count1 = (range1==null? 0: range1.getCount());
    	int count2 = (range2==null? 0: range2.getCount());
    	float prob = ((float)(count2+1)/(count1+1)); // TODO: more accurate smoothing
    	//System.out.print("range of "+firstWord+"="+range1+ " range of "+firstWord+" "+secondWord+"="+range2+ " prob="+prob);
    	return prob;
    }

    /**
     * We define "Likelihood" as: 1000*log(probability) 
     * @param suffixArray
     * @param firstWord
     * @param secondWord
     * @return
     */
    public static float bigramLikelihood(WordsSuffixArray suffixArray, String firstWord, String secondWord) {
    	float probability = bigramProbability(suffixArray, firstWord, secondWord);
    	float likelihood = (float)Math.round(1000*(Math.log(probability)));
    	//System.out.println(" likelihood="+likelihood);
    	return likelihood;
    }

    /**
     * 
     * @param suffixArray
     * @param range
     * @param numberOfRelevantPreviosWords
     * @return
     */
    public static Map<String, Long> getNextWordProbMap(WordsSuffixArray suffixArray, Range range, int numberOfRelevantPreviosWords, boolean isReverse, String originalWord) {
		Map<String, Long> word2Prob = new HashMap<String, Long>();
	
		int[] suffixes = suffixArray.getSuffixes();
		WordList corpusWords = suffixArray.getCorpusWords();
		
		int len = suffixArray.getLength();
		for (int i = range.getStartIndex(); i <= range.getEndIndex(); i++) {
			int nextWordIndex = suffixes[i] + numberOfRelevantPreviosWords;

		    if (nextWordIndex >= len)
		    	break;
		    
		    String potentialWord = corpusWords.get(nextWordIndex);
		    if (potentialWord.equals(originalWord))
		    	continue;

		    Long wordCounter = word2Prob.get(potentialWord);
		    if (wordCounter != null)
		    	word2Prob.put(potentialWord, wordCounter + 1);
		    else
		    	word2Prob.put(potentialWord, 1l);
		}
		
		return word2Prob;
    }

    /**
     * Writes collection into a file
     * 
     * @param elements - the collection to write
     * @param file - the destination file
     * @throws IOException
     */
    public static void writeFileOfCorpusWords(WordList elements, String file) {
    	try {

    		FileWriter writer = new FileWriter(file);

    		int index = 0;

    		for (int i=0; i<elements.size(); ++i) {
    			String element = elements.get(i);
    			int indexOfSpace = element.indexOf(" ");
    			if( indexOfSpace != -1){
    				System.out.print(" found element with space! pruning its end! before: " + element);
    				element = element.substring(0, indexOfSpace);
    				System.out.println(", after: " + element + ".");
    			}
    			writer.write(element + " ");
    			if(++index%1000 == 0){
    				writer.write("\n");
    				index=0;
    			}
    		}

    		writer.close();

    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    /**
     * 
     * @param length
     * @param suffixes
     * @param file
     */
    public static void writeFileOfSuffixes(int length, Integer[] suffixes, String file) {
	try {

	    FileWriter writer = new FileWriter(file);

	    writer.write(length + "\n");

	    int indexInRow = 0;
	    for (int i = 0; i < suffixes.length; i++) {
		writer.write(suffixes[i] + " ");

		if(++indexInRow%1000 == 0){
		    writer.write("\n");
		    indexInRow=0;
		}
	    }

	    writer.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    /**
     * @param fileName
     * @return
     */
    public static WordList readFileOfCorpusWords(String fileName) {
    	//WordList words = new WordListWithCharArrays(); 
    	//WordList words = new WordListWithByteArrays(); 
    	//WordList words = new WordListWithChunks();
    	//WordList words = new WordListWithHash();  // about 24 bytes per word!
    	WordList words = new WordListWithIntern();  // best memory performance - only 10 bytes per word!

    	File file = new File(fileName);

    	if (!file.exists())
    		return words;

    	try {
    		BufferedReader reader = FileUtils.newBufferedReader(new File(fileName));

    		for (String currLine = reader.readLine(); currLine != null; currLine = reader.readLine()) {
    			StringTokenizer posTokenizer = new StringTokenizer(currLine);

    			for (String word = posTokenizer.nextToken();; word = posTokenizer.nextToken()) {
    				words.add(word);

    				if (!posTokenizer.hasMoreTokens())
    					break;
    			}
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	return words;
    }

    /**
     * 
     * @param fileName
     * @return
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public static int[] readFileOfSuffixes(String fileName) throws IOException {
    	int[] words = null;

    	File file = new File(fileName);
    	if (!file.exists())
    		//return words;
    		throw new IOException("File of suffixes not found: "+fileName);

    	try {
    		BufferedReader reader = FileUtils.newBufferedReader(new File(fileName));

    		String currLine = reader.readLine();
    		words = new int[Integer.parseInt(currLine)];

    		int index = 0;
    		StringTokenizer posTokenizer;
    		for (currLine = reader.readLine(); currLine != null; currLine = reader.readLine()) {
    			posTokenizer = new StringTokenizer(currLine);

    			for (String wordIndex = posTokenizer.nextToken();; wordIndex = posTokenizer.nextToken()) {
    				if (!posTokenizer.hasMoreTokens())
    					break;
    				int wordInd = Integer.parseInt(wordIndex);;
    				words[index] = wordInd;
    				index++;
    			}
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	return words;
    }
    
    /**
     * 
     * @param suffixArray
     */
    public static void validateSort(WordsSuffixArray suffixArray){

	int numberOfRelevantPreviosWords = 3;
	
	int[] suffixes = suffixArray.getSuffixes();
	WordList corpusWords = suffixArray.getCorpusWords();
	int len = suffixArray.getLength();
	
	int numberOfWrongPoints = 0;
	int totalNumberOfSentences = 0;
	
	String currString = getStringToCompare(corpusWords, suffixes, 0, numberOfRelevantPreviosWords);
	for (int i = 0; i < len - 1; i++) {
	    String nextString = getStringToCompare(corpusWords, suffixes, i + 1, numberOfRelevantPreviosWords);
	    
	    //TODO case sensitive
	    if(currString.compareTo/*IgnoreCase*/(nextString) > 0){
		numberOfWrongPoints++;
		
	    }
	    totalNumberOfSentences++;
	    
	    currString = nextString;
	}
	
	System.out.println("Wrong points: [" + numberOfWrongPoints + "/" + totalNumberOfSentences + "]");
    }

}
