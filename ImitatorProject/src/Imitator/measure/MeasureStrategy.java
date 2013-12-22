package Imitator.measure;

import java.util.Map;

public interface MeasureStrategy {
    public String getMessage();
    public void writeToFiles(Map<String, Map<String,Integer>> words);
    public void handleSentence(Map<String, Map<String,Integer>> word2Words, TaggedCorpusParserIfc parser);
    public void destroy();
}
