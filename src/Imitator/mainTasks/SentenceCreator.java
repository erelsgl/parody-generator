package Imitator.mainTasks;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import utils.HebrewSentencePrinter;
import utils.ISentencePrinter;
import utils.LTRSentencePrinter;
import Imitator.common.Enums;
import Imitator.common.FileUtils;
import Imitator.common.Timer;
import Imitator.sentenceCreator.CreatorProps;
import Imitator.sentenceCreator.NGramsIfc;
import Imitator.sentenceCreator.NGramsWithSuffixArrayImpl;
import dataStructures.GeneratedSentence;
import dataStructures.ImitatorLogger;

/**
 * This class generates random sentences. 
 * It use the data that was collected earlier in the process.
 * The steps are:
 * 1. Choosing random sentence POS templates (with uniform distribution)
 * 2. Select the first word of the sentence:
 * 	2.1 With uniform distribution from the words that appeared in the corpus as this POS 
 * 	2.2 By the distribution of first words of sentence
 * 3. Select the next words:
 * 	3.1 By uniform distribution from the words that appeared in the corpus as this POS (like 2.1)
 * 	3.2 By the distribution of words that appeared in the corpus after the previous 2 words (3-grams) 
 * 
 * Usage: SentenceCreator <corpusName> <Frequencies Base Directory> <Number of sentences>
 * 
 * @author Roni Vered
 *
 */
public class SentenceCreator {

    private static Timer _timer = new Timer();
    
    /**
     * Counts the number of times that the generation process was failed and was restarted 
     */
    private static int totalNumberOfRetries = 0;

    /**
     *  The maximum number of tries to generate sentence from certain POS template 
     */
    private static final int NUMBER_OF_RETRIES = 3;
    
    /**
     *  The minimal sentence length for splitting the sentence 
     * (build the first part from the beginning to the end, and the second part backwards)
     */
    private static int MINIMAL_TEMPLATE_SIZE_TO_SPLIT;

    private static ISentencePrinter _printer;

    private String _sentenceCreationDirection;
    public void setSentenceCreationDirection (String newDirection) {
    	_sentenceCreationDirection = newDirection;
    }
    
    private String corpusDir;
	private String supplementaryCorpusName;

   
    /**
     * For each i, line 2*i is a sentence, and line 2*i+1 is the pos sequence of the sentence. 
     */
    private List<String> _fullSentences;
    
    private NGramsIfc _nGramsImpl;

	private int numberOfSectionsPerChoice, numberOfSentencesPerSection;
    
    public int getNumberOfSectionsPerChoice() {
		return numberOfSectionsPerChoice;
	}

	public int getNumberOfSentencesPerSection() {
		return numberOfSentencesPerSection;
	}

	public static void initialize_static(Properties properties) {

		ImitatorLogger.getInstance().printToOut = Boolean.parseBoolean(properties.getProperty("debug"));
		
		CreatorProps.setN(Integer.parseInt(properties.getProperty("n.gram.order")));
		CreatorProps.setNumberOfNgramToReduceN(Integer.parseInt(properties.getProperty("number.of.ngram.to.reduce.n")));
		CreatorProps.setNumberOfNgramToUseSupCorpus(Integer.parseInt(properties.getProperty("number.of.ngram.to.use.sup.corpus")));
		CreatorProps.setPositionsToCopyDirectly(getListFromStrings(properties.getProperty("positions.to.copy.directly")));
		CreatorProps.setPosToCopyDirectly(getListFromStrings(properties.getProperty("pos.to.copy.directly")));
		CreatorProps.setLanguage(properties.getProperty("language"));
		CreatorProps.setOutputFilePath(properties.getProperty("output.file.path"));
		MINIMAL_TEMPLATE_SIZE_TO_SPLIT = Integer.parseInt(properties.getProperty("minimal.template.size.to.split"));

		String encoding = properties.getProperty("input.file.encoding");
		if (encoding!=null)
			FileUtils.setInputFileEncoding(encoding);


		initPrinter();
    }
    
    public void initialize(Properties properties) throws IOException {
    	initialize_static(properties);
    	setMainFrequenciesDir(properties.getProperty("main.frequencies.dir"));
		// If there use of the supplementary corpus - initialize its location
		// TODO check these lines
    	boolean shouldUseSupplementaryCorpus = Boolean.parseBoolean(properties.getProperty("should.use.supplementary.corpus"));
		if (shouldUseSupplementaryCorpus) {
		    String supplFrequenciesPath = properties.getProperty("supplementary.frequencies.dir");
		    supplementaryCorpusName = supplFrequenciesPath.substring(supplFrequenciesPath.lastIndexOf("/") + 1);
		} else {
			supplementaryCorpusName = null;
		}

		_sentenceCreationDirection = properties.getProperty("sentence.creation.direction");

		/**********************************/
		/**** Filter the POS templates ****/
		/**********************************
		int minimalLengthOfSentence = Integer.parseInt(properties.getProperty("minimal.sentence.length"));
		int minimalFrequencyOfSentence = Integer.parseInt(properties.getProperty("minimal.sentence.frequency"));
		mainCorpusFilteredTemplates = filterCorpus(minimalLengthOfSentence, minimalFrequencyOfSentence);
		*/
	
		// filter the POS templates of the supplementary corpus - no need for now
	//	if (CreatorProps.getShouldUseSupplementaryCorpus()) {
	//	    Map<String, Long> supplementaryCorpusFilteredTemplates = filterCorpus(CreatorProps.getSupplementaryCorpusName(), minimalLengthOfSentence, minimalFrequencyOfSentence);
	//	}	
		_nGramsImpl = new NGramsWithSuffixArrayImpl(corpusDir, supplementaryCorpusName);
		

		this.numberOfSectionsPerChoice = Integer.parseInt(properties.getProperty("number.of.sections.per.choice"));
		this.numberOfSentencesPerSection = Integer.parseInt(properties.getProperty("number.of.sentences.per.section"));
		
		// read the data of the mainCorpus, including forward and reverse suffix arrays - about 1 minute
		readStatistics();
    }
    
    public void run(Properties properties) throws IOException {
		_timer.startClock();

		initialize(properties);
		//System.exit(1);
		
		/*************************************/
		/**** Generating random sentences ****/
		/*************************************/
		System.out.println(" ## Generating random text based on " + corpusDir);
		List<String> sentences 			= new ArrayList<String>();
		List<String> templates 			= new ArrayList<String>();
		List<String> beginSentences		= new ArrayList<String>();
		List<String> mergedSentences	= new ArrayList<String>();
		
		for (int i = 0; i < numberOfSectionsPerChoice; i++) {
		    GeneratedSentence sentence = createRandomSection(numberOfSentencesPerSection, /*adjacent=*/true);
		    if (sentence != null) {
		    	// if the sentence generated successfully
		    	sentences.add(sentence.getCreatedSentence());
		    	templates.add(sentence.getBeginTemplate());
		    	beginSentences.add(sentence.getBeginSentence());
		    	if (sentence.getMergeParts() != null) {
		    		mergedSentences.add(sentence.getMergeParts());
		    	}
		    } else {
				// if the generation was failed
				i--;
		    }
		}

		/*******************/
		/***** Summary *****/
		/*******************/
	
		_printer.printSentences(templates,		"  >>All generated templates:");
		_printer.printSentences(beginSentences,	"  >>All initial sentences:");
		_printer.printSentences(mergedSentences,"  >>All parts that were merged (created from different sides)");
		_printer.printSentences(sentences,		"  >>All GENERATED SENTENCES!!!");
	
		//print the text to the user in specified file
		_printer.printTextToFile(CreatorProps.getOutputFilePath(),sentences);
		// print the total number of times that generations were failed 
		System.out.println("total number of retries: " + (totalNumberOfRetries/* - numberOfSentencesToCreate*/));
		//System.out.println("\n***********************************\n>>Creation Statistics:");
		//ImitatorLogger.getInstance().printAll();
		
		_timer.finish();
    }

    private static void initPrinter()  {
		if (CreatorProps.getLanguage().equalsIgnoreCase("LTR"))
			_printer = new LTRSentencePrinter();
		else
			_printer = new HebrewSentencePrinter();
	}
    
    private void setMainFrequenciesDir(String frequenciesPath) {
		int lastDirectoryIndex = frequenciesPath.lastIndexOf("\\"); // windows
		if (lastDirectoryIndex<0)
			lastDirectoryIndex = frequenciesPath.lastIndexOf("/");  // linux
		this.corpusDir = frequenciesPath;
		//CreatorProps.setMainCorpusName(corpusName);
		//CreatorProps.setBaseDir(frequenciesPath);
    }

    private static String memory() {
    	final int unit = 1000000; // MB
    	long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    	long availableMemory = Runtime.getRuntime().maxMemory() - usedMemory;
    	return "Memory: "+" used="+usedMemory/unit+" available="+availableMemory/unit+" free="+(Runtime.getRuntime().freeMemory()/unit)+" total="+(Runtime.getRuntime().totalMemory()/unit)+" max="+(Runtime.getRuntime().maxMemory()/unit);
    }

    /**
     * read the frequencies from the files
     */
    private void readStatistics() {
    	System.err.println(memory());

    	System.err.print(" ## Reading full sentences file...");
		_fullSentences = FileUtils.readFullSentencesFile(corpusDir);
		System.err.println("map size="+_fullSentences.size()+". "+memory());
    }

    /**
     * Filter the POS templates according to a given properties.
     * The filtered templates will be in use while generated the sentences
     * 
     * @param mainCorpusName - the corpus to filter for
     * @param minimalLengthOfSentence
     * @param minimalFrequencyOfSentence
     * @return a Map with the filtered templates
     *
    private Map<String, Long> filterCorpus(int minimalLengthOfSentence, int minimalFrequencyOfSentence) {
		File originalTemplatesFile = FileUtils.getPosTemplatesFile(baseDir, mainCorpusName);
	
		Map<String, Long> originalTemplates = FileUtils.readPosTemplatesFile(originalTemplatesFile);
	
		Map<String, Long> filteredTemplates = FilterUtils.removeUncommonTemplates(originalTemplates, minimalLengthOfSentence, minimalFrequencyOfSentence);
		
		System.err.println(" ## Finished fitering the corpus " + mainCorpusName + ". Size before: " + originalTemplates.size() + ", after: " + filteredTemplates.size());
		
		originalTemplates.clear();
		
		return filteredTemplates;
    }*/
    

	/**
	 * Create a string containing the string representations of all elements in the given array, in order, glued with the given glue-string  
	 * @param <T>
	 * @param elements
	 * @param glue - space, comma, etc.
	 * @return the string.
	 */
	public static <T> String join(Collection<T> elements, String glue) {
		StringBuffer result = new StringBuffer();
		for (T element: elements) {
			if (element==null) continue; 
			if (result.length()>0)  result.append(glue);
			result.append(element.toString());
		}
		return result.toString();
	}
    
    
    /**
     * @return a random sentence from the corpus.
     * @author erelsgl
     */
    public String pickRandomSentence() {
    	int start = ((int)(Math.random()*_fullSentences.size()/2))*2;
    	return _fullSentences.get(start);
    }
    
    /**
     * @return [0] - a section of several non-adjacent sentences from the corpus. [1] - their POS sequences.
     * @param sentenceCount
     * @author erelsgl
     */
    public String[] pickRandomSentences(int sentenceCount) {
    	StringBuffer sectionWords = new StringBuffer();
    	StringBuffer sectionPOSes = new StringBuffer();
    	for (int count=0; count<sentenceCount; ++count) {
        	int i = ((int)(Math.random()*_fullSentences.size()/2))*2;
    		sectionWords.append(_fullSentences.get(i)).append(' ');
    		sectionPOSes.append(_fullSentences.get(i+1)).append(' ');
    	}
    	return new String[] {
    			sectionWords.toString(),
    			sectionPOSes.toString()
    	};
    }
   
    /**
     * @return [0] - a section of several adjacent sentences from the corpus. [1] - their POS sequences.
     * @param sentenceCount
     * @author erelsgl
     */
    public String[] pickRandomAdjacentSentences(int sentenceCount) {
    	int start = ((int)(Math.random()*_fullSentences.size()/2)-sentenceCount+1)*2;
    	StringBuffer sectionWords = new StringBuffer();
    	StringBuffer sectionPOSes = new StringBuffer();
    	for (int i=start; i<start+sentenceCount*2; i+=2) {
    		sectionWords.append(_fullSentences.get(i)).append(' ');
    		sectionPOSes.append(_fullSentences.get(i+1)).append(' ');
    	}
    	return new String[] {
    			sectionWords.toString(),
    			sectionPOSes.toString()
    	};
    }

    /**
     * Creates a random section, based on a section of adjacent or non-adjacent sentences (depends on the second param). 
     * The method choose sentence POS template randomly (with uniform distribution),
     * and generate sentence to this template. 
     * If the length of the template is bigger from 'MINIMAL_TEMPLATE_SIZE_TO_SPLIT' then the sentence 
     * are split into 2 parts, and 2 sentences will be generated. 
     * For the first part from the beginning to the end, and for the second part from the end to the beginning. 
     * Meaning that for the second part we first will choose the last word, 
     * then the word before it, with rely on the last word and so on. 
     * 
     * @param sentenceCount - number of sentences in the section.
     * @param adjacent - whether to start with adjacent or random sentences.
     * 
     * @return the generated sentence
     */
    public GeneratedSentence createRandomSection(int sentenceCount, boolean adjacent) {
    	GeneratedSentence result;
		// choose random sentence POS template and get it's POS as a List

    	/* New code by Erel: */
    	String[] templateSentence = adjacent? pickRandomAdjacentSentences(sentenceCount): pickRandomSentences(sentenceCount);
		String realSentenceOfTemplate = templateSentence[0];
    	List<String> posTemplate = getListFromStrings(templateSentence[1]);
    	System.out.println("  real: "+realSentenceOfTemplate);
    	System.out.println("  poss: "+posTemplate);

	
		// the sub-lists for the split POS template (if needed)
		List<String> firstPartOfPosTemplate = new ArrayList<String>();
		List<String> secondPartOfPosTemplate = new ArrayList<String>();
		// the sub-lists for the split sentence (if needed)
		List<String> firstPartOfRealSentence = new ArrayList<String>();
		List<String> secondPartOfRealSentence = new ArrayList<String>();
		
		if (posTemplate.size() >= MINIMAL_TEMPLATE_SIZE_TO_SPLIT) {
		    if (!_sentenceCreationDirection.equalsIgnoreCase(Enums.START_FROM_END)) {
				firstPartOfPosTemplate.addAll(posTemplate);
		    } 
		    
		    if (!_sentenceCreationDirection.equalsIgnoreCase(Enums.START_FROM_BEGINNING)) {
				secondPartOfPosTemplate.addAll(posTemplate);
				Collections.reverse(secondPartOfPosTemplate);
		    }
		}


		// The result will be here
		String generatedSentence = null;
		
	    Collection<String> posToCopyDirectly = CreatorProps.getPosToCopyDirectly();
		for (int i = 0; i < NUMBER_OF_RETRIES; i++) {
			
		    ImitatorLogger.getInstance().createNewSentence();
			ImitatorLogger.getInstance().addToLastSentence("Real Sentence: " + realSentenceOfTemplate);
			ImitatorLogger.getInstance().addToLastSentence("POS Template: " + posTemplate);
			
		    String firstPartOfsentence = "";
		    String secondPartOfsentence = "";
		    
		    // generate the first part
		    if(!_sentenceCreationDirection.equals(Enums.START_FROM_END)) {
		    	ImitatorLogger.getInstance().addToLastSentence("<<<<****>>>> Generating sentence from the beginning");
		    	firstPartOfRealSentence = getListFromStrings(realSentenceOfTemplate); // TODO: test the randomness (+1) , test the assignment (above)
		    	ImitatorLogger.getInstance().addToLastSentence("<<<<****>>>> first part of real sentence: \"" + firstPartOfRealSentence + "\"");
		    	firstPartOfsentence = _nGramsImpl.generateSentence(firstPartOfPosTemplate, firstPartOfRealSentence, false);
		    	ImitatorLogger.getInstance().addToLastSentence("<<<<****>>>> Generated sentence from the beginning: \"" + firstPartOfsentence + "\"");
		    	if (firstPartOfsentence==null && _sentenceCreationDirection.equalsIgnoreCase(Enums.START_FROM_BOTH)) {
		    		firstPartOfsentence = "";
		    		_sentenceCreationDirection = Enums.START_FROM_END;
		    	}
		    }
	
		    // generate the second part
		    if(!_sentenceCreationDirection.equalsIgnoreCase(Enums.START_FROM_BEGINNING)) {
		    	ImitatorLogger.getInstance().addToLastSentence("<<<<****>>>> Generating sentence from the end");
				secondPartOfRealSentence = getListFromStrings(realSentenceOfTemplate);
				Collections.reverse(secondPartOfRealSentence);
		    	secondPartOfsentence = _nGramsImpl.generateSentence(secondPartOfPosTemplate, secondPartOfRealSentence, true);
		    	secondPartOfsentence = reverseWords(secondPartOfsentence);
		    	ImitatorLogger.getInstance().addToLastSentence("<<<<****>>>> Generated reverse sentence: \"" + secondPartOfsentence + "\"");
		    	if (secondPartOfsentence==null && _sentenceCreationDirection.equalsIgnoreCase(Enums.START_FROM_BOTH)) {
		    		secondPartOfsentence = "";
		    		_sentenceCreationDirection = Enums.START_FROM_BEGINNING;
		    	}
		    }

	    	result = new GeneratedSentence(	firstPartOfPosTemplate+" & "+secondPartOfPosTemplate,
					firstPartOfRealSentence+" & "+secondPartOfRealSentence);
		    
		    // if the 2 generations were not failed (succeed or didn't run) - return the result
		    if (firstPartOfsentence != null && secondPartOfsentence != null) {
		    	// calculate possible merge locations:
		    	List<Integer> possibleMergeLocationsList = new ArrayList<Integer>();
		    	int previousInsertedIndex =-1;
				for (int index=0; index<firstPartOfPosTemplate.size()-1; ++index) {
					String currPOS = firstPartOfPosTemplate.get(index);
					if (!posToCopyDirectly.contains(currPOS)) {
						if (previousInsertedIndex!=index)
							possibleMergeLocationsList.add(index);
						possibleMergeLocationsList.add(index+1);
						previousInsertedIndex = index+1;
					}
				}
				int[] possibleMergeLocations = new int[possibleMergeLocationsList.size()];
				for (int j=0; j<possibleMergeLocationsList.size(); ++j)
					possibleMergeLocations[j]=possibleMergeLocationsList.get(j);
					
		    	
		    	if (_sentenceCreationDirection.equalsIgnoreCase(Enums.START_FROM_BOTH)) {
		    		ImitatorLogger.getInstance().addToLastSentence("<<<<****>>>> Merging: "+firstPartOfsentence+" & "+ secondPartOfsentence);
		    		generatedSentence = _nGramsImpl.mergeTwoSentences(firstPartOfsentence,secondPartOfsentence,possibleMergeLocations);
		    		result.setMergeParts(firstPartOfsentence+" & "+ secondPartOfsentence);
		    		ImitatorLogger.getInstance().addToPreviousSentence("<<<<****>>>> The finally created sentence is: \"" + generatedSentence + "\". Succedded after " + (i + 1) + " times");
		    	} else {
		    		generatedSentence = firstPartOfsentence + secondPartOfsentence;
		    		ImitatorLogger.getInstance().addToLastSentence("<<<<****>>>> The created sentence is: \"" + generatedSentence + "\". Succedded after " + (i + 1) + " times");
		    	}

	    		ImitatorLogger.getInstance().addToLastSentence("<<<<****>>>> The created sentence without spaces is: \"" + removeSpacesBetweenParticles(generatedSentence) + "\". Succedded after " + (i + 1) + " times");
				result.setCreatedSentence(generatedSentence);
				return result;
		    }
		    // if didn't succeed:
		    ImitatorLogger.getInstance().removeLast();
		    totalNumberOfRetries++;
		}
	
		System.out.println(" Giving up after " + NUMBER_OF_RETRIES + " tries...");
		return null;
    }
    

    /**
     * The method selects random word, considering the weight of each word.
     * The algorithm is:
     * 		1. Count the total weight of all the words. Let it be TOTAL_COUNT
     * 		2. Select random number from the range [0, TOTAL_COUNT]. Let it be RANDOM
     * 		3. Run over the words and count the weights in another counter. Let it be COUNTER
     * 		   3.1. Let the weight of the current word be WEIGHT. 
     * 			If in certain word, RADNOM is in the range [COUNTER, COUNTER + WEIGHT] - choose this word.
     *  
     * @param posTemplatesFrequencies - a map that holds the words collection as the keys, 
     * 					and the values are the weight of each word
     * @return the selected word
     */
    public static String getRandomElementByAccuracies(Map<String, Long> posTemplatesFrequencies, boolean print) 
    {
		// choose random number 
		long numOfAcc = countAccuracies(posTemplatesFrequencies);
		long random = (long) (Math.random() * numOfAcc);
	
		long counter = 0,value;
		for (String key : posTemplatesFrequencies.keySet()) {
		    value = (Long) posTemplatesFrequencies.get(key);
	
		    if (random >= counter && random <= (counter + value))
		    {
				if (print)
		    	{
					ImitatorLogger.getInstance().addToLastSentence(":->) The choosen word is: "+key);
					ImitatorLogger.getInstance().addToLastSentence(":->) The probability of the choosen word is: "+(double)((double)value/(double)numOfAcc));
		    	}
		    	return key;
		    }
	
		    counter += value;
		}
	
		// Should not arrive here
		return null;
    }

    /**
     * Change the representation of sentence from String into a List where each word is a separated element.
     * 
     * @param sentenceAsString
     * @return
     */
    public static List<String> getListFromStrings(String sentenceAsString) {
	List<String> sentenceAsList = new ArrayList<String>();
	
	if(sentenceAsString == null || sentenceAsString.equals(""))
	    return sentenceAsList;

	StringTokenizer posTokenizer = new StringTokenizer(sentenceAsString);

	for (String currPOS = posTokenizer.nextToken();; currPOS = posTokenizer.nextToken()) {
	    sentenceAsList.add(currPOS);
	    
	    if(!posTokenizer.hasMoreTokens())
		break;
	}

	return sentenceAsList;
    }

    /**
     * Change the representation of sentence from String into an array of objects.
     * 
     * @param sentenceAsString
     * @return
     */
    public static Object[] getObjArrayFromString(String str) {
	Collection<Object> objCol = new ArrayList<Object>();

	if(str == null || str.equals(""))
	    return objCol.toArray();

	StringTokenizer posTokenizer = new StringTokenizer(str);

	for (String currPOS = posTokenizer.nextToken();; currPOS = posTokenizer.nextToken()) {
	    objCol.add((Object)currPOS);
	    
	    if(!posTokenizer.hasMoreTokens())
		break;
	}

	return objCol.toArray();
    }

    public static Object[] getIntArrayFromString(String str) {
	Collection<Object> objCol = new ArrayList<Object>();

	if(str == null || str.equals(""))
	    return objCol.toArray();

	StringTokenizer posTokenizer = new StringTokenizer(str);

	for (String currPOS = posTokenizer.nextToken();; currPOS = posTokenizer.nextToken()) {
	    objCol.add(Integer.parseInt(currPOS));
	    
	    if(!posTokenizer.hasMoreTokens())
		break;
	}

	return objCol.toArray();
    }


    public static String reverseWords(String key) 
    {
    	if (key == null)
    	{
    		return null;
    	}
    	
		List<String> list = getListFromStrings(key);
		Collections.reverse(list);
		
		StringBuffer sb = new StringBuffer();
		for (String string : list) 
		{
			sb.append(string).append(" ");
		}
		
		return sb.toString();
    }

    /**
     * Receives map<String, Integer> and return the total sum of the Integers 
     * @param posTemplatesFrequencies
     * @return
     */
    public static long countAccuracies(Map<String, Long> posTemplatesFrequencies) 
    {
		long posTemplatesCount = 0;
	
		for (Long localCount : posTemplatesFrequencies.values()) 
		{
		    if (localCount == null)
		    {
		    	continue;
		    }
		    posTemplatesCount += localCount.longValue();
		}

		return posTemplatesCount;
    }

    /**
     * Receives map<String, Integer> and return the total sum of the Integers 
     * @param map
     * @return
     */
    public static long countAccuraciesInt(Map<String, Integer> map) 
    {
		long posTemplatesCount = 0;
	
		if(map == null)
		{
		    return posTemplatesCount;
		}
		
		for (Integer localCount : map.values()) 
		{
		    if (localCount == null)
		    {
		    	continue;
		    }
	
		    posTemplatesCount += localCount.longValue();
		}
	
		return posTemplatesCount;
    }

    /**
     * @param sentece a sentence created by the creator, where the particles are treated as separate words, 
     * such as "אבל ב מידה הגדולה ו הקודמת.."
     * @return a sentence without these spaces.
     * 
     * @author erelsgl
     * @since 2011-07-01
     */
    public static String removeSpacesBetweenParticles(String sentence) {
    	return sentence.replaceAll(ParticleHeyRegexp, "$1").replaceAll(HeyRegexp, "$1").replaceAll(ParticleRegexp, "$1").replaceAll(WawRegexp, "$1").replaceAll(PunctuationRegexp, "$1");
    }
    private static String ParticleHeyRegexp = "\\b(ב|כ|ל)\\b \\b(ה)\\b ";
    private static String ParticleRegexp = "\\b(ש|ב|כ|ל|מ|כש|מש)\\b ";
    private static String WawRegexp = "\\b(ו)\\b ";
    private static String HeyRegexp = "\\b(ה)\\b ";
    private static String PunctuationRegexp = " ([.;,:!?])";

    /**
     * @param sentece a sentence created by the generator, where each replaced word contains the original word with a slash. 
     * @return a sentence without the originals.
     * 
     * @author erelsgl
     * @since 2011-11-23
     */
    public static String removeOriginalWords(String sentence) {
    	return sentence.replaceAll(OriginalAndReplacementRegexp, "$2");
    }
    private static String OriginalAndReplacementRegexp = "\\b([^ ]+)[/]+([^ ]+)\\b";

    
    
    
    public SentenceCreator() {}
    
    public SentenceCreator(String propertiesFileName) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFileName));
		initialize(properties);
    }
    

    public static void main(String[] args) throws FileNotFoundException, IOException {
//    	String propertiesFile = "corpora/Rambam/SentenceCreator.properties";
//    	String propertiesFile = "corpora/Herzl/SentenceCreator.properties";
    	String propertiesFile = "corpora/ShmonaKvazim/SentenceCreator.properties";

		new SentenceCreator(propertiesFile).createRandomSection(1, true);
    }
}
