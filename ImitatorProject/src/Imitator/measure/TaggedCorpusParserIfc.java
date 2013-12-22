package Imitator.measure;

public interface TaggedCorpusParserIfc {

    public void startFile(String filePath);
    
    public boolean hasMoreSentences();
    
    public String getNextSentence();
    
    public boolean hasMoreWordsInSentence();
    
    public String getNextWord();
    
    public String getPOS();
    
    public void finishFile();
}
