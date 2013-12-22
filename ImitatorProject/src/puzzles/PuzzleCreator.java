package puzzles;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import Imitator.common.Enums;
import Imitator.mainTasks.SentenceCreator;
import dataStructures.GeneratedSentence;
import dataStructures.ImitatorLogger;

/**
 * Create puzzles with several fake sentences and one real sentence.
 * 
 * @author Erel Segal Halevi
 * @since 2011
 */
public class PuzzleCreator {
	private static final Logger LOG = Logger.getLogger(PuzzleCreator.class.getName());
	
	String corporaFolder;
	public PuzzleCreator(String corporaFolder) {
		LOG.info("create ("+corporaFolder+")");
		this.corporaFolder = corporaFolder;
	}
	
	// Must be static because GAE starts several instances of the class!
	private static Map<String,String> logs = new HashMap<String,String>(); 
//	DatastoreService datastore = null;
//	ScoreService scores = null;
	
	Map<String, SentenceCreator> mapCorpusNameToSentenceCreator = new HashMap<String, SentenceCreator>();

	public SentenceCreator getSentenceCreator(String corpus)  {
		String id = corpus+"-"+"sentence-creator";
		SentenceCreator sentenceCreator = mapCorpusNameToSentenceCreator.get(id);
		if (sentenceCreator==null) {
			String propertiesFile = corporaFolder+File.separator+corpus+File.separator+"SentenceCreator.properties";
			try {
					mapCorpusNameToSentenceCreator.put(corpus, 
						sentenceCreator = new SentenceCreator(propertiesFile));
			} catch (IOException ex) {
					throw new RuntimeException("Corpus '"+corpus+"' could not be initialized from properties file '"+propertiesFile+"': "+ex,ex);
			}
			LOG.info("Corpus '"+corpus+"' initialized with properties file "+propertiesFile);
		} else {
			LOG.info("Corpus '"+corpus+"' already initialized");
		}
		return sentenceCreator;
	}

	
	
	
	/**
	 * @param sentence one of the created sentences.
	 * @param log a detailed report, for developers or managers.
	 * @param reply a short reply, for the user.
	 * @param isCorrect is this a real sentence?
	 */
	private ChoiceAndExplanation addExplanation(String sentence, String reply, String explanation, String log, boolean isCorrect) {
		String logTruncated = (log.length()>100000? log.substring(0, 100000): log);
		logs.put(sentence, logTruncated); // don't need all the log
	
		return new ChoiceAndExplanation(sentence, reply, explanation, isCorrect);
	}
	/*
	private String getLogFromDatastore(String first3words) {
		Key key = KeyFactory.createKey("Explanation", first3words);		
		try {
			Entity explanationEntity = datastore.get(key);
			return ((Text)(explanationEntity.getProperty("log"))).getValue();
		} catch (EntityNotFoundException e) {
			return null;
		}
	}
	
	private String getReplyFromDatastore(String first3words) {
		Key key = KeyFactory.createKey("Explanation", first3words);		
		try {
			Entity explanationEntity = datastore.get(key);
			String reply = (String)explanationEntity.getProperty("reply");
			if (reply.contains("נכון"))
				scores.incrementNumCorrect(getUser());
			else 
				scores.incrementNumWrong(getUser());
			return reply;
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	public String getLog(String sentenceKey) {
		for (Map.Entry<String,String> entry: logs.entrySet()) {
			if (entry.getKey().contains(sentenceKey)) 
				return entry.getValue();
		}
		String explanation = getLogFromDatastore(sentenceKey);
		if (explanation!=null)
			return explanation;
		return "I couldn't find any explanation that matches '"+sentenceKey+"'.";
	}

	public String getReply(String sentenceKey) {
		String reply = getReplyFromDatastore(sentenceKey);
		if (reply!=null)
			return reply;
		return "בעיה - מפתח לא נמצא: '"+sentenceKey+"'.";
	}
	*/

	/*
	 * SENTENCES
	 */
	protected ChoiceAndExplanation pickRandomSection(SentenceCreator sentenceCreator, int numSentences) throws IllegalArgumentException {
		String sentence = SentenceCreator.removeSpacesBetweenParticles(sentenceCreator.pickRandomAdjacentSentences(numSentences)[0]);
		String reply = "נכון מאד - הקטע אמיתי";
		//String explanation = "This is a real section - "+numSentences+" adjacent sentences.";
		String explanation = "קטע המורכב מ-" +numSentences+" משפטים סמוכים: <br/>"+sentence+"";
		return addExplanation(sentence, reply, explanation, explanation, true);
	}

	protected ChoiceAndExplanation pickRandomSentences(SentenceCreator sentenceCreator, int numSentences) throws IllegalArgumentException {
		String sentence = SentenceCreator.removeSpacesBetweenParticles(sentenceCreator.pickRandomSentences(numSentences)[0]);
		String reply = "טעות - הקטע נוצר על-ידי בחירת משפטים בסדר אקראי";
		//String explanation = "This is a collection of "+numSentences+" non-adjacent real sentences chosen randomly.";
		String explanation = "אוסף של " +numSentences+" משפטים שנבחרו בסדר אקראי: <br/>"+sentence+"";
		return addExplanation(sentence, reply, explanation, explanation, false);
	}

	protected ChoiceAndExplanation createRandomSection(SentenceCreator sentenceCreator, int numSentencesPerSection, String direction) throws IllegalArgumentException {
		sentenceCreator.setSentenceCreationDirection(direction);
		String sentence, explanation, reply;
	    GeneratedSentence generatedSentence = sentenceCreator.createRandomSection(numSentencesPerSection, /*adjacent=*/true);
	    if (generatedSentence==null) {
	    	sentence = SentenceCreator.removeSpacesBetweenParticles(sentenceCreator.pickRandomSentences(numSentencesPerSection)[0]);
	    	//explanation = "I failed in creating a random sentence, so I created a collection of "+numSentencesPerSection+" non-adjacent real sentences chosen randomly.";
			explanation = "לא הצלחתי ליצור קטע אקראי, אז יצרתי אוסף של " +numSentencesPerSection+" משפטים שנבחרו בסדר אקראי: <br/>"+sentence+"";
	    	reply = "טעות - הקטע נוצר על-ידי בחירת משפטים בסדר אקראי";
	    } else {
	    	String sentenceWithOriginals = generatedSentence.getCreatedSentence();
	    	sentence = SentenceCreator.removeSpacesBetweenParticles(SentenceCreator.removeOriginalWords(sentenceWithOriginals));
	    	//explanation = "This is a random section, "+direction+", based on a section of "+numSentencesPerSection+" adjacent sentences.\n"+ImitatorLogger.getInstance().getLogForLastSentence();
			explanation = "קטע שנוצר על-ידי בחירת משפטים רצופים והחלפת מילים אקראית: <br/>"+sentenceWithOriginals+"";
	    	reply = "טעות - הקטע נוצר על-ידי בחירת משפטים רצופים והחלפת מילים אקראית";
	    }
		return addExplanation(sentence, reply, explanation, ImitatorLogger.getInstance().getLogForLastSentence(), false);
	}
	
	protected ChoiceAndExplanation createRandomSentences(SentenceCreator sentenceCreator, int numSentencesPerSection, String direction) throws IllegalArgumentException {
		sentenceCreator.setSentenceCreationDirection(direction);
		String sentence, explanation, reply;
	    GeneratedSentence generatedSentence = sentenceCreator.createRandomSection(numSentencesPerSection, /*adjacent=*/false);
	    if (generatedSentence==null) {
	    	sentence = SentenceCreator.removeSpacesBetweenParticles(sentenceCreator.pickRandomSentences(numSentencesPerSection)[0]);
	    	//explanation = "I failed in creating random sentences, so I created a collection of "+numSentencesPerSection+" non-adjacent real sentences chosen randomly.";
			explanation = "לא הצלחתי ליצור קטע אקראי, אז יצרתי אוסף של " +numSentencesPerSection+" משפטים שנבחרו בסדר אקראי: <br/>"+sentence+"";
	    	reply = "טעות - הקטע נוצר על-ידי בחירת משפטים בסדר אקראי";
	    } else {
	    	String sentenceWithOriginals = generatedSentence.getCreatedSentence();
	    	sentence = SentenceCreator.removeSpacesBetweenParticles(SentenceCreator.removeOriginalWords(sentenceWithOriginals));
	    	//explanation = "This is a random section, "+direction+", based on a collection of "+numSentencesPerSection+" random sentences.\n"+ImitatorLogger.getInstance().getLogForLastSentence();
			explanation = "קטע שנוצר על-ידי בחירת משפטים בסדר אקראי והחלפת מילים אקראית:<br/>"+sentenceWithOriginals+"";
 	    	reply = "טעות - הקטע נוצר על-ידי בחירת משפטים בסדר אקראי והחלפת מילים אקראית";
	    }
		return addExplanation(sentence, reply, explanation, ImitatorLogger.getInstance().getLogForLastSentence(), false);
	}

	protected ChoiceAndExplanation[] createPuzzleChoices(SentenceCreator sentenceCreator, int numChoices) {
		int numSectionsPerChoice = sentenceCreator.getNumberOfSectionsPerChoice();
		int numSentencesPerSection= sentenceCreator.getNumberOfSentencesPerSection();
		int numSentencesPerChoice = numSentencesPerSection*numSectionsPerChoice;

		// TODO: replace the explanations map with an LRU cache.
		if (logs.size()>1000)
			logs.clear();

		ChoiceAndExplanation[] choices = new ChoiceAndExplanation[numChoices];
		if (choices.length<=0) return choices;
		
		int iChoice = 0;
		
		// Create the true choice - N adjacent real sentences:
		choices[iChoice] = pickRandomSection(sentenceCreator, numSentencesPerChoice);
		if (choices.length<=(++iChoice)) return choices;

		// Create a false choice - N real sentences, but not adjacent: 
		choices[iChoice] = pickRandomSentences(sentenceCreator, numSentencesPerChoice);
		if (choices.length<=(++iChoice)) return choices;

		// Create a false choice - N random sentences, based on an adjacent template: 
		choices[iChoice] = createRandomSection(sentenceCreator, numSentencesPerChoice, Enums.START_FROM_BOTH);
		if (choices.length<=(++iChoice)) return choices;
		
		// Create a false choice - N random sentences, based on an non-adjacent sentences: 
		choices[iChoice] = createRandomSentences(sentenceCreator, numSentencesPerChoice, Enums.START_FROM_BOTH);
		if (choices.length<=(++iChoice)) return choices;

		return choices;
	}
	
	private ChoiceAndExplanation[] createPuzzleNoShuffleNoCache(String corpus, int numChoices)  {
		ChoiceAndExplanation[] choices = null;
		SentenceCreator sentenceCreator = getSentenceCreator(corpus);
		synchronized(sentenceCreator) {
			choices = createPuzzleChoices(sentenceCreator, numChoices);
		}
		return choices;
	}


	public ChoiceAndExplanation[] createPuzzle(String corpus, int numChoices)  {
		System.err.println("createPuzzle start "+corpus);
		ChoiceAndExplanation[] choices = null;
		choices = createPuzzleNoShuffleNoCache(corpus, numChoices);
		Collections.shuffle(Arrays.asList(choices));
		System.err.println("createPuzzle end "+corpus);
		return choices;
	}


	
	/*
	 * TEST ZONE
	 */

	public static void main(String[] args) throws FileNotFoundException, IOException {
		PuzzleCreator impl = new PuzzleCreator("corpora");
		long start = System.currentTimeMillis();
		
		// TODO: Currently doesn't work because of the memcache 
		System.out.println(Arrays.asList(impl.createPuzzle("Rambam", 5)));
		System.exit(0);
		System.out.println("first createPuzzle ShmonaKvazim took "+(System.currentTimeMillis()-start));
		start = System.currentTimeMillis();
		System.out.println(Arrays.asList(impl.createPuzzle("Rambam", 5)));
		System.out.println("first createPuzzle Rambam took "+(System.currentTimeMillis()-start));
		start = System.currentTimeMillis();
		System.out.println(Arrays.asList(impl.createPuzzle("ShmonaKvazim", 5)));
		System.out.println(Arrays.asList(impl.createPuzzle("Rambam", 5)));
		System.out.println("createPuzzle both took "+(System.currentTimeMillis()-start));

		/*
		for (int i=0; i<=4; ++i) {
			long start = System.currentTimeMillis();
			System.out.println(i+"\n"+impl.createPuzzleChoice(i));
			System.out.println("   time: "+(System.currentTimeMillis()-start)/1000+" s");
		}
		*/
		
		//for (String ex: explanations.values())
		//	System.out.println("\n\n"+ex);
	}
}
