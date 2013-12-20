package Imitator.common;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

@SuppressWarnings({"rawtypes","unchecked"}) 
public class FileUtils {
	
	private static String InputFileEncoding = "UTF8"; // or: "Cp1255"
	public static void setInputFileEncoding(String newInputFileEncoding) {
		InputFileEncoding = newInputFileEncoding;
	}
	public static BufferedReader newBufferedReader(File file) throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file), InputFileEncoding));
	}
	
    private static final int EDGE = 100;
    private static int ReadCounter = 0;
    
    private static final String NEW_TEMPLATE_SIGN = "<new>";

    public static int getReadCounter() 
    {
        return ReadCounter;
    }

    public static void setReadCounter(int readCounter) 
    {
        ReadCounter = readCounter;
    }

    public static String getFileNameNoExtension(String fileName) 
    {
    	return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * 
     * @param corpusName
     * @param templates
     * @param baseDir
     */
	public static void writePOSTemplatesMapToFile(String corpusDir, Map templates) 
    {
		File posTemplatesFile = getPosTemplatesFile(corpusDir);
	
		try {
		    writeMapIntoFile(templates, posTemplatesFile);
	
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }

    /**
     * Writes map into a file with the next format:
     * 		Key1 Value1
     * 		Key2 Value2
     * 		...
     * @param map - the map to write into the file
     * @param file - the destination file
     * @throws IOException
     */
    private static void writeMapIntoFile(Map<String,?> map, File file) throws IOException 
    {
    	boolean append=false;
    	FileWriter writer = new FileWriter(file,append);
	
		for (String key : map.keySet())
		    writer.write(key + " " + map.get(key) + "\n");
	
		writer.close();
    }

    /**
     * Writes collection into a file, each element in new line
     * @param elements - the collection to write
     * @param file - the destination file
     * @throws IOException
     */
    public static void writeFile(Collection<String> elements, String file)
    {
		try {
			//maybe append? not sure
		    FileWriter writer = new FileWriter(file);
	
		    for (String element : elements)
			writer.write(element + "\n");
	
		    writer.close();
	
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }



    public static void writeRealSentencesFile(Map<String, Collection<String>> template2RealSentences, String corpusDir)    {
		try {
			//maybe append?
		    FileWriter writer = new FileWriter(getRealSentencesOfTemplateFile(corpusDir),true);
	
		    for (String template : template2RealSentences.keySet()) {
				Collection<String> realSentences = template2RealSentences.get(template);
				
				writer.write(NEW_TEMPLATE_SIGN + template + "\n");
				
				for (String realSentence : realSentences) {
				    writer.write(realSentence + "\n");
				}
		    }
		    
		    writer.close();
	
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }

    public static void writeFullSentencesFile(Collection<String> sentences, String corpusDir) {
    	final int MAX_LINES_PER_FILE = 10000;
		try {
			int currentFileIndex = 0;
		    int numLinesInCurrentFile = 0;
		    FileWriter writer = new FileWriter(getFullSentencesOfTemplateFile(corpusDir, currentFileIndex), /*append=*/false);
		    for (String sentence: sentences) {
		    	if (numLinesInCurrentFile >= MAX_LINES_PER_FILE) {
		    		writer.close();
		    		numLinesInCurrentFile = 0;
		    		++currentFileIndex;
		    		writer = new FileWriter(getFullSentencesOfTemplateFile(corpusDir, currentFileIndex), /*append=*/false);
		    	}
		    	writer.write(sentence + "\n");
		    	++numLinesInCurrentFile;
		    }
		    writer.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }


    /**
     * The methods reads the POS2words given file.
     * The file should be with the next format:
     * 		<wordA> <numberA>
     * 		<wordB> <numberB>
     * 		........
     * @param posFile - the file to read
     * @return map that contains the words as keys and the numbers as values
     * @throws IOException
     */
    public static Map<String, Long> readPOS2WordsFile(File posFile)  {
		Map<String, Long> word2Count = new HashMap<String, Long>();
		if (!posFile.exists())
		    return word2Count;
		try {
		    BufferedReader reader = newBufferedReader(posFile);
		    for (String currLine = reader.readLine(); currLine != null; currLine = reader.readLine()) {
	
			StringTokenizer lineTokenizer = new StringTokenizer(currLine);
	
			String word = lineTokenizer.nextToken(" ");

			long count;
			try {
			    count = Integer.parseInt(lineTokenizer.nextToken(" "));
			    //if (count==24) System.out.println(word+" "+count);
			} catch (NoSuchElementException e) {
				System.err.println("No count for word '"+word+"'");
				throw e;
			} catch (NumberFormatException e) {
			    // System.out.println("NumberFormatException! " + e.getMessage());
			    // count = Integer.parseInt(lineTokenizer.nextToken(" "));
			    // skipping
			    continue;
			}
	
			word2Count.put(word, count);
	
		    }
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	
		return word2Count;
    }
    
    public static File getPosTemplatesFile(String corpusDir) {
	return new File(corpusDir + "/PosTemplates.txt");
    }

    public static String getPosDirectory(String corpusDir) {
	return corpusDir + "/POS/";
    }

    public static String getPosFile(String corpusDir, String pos) {
	return getPosDirectory(corpusDir) + pos + ".txt";
    }

    public static String getTripplesDirectory(String corpusDir) {
	return corpusDir + "/Word/"; // +
    }

    public static String getTripplesFile(String corpusDir, String word) {
	return getTripplesDirectory(corpusDir) + word + ".txt";
    }

    public static String getReverseTripplesDirectory(String corpusDir) {
	return corpusDir + "/ReverseWord/"; // +
    }

    public static String getReverseTripplesFile(String corpusDir, String word) {
	return getReverseTripplesDirectory(corpusDir) + word + ".txt";
    }
    

    /**
     * Reads the POS templates file.
    * The file should be with the next format:
     * 		<sentenceA> <numberA>
     * 		<sentenceB> <numberB>
     * 		........
     * @param posTemplatesFile - the file to read
     * @return map that containts the sentences as keys, and the numbers as values
     */
    public static Map<String, Long> readPosTemplatesFile(File posTemplatesFile)
    {
		Map<String, Long> posTemplates2Count = new HashMap<String, Long>();
	
		try {
		    if (!posTemplatesFile.exists()) {
			
			posTemplatesFile.createNewFile();
			return posTemplates2Count;
		    
		    } else {
			
			BufferedReader reader = newBufferedReader(posTemplatesFile);
			
			for (String currLine = reader.readLine(); currLine != null; currLine = reader.readLine()) {
	
			    StringTokenizer lineTokenizer = new StringTokenizer(currLine);
	
			    Long count = null;
			    StringBuffer posSentence = new StringBuffer(lineTokenizer.nextToken(" "));
	
			    while (lineTokenizer.hasMoreElements()) {
				String word = lineTokenizer.nextToken(" ");
	
				if (lineTokenizer.hasMoreElements())
				    posSentence.append(" ").append(word);
				else {
				    try {
					count = Long.parseLong(word);
				    } catch (NumberFormatException e) {
					System.out.println("NumberFormatException! " + e.getMessage());
	
					// skipping
					continue;
				    }
				}
			    }
	
			    posTemplates2Count.put(posSentence.toString(), count);
			}
		    }
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	
		return posTemplates2Count;
    }

    /**
     * Reads given file into collection of strings, each line as an element
     * @param fileName
     * @return a list with the file's lines as elements
     */
    public static List<String> readFile(String fileName) 
    {
		List<String> fileElements = new ArrayList<String>();
	
		File file = new File(fileName);
	
		if (!file.exists())
		    return fileElements;
	
		try {
		    BufferedReader reader = newBufferedReader(new File(fileName));

		    for (String currLine = reader.readLine(); currLine != null; currLine = reader.readLine()) {
			fileElements.add(currLine);
	
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
	
		return fileElements;
    }
    
   /**
     * @param baseDir
     * @param corpusName
     * @return
     */
    public static Map<String, List<String>> readRealSentencesFile(String corpusDir)   {
		Map<String, List<String>> realSentencesMap = new HashMap<String, List<String>>();
		
		File realSentencesFile = new File(getRealSentencesOfTemplateFile(corpusDir)); 
		
		if (!realSentencesFile.exists())
		    return realSentencesMap;
	
		try {
		    BufferedReader reader = newBufferedReader(realSentencesFile);
	
		    String template = null;
		    List<String> realSentences = null; 
		    
		    // each line can be template or real sentence of the current template
		    for (String currLine = reader.readLine(); currLine != null; currLine = reader.readLine()) {
			
			if(currLine.startsWith(NEW_TEMPLATE_SIGN)){ //the line is template
			    
			    if(template != null) // not the first iteration
				realSentencesMap.put(template, realSentences);
				
			    template = currLine.substring(NEW_TEMPLATE_SIGN.length());
			    realSentences = new ArrayList<String>();
			
			} else { //the line is real sentence of the current template
			
			    realSentences.add(currLine);
			}
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return realSentencesMap;
    }

    
    public static List<String> readFullSentencesFile(String corpusDir)   {
		List<String> fullSentences = new ArrayList<String>();
		
		for (int currentFileIndex = 0;; ++currentFileIndex) {
			File fullSentencesFile = new File(getFullSentencesOfTemplateFile(corpusDir, currentFileIndex));
			boolean fileExists = false;
			try {
				fileExists = fullSentencesFile.exists();
			} catch (java.security.AccessControlException ex) {	}
			if (!fileExists) {
				if (fullSentences.isEmpty())
					throw new RuntimeException("No fullSentences file found - "+fullSentencesFile);
			    return fullSentences;
			}
			try {
			    BufferedReader reader = newBufferedReader(fullSentencesFile);
			    // each line can be template or real sentence of the current template
			    for (String currLine = reader.readLine(); currLine != null; currLine = reader.readLine()) {
			    	fullSentences.add(currLine);
			    }
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
    }
    
    public static Map<String,Integer> readBigramCountsFile(String corpusDir)   {
    	Map<String,Integer> bigramCounts = new HashMap<String,Integer>();
		File bigramCountsFile = new File(getBigramCountsFile(corpusDir)); 
		if (!bigramCountsFile.exists())
		    return bigramCounts;
		try {
		    BufferedReader reader = newBufferedReader(bigramCountsFile);
	
		    // each line can be template or real sentence of the current template
		    for (String currLine = reader.readLine(); currLine != null; currLine = reader.readLine()) {
		    	String[] parts = currLine.split("~~~");
		    	if (parts.length<2) {
		    		System.err.println("Bad line format: "+currLine);
		    		continue;
		    	}
		    	bigramCounts.put(parts[0],Integer.valueOf(parts[1]));
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return bigramCounts;
    }
    
    /**
     * 
     * @param population
     * @param pos2Words
     * @param baseDir
     */
    public static void writePos2WordMapToFiles(String corpusDir, Map<String, Map<String,Integer>> pos2Words)   {
		String directoryPath = getPosDirectory(corpusDir);
		File directory = new File(directoryPath);
		if (!directory.exists())
		    directory.mkdir();
	
		for (String pos : pos2Words.keySet()) {
		    Map<String, Integer> probMap = pos2Words.get(pos);

		    try {
		    	File file = new File(getPosFile(corpusDir, pos));
		    	System.out.println("\t Writing POS file "+file.getAbsolutePath());
		    	writeMapIntoFile(probMap, new File(getPosFile(corpusDir, pos)));
		    } catch (IOException e) {
			System.err.println(e.getMessage());
			System.err.println("skipping that file!");
			continue;
		    }
		}
    }

    /**
     * 
     * @param population
     * @param words
     * @param baseDir
     */
    public static void writeTripplesMapToFiles(String corpusDir, Map<String, Map> words)    {
		String directoryPath = getTripplesDirectory(corpusDir);
		writeTripplesMapToFiles(words, directoryPath);
    }

    public static void writeBigramCountsToFile(Map<String, Integer> bigramCounts, String corpusDir) {
	    try {
		    FileWriter writer = new FileWriter(getBigramCountsFile(corpusDir),/*append=*/false);
		    for (Map.Entry<String,Integer> entry: bigramCounts.entrySet()) {
		    	writer.write(entry.getKey()+"~~~"+entry.getValue()+"\n");
		    }
		    writer.close();
	    } catch(IOException ex) {
	    	ex.printStackTrace();
	    }
    }
    
    
    /**
     * 
     * @param corpusName
     * @param words
     * @param baseDir
     */
    public static void writeReverseTripplesMapToFiles(String corpusDir, Map<String, Map> words)    {
		String directoryPath = getReverseTripplesDirectory(corpusDir);
		writeTripplesMapToFiles(words, directoryPath);
	}

    /**
     * 
     * @param words
     * @param directoryPath
     */
    private static void writeTripplesMapToFiles(Map<String, Map> words, String directoryPath)   {
		File directory = new File(directoryPath);
		if (!directory.exists())
		    directory.mkdir();
	
		try {
			//maybe append?
		    FileWriter writer = new FileWriter(directoryPath + "/Sequences.txt");
		    for (String word1 : words.keySet()) {
			Map<String, Map> innerMap = words.get(word1);
	
			writeSingleTrippleFile(writer, innerMap, directoryPath, word1);
		    }
		    writer.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
   }

    /**
     * 
     * @param secondMap
     * @param directoryPath
     * @param word1
     */
    public static void writeSingleTrippleFile(FileWriter writer, Map<String, Map> innerMap, String directoryPath, String word1)   {
		try {
	
		    for (String word2 : innerMap.keySet()) {
			Map<String, Integer> probMap = innerMap.get(word2);
	
			for (String word3 : probMap.keySet()) {
	
			    writer.write(word1 + " " + word2 + " " + word3 + " " + probMap.get(word3) + "\n");
			}
		    }
		} catch (IOException e) {
		    System.err.println(e.getMessage());
		    return;
		}
	
    }
    
    /**
     * 
     * @param words
     * @param directoryPath
     */
    private static void writeAndClearMap(Map<String, Map> words, String directoryPath)   {
		File directory = new File(directoryPath);
	
		if (!directory.exists())
		    directory.mkdir();
	
		int totalCounter = 0;
		int removedCounter = 0;
		List<String> removedWords = new ArrayList<String>();
		
		for (String word1 : words.keySet()) 
		{
	
		    if (word1.equalsIgnoreCase("Con"))
			continue;
	
		    Map<String, Map> secondMap = words.get(word1);
	
		    totalCounter++;
		    long numberOfAccurancies = countAccurancies(secondMap);
		    if (numberOfAccurancies > EDGE)
			continue;
	
		    removedCounter++;
	
		    try 
		    {
		    	//maybe append?
				FileWriter writer = new FileWriter(directoryPath + word1 + ".txt");
		
				for (String word2 : secondMap.keySet()) {
				    Map<String, Integer> thirdMap = secondMap.get(word2);
		
				    for (String word3 : thirdMap.keySet()) {
		
					writer.write(word2 + " " + word3 + " " + thirdMap.get(word3) + "\n");
				    }
				}
		
				writer.close();
				secondMap.clear();
				removedWords.add(word1);
		
		    } catch (IOException e) {
			System.err.println(e.getMessage());
			System.err.println("skipping that file!");
			removedWords.add(word1);
			continue;
		    }
		}
	
		for (String removedWord : removedWords)
		    words.remove(removedWord);
	
		System.out.println("## Finish writing " + removedCounter + " files from " + totalCounter);
		System.out.println("## Number of file that was loaded from files in the last chunk is " + getReadCounter());
		
		setReadCounter(0);
	
    }

    /**
     * 
     * @param population
     * @param words
     * @param baseDir
     */
    public static void writeAndClearMap(String corpusDir, Map<String, Map> words)   {
		String directoryPath = getTripplesDirectory(corpusDir);
		writeAndClearMap(words, directoryPath);
    }

    public static void writeAndClearMapReverse(String corpusDir, Map<String, Map> words)   {
		String directoryPath = getReverseTripplesDirectory(corpusDir);
		writeAndClearMap(words, directoryPath);
    }

    /**
     * 
     * @param secondMap
     * @return
     */
    private static long countAccurancies(Map<String, Map> secondMap)  {
		long counter = 0;
		for(String word2 : secondMap.keySet()){
		    
		    Map<String, Integer> thirdMap = secondMap.get(word2);
		    for(String word3 : thirdMap.keySet())
			counter += thirdMap.get(word3);
		}
		
		return counter;
    }

    /**
     * 
     * @param baseDir
     * @param corpusName
     * @param word
     * @return
     */
    public static Map<String, Map> readTripplesFile(String corpusDir, String word)   {
		File wordFile = new File(FileUtils.getTripplesFile(corpusDir, word));
		return readTripplesFile(wordFile);
    }

    public static Map<String, Map> readTripplesFileNew(String corpusDir) 
    {
		
		File file = new File(corpusDir + "/Word/sequences.txt");
		
		Map<String, Map> outerMap = new HashMap<String, Map>();
		
		if (!file.exists()){
		    return outerMap;
		}
		
		try {
		    BufferedReader reader = newBufferedReader(file);
		    for (String currLine = reader.readLine(); currLine != null; currLine = reader.readLine()) {
	
			StringTokenizer lineTokenizer = new StringTokenizer(currLine);
	
			String word1 = lineTokenizer.nextToken(" ");
			String word2 = lineTokenizer.nextToken(" ");
			String word3 = lineTokenizer.nextToken(" ");
			Integer count;
			try{
			    count = Integer.parseInt(lineTokenizer.nextToken(" "));
			} catch (Exception e) {
			    // may arrive here several times
			    e.printStackTrace();
			    continue;
			}
			
			// innerMap may be already exist here
			Map<String, Map> innerMap = outerMap.get(word1);
			if(innerMap == null)
			    innerMap = new HashMap<String, Map>();
	
			// probMap may be already exist here
			Map<String, Integer> probMap = innerMap.get(word2);
			if(probMap == null)
			    probMap = new HashMap<String, Integer>();
			    
			probMap.put(word3, count);
			innerMap.put(word2, probMap);
			outerMap.put(word1, innerMap);
			    
		    }
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	
		return outerMap;
	
    }

    /**
     * 
     * @param baseDir
     * @param corpusName
     * @param word
     * @return
     */
    public static Map<String, Map> readReverseTripplesFile(String corpusDir, String word) 
    {
	
		File wordFile = new File(FileUtils.getReverseTripplesFile(corpusDir, word));
		
		return readTripplesFile(wordFile);
    }

    /**
     * Read file with data of 3-grams that start with specific word.
     * The data is read into a Map<String, Map> that called innerMap. 
     * innerMap maps from the second word of the sequence to another Map that contain the
     * third word, and the number of appearances of the 3-gram.
     * @param wordFile the file to read
     * @return innerMap with the data that was read from the file
     * 		or empty Map if the file does not exist
     */
    private static Map<String, Map> readTripplesFile(File wordFile) 
    {
		Map<String, Map> innerMap = new HashMap<String, Map>();
		
		if (!wordFile.exists()){
		    return innerMap;
		}
	
		ReadCounter++;
		try {
		    BufferedReader reader = newBufferedReader(wordFile);
		    for (String currLine = reader.readLine(); currLine != null; currLine = reader.readLine()) {
	
			StringTokenizer lineTokenizer = new StringTokenizer(currLine);
	
			String word2 = lineTokenizer.nextToken(" ");
			String word3 = lineTokenizer.nextToken(" ");
			Integer count;
			try{
			    count = Integer.parseInt(lineTokenizer.nextToken(" "));
			} catch (Exception e) {
			    // may arrive here several times
			    continue;
			}
			
			// probMap may be already exist here
			Map<String, Integer> probMap = innerMap.get(word2);
			if(probMap == null)
			    probMap = new HashMap<String, Integer>();
			    
			probMap.put(word3, count);
			innerMap.put(word2, probMap);
			    
		    }
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	
		return innerMap;
    }

    /**
     * 
     * @param file
     */
    public static void cleanDirectory(File file) 
    {
		file.delete();
		file.mkdir();
    }

    public static String getRealSentencesOfTemplateDirectory(String corpusDir) 
    {
    	return corpusDir + "/realSentencesOftemplates/";
    }

    public static String getRealSentencesOfTemplateFile(String corpusDir, String posTemplate) {
    	return getRealSentencesOfTemplateDirectory(corpusDir) + posTemplate + ".txt";
    }
    
    public static String getRealSentencesOfTemplateFile(String corpusDir) {
    	return corpusDir + "/realSentences.txt";
    }
    
    public static String getFullSentencesOfTemplateFile(String corpusDir, int index) {
    	return corpusDir + "/fullSentences"+index+".txt";
    }

    public static String getBigramCountsFile(String corpusDir) {
    	return corpusDir + "/bigrams.txt";
    }

   public static String getCorpusWordsFile(String corpusDir) {
    	return corpusDir + "/corpusWords.txt";
    }
    
    public static String getReverseCorpusWordsFile(String corpusDir) {
    	return corpusDir + "/reverseCorpusWords.txt";
    }

    public static String getSuffixArrayFile(String corpusDir) {
    	return corpusDir + "/suffixes.txt";
    }

    public static String getReverseSuffixArrayFile(String corpusDir) {
    	return corpusDir + "/reverseSuffixes.txt";
    }
}
