package Imitator.measure;


public class MeasureProps {

    private static String _baseDir;

    private static String _corpusName;

    private static int _minimalTemplateLength;

    private static int _minimalTemplateFrequency;
    
    private static int _minimalPOSFrequency;
    
    private static int _minimalSequenceFrequency;
    
    private static int _numberOfWordsToCompareInSuffixArray;
    

    /***********************
     * Getters and setters *
     ***********************/
    
    public static int getNumberOfWordsToCompareInSuffixArray() {
        return _numberOfWordsToCompareInSuffixArray;
    }

    public static void setNumberOfWordsToCompareInSuffixArray(int ofWordsToCompareInSuffixArray) {
        _numberOfWordsToCompareInSuffixArray = ofWordsToCompareInSuffixArray;
    }

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

    public static String getCorpusDir() {
        return _baseDir;
    }

    public static void setCorpusDir(String dir) {
        _baseDir = dir;
    }

    public static String getCorpusName() {
        return _corpusName;
    }

    public static void setCorpusName(String name) {
        _corpusName = name;
    }

    public static int getMinimalPOSFrequency() {
        return _minimalPOSFrequency;
    }

    public static void setMinimalPOSFrequency(int frequency) {
        _minimalPOSFrequency = frequency;
    }

    public static int getMinimalSequenceFrequency() {
        return _minimalSequenceFrequency;
    }

    public static void setMinimalSequenceFrequency(int sequenceFrequency) {
        _minimalSequenceFrequency = sequenceFrequency;
    }

 }
