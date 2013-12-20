package Imitator.sentenceCreator;

import java.util.Collection;

public class CreatorProps {

	/**
	 * RTL/LTR
	 */
	private static String _languageDirection; 
	private static String _outputFilePath;
	
    private static int _minimalTemplateLength;

    private static int _minimalTemplateFrequency;
    
    private static Collection<String> _positionsToCopyDirectly;
    
    private static Collection<String> _posToCopyDirectly;
    
    private static int _n;

    private static int _numberOfNgramToUseSupCorpus;
    
    private static int _numberOfNgramToReduceN;
    
    private static String posOfProperNoun;
    private static String posOfProperNounPlural;
    private static String posOfNumber;
    private static String posOfDate;
    private static String posOfHour;

    /* *********************/
    /* getters and setters */
    /* *********************/
    public static int getNumberOfNgramToUseSupCorpus() {
        return _numberOfNgramToUseSupCorpus;
    }

    public static void setNumberOfNgramToUseSupCorpus(int _k) {
        CreatorProps._numberOfNgramToUseSupCorpus = _k;
    }

    public static int getNumberOfNgramToReduceN() {
        return _numberOfNgramToReduceN;
    }

    public static void setNumberOfNgramToReduceN(int _c) {
        CreatorProps._numberOfNgramToReduceN = _c;
    }

    public static Collection<String> getPositionsToCopyDirectly() {
        return _positionsToCopyDirectly;
    }

    public static void setPositionsToCopyDirectly(Collection<String> toCopyDirectly) {
        _positionsToCopyDirectly = toCopyDirectly;
    }

    public static Collection<String> getPosToCopyDirectly() {
        return _posToCopyDirectly;
    }

    public static void setPosToCopyDirectly(Collection<String> toCopyDirectly) {
        _posToCopyDirectly = toCopyDirectly;
    }

    /***********************
     * Getters and setters *
     ***********************/
    
    public static int getMinimalTemplateLength() {
        return _minimalTemplateLength;
    }

    public static void setMinimalTemplateLength(int templateLength) {
        _minimalTemplateLength = templateLength;
    }

    public static int getMinimalTemplateFrequency() {
        return _minimalTemplateFrequency;
    }

    public static void setMinimalTemplateFrequency(int templateFrequency) {
        _minimalTemplateFrequency = templateFrequency;
    }


    public static int getN() {
        return _n;
    }

    public static void setN(int _n) {
        CreatorProps._n = _n;
    }

    public static String getPosOfProperNoun() {
        return posOfProperNoun;
    }

    public static void setPosOfProperNoun(String posOfProperNoun) {
        CreatorProps.posOfProperNoun = posOfProperNoun;
    }

    public static String getPosOfProperNounPlural() {
        return posOfProperNounPlural;
    }

    public static void setPosOfProperNounPlural(String posOfProperNounPlural) {
        CreatorProps.posOfProperNounPlural = posOfProperNounPlural;
    }

    public static String getPosOfNumber() {
        return posOfNumber;
    }

    public static void setPosOfNumber(String posOfNumber) {
        CreatorProps.posOfNumber = posOfNumber;
    }

    public static String getPosOfDate() {
        return posOfDate;
    }

    public static void setPosOfDate(String posOfDate) {
        CreatorProps.posOfDate = posOfDate;
    }

    public static String getPosOfHour() {
        return posOfHour;
    }

    public static void setPosOfHour(String posOfHour) {
        CreatorProps.posOfHour = posOfHour;
    }

	public static void setLanguage(String language) 
	{
		_languageDirection = language;
	}

	public static String getLanguage() 
	{
		return _languageDirection;
	}

	public static String getOutputFilePath() {
		return _outputFilePath;
	}

	public static void setOutputFilePath(String filePath) {
		_outputFilePath = filePath;
	}
 }
