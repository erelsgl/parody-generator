package Imitator.mainTasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import Imitator.common.*;
import Imitator.measure.*;

public class MeasureFrequencies {

    // map from sequence of 2 words to its preceding words
    private static Map<String, Map<String,Integer>> map = new HashMap<String, Map<String,Integer>>();

    private static String _corpusName;

    public static Timer _timer = new Timer();

    public static void run(Properties properties) {
		_corpusName = properties.getProperty("corpus.name");
		String inputDirName = properties.getProperty("input.directory"); // parsed corpus
		File inputDir = new File(inputDirName);
		if (!inputDir.isDirectory()) {
		    System.err.println(" ## the corpus path "+inputDirName+" is not a valid directory !!");
		    return;
		}
		
		String outputDirName = properties.getProperty("output.directory"); // parsed corpus measures
		File outputDir = new File(outputDirName);
		if (!outputDir.exists())
			outputDir.mkdirs();
	
		initProperties(properties, outputDirName);
		
		/***************************************/
		/**** Measure frequencies of corpus ****/
		/***************************************/
		
		_timer.startClock();
		
		// measure n-grams forward
		if (Boolean.parseBoolean(properties.getProperty("should.measure.sequence.forward"))) {
		    MeasureStrategy suffixArrayStrategy = new SuffixArrayStrategy();
		    measure(inputDir, suffixArrayStrategy);
		    suffixArrayStrategy.destroy();
		}
		
		// measure n-grams backward
		if (Boolean.parseBoolean(properties.getProperty("should.measure.sequence.backward"))) {
		    MeasureStrategy reverseSuffixArrayStrategy = new ReverseSuffixArrayStrategy();
		    measure(inputDir, reverseSuffixArrayStrategy);
		    reverseSuffixArrayStrategy.destroy();
		}
		
		// measure POS/word frequencies
		if (Boolean.parseBoolean(properties.getProperty("should.measure.pos2words"))) {
		    MeasureStrategy measurePOSStrategy = new measurePOSStrategy();
		    measure(inputDir, measurePOSStrategy);
		    measurePOSStrategy.destroy();
		}

		//measure POS templates frequencies
		if (Boolean.parseBoolean(properties.getProperty("should.measure.pos.templates"))) {
		    MeasureStrategy measurePOSTemplatesStrategy = new MeasurePOSTemplatesStrategy();
		    measure(inputDir, measurePOSTemplatesStrategy);
		    measurePOSTemplatesStrategy.destroy();
		}

		//measure POS templates frequencies
		if (Boolean.parseBoolean(properties.getProperty("should.create.full.sentences"))) {
		    MeasureStrategy measureFullSentencesStrategy = new MeasureFullSentencesStrategy();
		    measure(inputDir, measureFullSentencesStrategy);
		    measureFullSentencesStrategy.destroy();
		}

		_timer.finish();
    }

    /**
     * Init the properties static class. 
     * The class is in use in the measure strategies.
     * @param properties - the properties file
     * @param baseDir
     */
    private static void initProperties(Properties properties, String corpusDir) {
		MeasureProps.setCorpusDir(corpusDir);
		MeasureProps.setCorpusName(_corpusName);
		MeasureProps.setMinimalTemplateLength(Integer.parseInt(properties.getProperty("minimal.template.length")));
		MeasureProps.setMinimalTemplateFrequency(Integer.parseInt(properties.getProperty("minimal.template.frequency")));
		MeasureProps.setMinimalPOSFrequency(Integer.parseInt(properties.getProperty("minimal.POS.frequency")));
		//MeasureProps.setMinimalSequenceFrequency(Integer.parseInt(properties.getProperty("minimal.sequence.frequency")));
		MeasureProps.setNumberOfWordsToCompareInSuffixArray(Integer.parseInt(properties.getProperty("number.of.words.to.compare.in.suffix.array")));
    }

    /**
     * 
     * @param parsedCorpusDir
     * @param filteredparsedCorpusDir
     */
    public static void measure(File parsedCorpusDir, MeasureStrategy strategy) {

		long numberOfSentences = 0;
	
		File[] corpusFiles = parsedCorpusDir.listFiles();
	
		// run over all the corpus files and measure frequencies
		TaggedCorpusParserIfc parsedCorpusParser = ParserFactory.createParser();
		//	DOMParser parser = new DOMParser();
	
		System.out.println("  ==============================================================");
		System.out.println("       " + strategy.getMessage() + " of the corpus \"" + _corpusName + "\"");
		System.out.println("  ==============================================================");
	
	//	System.out.println(strategy.getClass().getName());
		
		int corpusSize = corpusFiles.length;
		
		
		for (int fileIndex = 1; fileIndex <= corpusSize; fileIndex++) {
		    File currentFile = corpusFiles[fileIndex - 1];
	
		    log(fileIndex, corpusSize);
	
		    parsedCorpusParser.startFile(currentFile.getAbsolutePath());
	
		    // Iterate over all the sentences of the current file
		    while (parsedCorpusParser.hasMoreSentences()) {
		    	parsedCorpusParser.getNextSentence();
		    	strategy.handleSentence(map, parsedCorpusParser);
		    	numberOfSentences++;
		    }
		    parsedCorpusParser.finishFile();
		}

		// Writes the data into files and clear the maps
		System.out.println(" >> Finish working on " + _corpusName + "! Writes into files...");
		strategy.writeToFiles(map);
		map.clear();
	
	
		// treat sentence and word counters
		System.out.println(" >> The number of sentences in the corpus was " + numberOfSentences);
    }
	
	private static void log(int fileIndex, int corpusSize) 
	{
		
		if(_timer.hasEnoughTimePassed()){
	
		    System.out.println("Finished processing [" + (fileIndex - 1) + "\\" + corpusSize + "] files in " 
			    + _timer.getFormattedTimeFromBegin());
		}
    }
    
    public static void logTime()
    {
    	System.out.println("Time passed: " + _timer.getFormattedTimeFromBegin());
    }

    /** ************************************************************************ */
    /** Private methods which checks the population of certain file, by its name */
    /** ************************************************************************ */

    public static PopulationEnum getRelevantPopulation(File currentFile) 
    {
		PopulationEnum population;
		if (isMale(currentFile)) {
		    if (is10s(currentFile))
			population = PopulationEnum.MALE_10s;
		    else if (is20s(currentFile))
			population = PopulationEnum.MALE_20s;
		    else
			population = PopulationEnum.MALE_30s;
		} else {
		    if (is10s(currentFile))
			population = PopulationEnum.FEMALE_10s;
		    else if (is20s(currentFile))
			population = PopulationEnum.FEMALE_20s;
		    else
			population = PopulationEnum.FEMALE_30s;
		}
		return population;
    }

    private static boolean isMale(File file)
    {
		if (file.getName().contains(".male."))
		    return true;
		return false;
    }

    private static boolean is20s(File file) 
    {
		if (file.getName().contains(".2"))
		    return true;
		return false;
    }

    private static boolean is10s(File file)
    {
		if (file.getName().contains(".1"))
		    return true;
		return false;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
    	String props = "corpora/Rambam/MeasureFrequencies.properties";
//    	String props = "corpora/ShmonaKvazim/MeasureFrequencies.properties";
//    	String props = "corpora/Herzl/MeasureFrequencies.properties";

		// Read properties file.
		Properties properties = new Properties();
		properties.clear();
		properties.load(new FileInputStream(props));
		run(properties);
    }
}
