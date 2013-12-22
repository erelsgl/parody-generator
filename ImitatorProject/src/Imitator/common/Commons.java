package Imitator.common;

public class Commons {

    public static final String PRE_SENTENCE_SIGN = "@pre@";
    public static final String SECOND_PRE_SENTENCE_SIGN = "@prepre@";

    private static final String TIME = "TTIMEE";
    private static final String DATE = "DDATEE";
    private static final String NUMBER = "NNUMBERR";
    private static final String PROPER_NOUN = "PROPNOUN";
    private static final String PROPER_NOUN_PLURAL = "PROPNOUN_PL";

    public static String getKey(String word, String pos) {
	//TODO case sensitive
	return getKey(word/*.toLowerCase()*/, pos.equals("CD"), pos.equals("NNP"), pos.equals("NNPS"));
    }
    
    public static String getKey(String word, boolean isNumber, boolean isProperNoun, boolean isProperNounPlural) {
	    
	    if(isNumber){
		// check if 'word' is TIME
		int firstIndex = word.indexOf(":");
		if(firstIndex != -1){
		    return TIME;
		}
		
		// check if 'word' is Date
		firstIndex = word.indexOf("/");
		if(firstIndex != -1){
		    int secondIndex = word.indexOf("/");
		    if(secondIndex != -1){
			return DATE;
		    }
		}

		firstIndex = word.indexOf("\\");
		if(firstIndex != -1){
		    int secondIndex = word.indexOf("\\");
		    if(secondIndex != -1){
			return DATE;
		    }
		}

		firstIndex = word.indexOf("-");
		if(firstIndex != -1){
		    int secondIndex = word.indexOf("-");
		    if(secondIndex != -1){
			return DATE;
		    }
		}

		// else - 'word' is just a number
		return NUMBER;
	    }

	    if(isProperNoun){
		return PROPER_NOUN;
	    }

	    if(isProperNounPlural){
		return PROPER_NOUN_PLURAL;
	    }

	    return word;
	    //return replaceSpecialCharacters(word); 
	}

    /**
     * That method is needed because the data is store on files, and the file system cannot store files like ":.txt". 
     * The use of that method is for POS and for words.
     * For POS - called directly.
     * For word - called from the method 'getKey' that replace numbers and proper nouns into constants.
     * 
     * @param word - the POS or word to handle
     * @return the key after replacement
     */
    public static String replaceSpecialCharacters(String word) {

	return word.replace(":", "AAA");
    }

    /**
     * 
     * @param word
     * @return
     */
    public static String getRealWord(String word) {

	return word.replace("AAA", ":");
    }

}
